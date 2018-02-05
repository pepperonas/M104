/*
 * Copyright (c) 2018 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pepperonas.m104.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.andbasx.format.TimeFormatUtilsLocalized;
import com.pepperonas.jbasx.base.Binary;
import com.pepperonas.jbasx.format.TimeFormatUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.m104.MainService;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.fragments.FragmentSettings;
import com.pepperonas.m104.model.Database;
import com.pepperonas.m104.model.NetworkHistory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class NetworkDialogActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "NetworkDialogActivity";

    private static final long OFFSET_TIME_10_SEC = 10000L;

    private Database mDb;

    private LineChart mChart;

    private long mUnitDivider;

    private boolean mShowLiveChart = true;

    private boolean mDoAnimate = true;

    private BroadcastReceiver mMainServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            } catch (Exception e) {
                Log.e(TAG, "onReceive: Error while updating in live mode (is live-mode the correct config?).");
            }
            loadHistoryChart();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         *  init dialog
         * */
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * Const.RELATIVE_DIALOG_WIDTH);

        setContentView(R.layout.activity_dialog_network);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = Const.DIALOG_DIM_AMOUNT;
        getWindow().setAttributes(layoutParams);
        getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        mChart = findViewById(R.id.chart);

        if (getIntent() != null && getIntent().getBooleanExtra(Loader.gStr(R.string.NETWORK_CHART_LIVE_MODE), false)) {
            mShowLiveChart = false;
            Log.d(TAG, "onCreate: Invalid intent parameter.");
        } else {
            mShowLiveChart = true;
        }

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                finish();
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mDoAnimate = true;

        if (mShowLiveChart) {
            loadHistoryChart();
        }

        registerReceiver(mMainServiceReceiver, new IntentFilter(MainService.BROADCAST_NETWORK_INFO));
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mMainServiceReceiver);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: Receiver was not registered.");
        }

        if (mDb != null) {
            mDb.close();
        }

        super.onDestroy();
    }

    /**
     * Load history chart.
     * <p/>
     * CAUTION: in the current app-configuration the history will not work. Keep in mind that the
     * database needs to be filled in {@link MainService#mRunnableNetworkCheck}. And also mention
     * that if the network notification is disabled in {@link FragmentSettings#onPreferenceClick(Preference)},
     * {@link MainService#mRunnableNetworkCheck} won't be executed because of the return-statement
     * in {@link MainService#startRepeatingTask()}
     */
    private void loadHistoryChart() {
        Log.d(TAG, "loadHistoryChart " + "");

        TextView tvHeader = findViewById(R.id.tv_header);

        /**
         *  load network stats from database
         * */
        mDb = new Database(this);
        List<NetworkHistory> nwkStats = mDb.getNetworkHistory((System.currentTimeMillis()
                - (Const.DIALOG_NETWORK_HISTORY_IN_MINUTES * 1000 * 60)) - OFFSET_TIME_10_SEC);

        if (nwkStats.size() <= 0) {
            tvHeader.setText(getString(R.string.no_history));
            return;
        }

        /**
         *  calculate time variables
         * */
        long min = nwkStats.get(0).getStamp();
        long max = System.currentTimeMillis();
        long diff = max - min;
        int old_pDiff = 0;

        long maxTraffic = getMaxTraffic(nwkStats);

        String unit = calculateUnitByGivenMax(maxTraffic);

        /**
         *  set up {@link TextView}
         * */
        tvHeader = findViewById(R.id.tv_header);
        int minutes;
        if (diff < (AesPrefs.getIntResNoLog(R.string.DIALOG_NETWORK_CHART_HISTORY_IN_MINUTES, Const.DIALOG_NETWORK_HISTORY_IN_MINUTES) * 1000 * 60)) {
            minutes = (int) (diff / 1000) / 60;
        } else {
            minutes = AesPrefs.getIntResNoLog(R.string.DIALOG_NETWORK_CHART_HISTORY_IN_MINUTES, Const.DIALOG_NETWORK_HISTORY_IN_MINUTES);
        }
        if (minutes == 0 || minutes == 1) {
            tvHeader.setText(getString(R.string.last_minute));
        } else {
            tvHeader.setText(MessageFormat.format("{0} {1} {2}", getString(R.string.dialog_chart_header), minutes, getString(R.string.minutes)));
        }

        List<Entry> valuesRx = new ArrayList<>();
        List<Entry> valuesTx = new ArrayList<>();
        List<Entry> valuesRxMobile = new ArrayList<>();
        List<Entry> valuesTxMobile = new ArrayList<>();

        /**
         *  store battery stats in list
         * */
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < nwkStats.size(); i++) {
            double f = ((nwkStats.get(i).getStamp() - (double) min) / (double) diff) * 100d;

            int pDiff = (int) f;
            if (pDiff != old_pDiff) {
                old_pDiff = pDiff;
            } else {
                pDiff++;
            }

            int relativeX = nwkStats.size() * pDiff / 100;

            valuesRx.add(new Entry(divideByGivenUnit(maxTraffic, nwkStats.get(i).getRx()), relativeX));
            valuesTx.add(new Entry(divideByGivenUnit(maxTraffic, nwkStats.get(i).getTx()), relativeX));
            valuesRxMobile.add(new Entry(divideByGivenUnit(maxTraffic, nwkStats.get(i).getRxMobile()), relativeX));
            valuesTxMobile.add(new Entry(divideByGivenUnit(maxTraffic, nwkStats.get(i).getTxMobile()), relativeX));

            xVals.add(TimeFormatUtilsLocalized.formatTime(nwkStats.get(i).getStamp(), TimeFormatUtils.DEFAULT_FORMAT_MD_HM));
        }

        /**
         *  Rx
         * */
        LineDataSet lineDataSetRx = new LineDataSet(valuesRx, getString(R.string.rx_wlan));
        lineDataSetRx.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetRx.setLineWidth(2f);
        lineDataSetRx.setDrawCircles(false);
        lineDataSetRx.setDrawValues(false);
        lineDataSetRx.setColor(getResources().getColor(R.color.green_700));

        /**
         *  Tx
         * */
        LineDataSet lineDataSetTx = new LineDataSet(valuesTx, getString(R.string.tx_wlan));
        lineDataSetTx.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetTx.setLineWidth(2f);
        lineDataSetTx.setDrawCircles(false);
        lineDataSetTx.setDrawValues(false);
        lineDataSetTx.setColor(getResources().getColor(R.color.blue_700));

        /**
         * Rx mobile
         * */
        LineDataSet lineDataSetRxMobile = new LineDataSet(valuesRxMobile, getString(R.string.rx_mobile));
        lineDataSetRxMobile.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetRxMobile.setLineWidth(2f);
        lineDataSetRxMobile.setDrawCircles(false);
        lineDataSetRxMobile.setDrawValues(false);
        lineDataSetRxMobile.setColor(getResources().getColor(R.color.green_300));

        /**
         * Tx mobile
         * */
        LineDataSet lineDataSetTxMobile = new LineDataSet(valuesTxMobile, getString(R.string.tx_mobile));
        lineDataSetTxMobile.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetTxMobile.setLineWidth(2f);
        lineDataSetTxMobile.setDrawCircles(false);
        lineDataSetTxMobile.setDrawValues(false);
        lineDataSetTxMobile.setColor(getResources().getColor(R.color.blue_300));

        /**
         *  set data to {@link LineDataSet}
         * */
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSetRx);
        lineDataSets.add(lineDataSetTx);
        lineDataSets.add(lineDataSetRxMobile);
        lineDataSets.add(lineDataSetTxMobile);

        /**
         * X-Axis
         * */
        XAxis xAxis = mChart.getXAxis();

        /**
         * Y-Axis (left)
         * */
        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setAxisMinValue(0f);
        yAxisLeft.setAxisMaxValue(((maxTraffic * 1.2f) / mUnitDivider));
        yAxisLeft.setValueFormatter(new LargeValueFormatter(" " + unit));

        /**
         * Y-Axis (right)
         * */
        YAxis yAxisRight = mChart.getAxisRight();
        yAxisRight.setEnabled(false);

        /**
         * init chart
         * */
        LineData data = new LineData(xVals, lineDataSets);
        mChart.setData(data);
        if (mDoAnimate) {
            mDoAnimate = false;
            mChart.animateX(1000, Easing.EasingOption.EaseInQuart);
        }
        mChart.setDescription(getString(R.string.chart_description_network));
        mChart.setDrawMarkerViews(false);
        mChart.setMaxVisibleValueCount(nwkStats.size());
        mChart.setVisibleXRangeMaximum(nwkStats.size());
        mChart.invalidate();
        mChart.setHighlightPerDragEnabled(false);
        mChart.setHighlightPerTapEnabled(false);
        mChart.setDrawGridBackground(false);
    }

    /**
     * Calculate unit by given max string.
     *
     * @param maxTrafficInBytes the max traffic in bytes
     * @return the string
     */
    private String calculateUnitByGivenMax(long maxTrafficInBytes) {
        if (maxTrafficInBytes > Binary.GIGA) {
            mUnitDivider = (long) Binary.GIGA;
            return Loader.gStr(R.string._unit_gigabytes_per_second);
        } else if (maxTrafficInBytes > Binary.MEGA) {
            mUnitDivider = (long) Binary.MEGA;
            return Loader.gStr(R.string._unit_megabytes_per_second);
        } else if (maxTrafficInBytes > Binary.KILO) {
            mUnitDivider = (long) Binary.KILO;
            return Loader.gStr(R.string._unit_kilobytes_per_second);
        } else {
            mUnitDivider = 1;
            return Loader.gStr(R.string._unit_bytes_per_second);
        }
    }

    /**
     * Divide by given unit float.
     *
     * @param maxTrafficInBytes the max traffic in bytes
     * @param toResolve         the value to recalculate
     * @return the float
     */
    private float divideByGivenUnit(long maxTrafficInBytes, long toResolve) {
        if (maxTrafficInBytes > Binary.GIGA) {
            return (float) toResolve / (float) Binary.GIGA;
        } else if (maxTrafficInBytes > Binary.MEGA) {
            return (float) toResolve / (float) Binary.MEGA;
        } else if (maxTrafficInBytes != 0) {
            return (float) toResolve / (float) Binary.KILO;
        } else {
            return toResolve;
        }
    }

    /**
     * Gets max traffic.
     *
     * @param nwkStats the nwk stats
     * @return the max traffic
     */
    private long getMaxTraffic(List<NetworkHistory> nwkStats) {
        long max = 0;
        int i = 0, nwkStatsSize = nwkStats.size();
        while (i < nwkStatsSize) {
            NetworkHistory nwh = nwkStats.get(i);
            max = nwh.getRx() > max ? nwh.getRx() : max;
            max = nwh.getTx() > max ? nwh.getTx() : max;
            max = nwh.getRxMobile() > max ? nwh.getRxMobile() : max;
            max = nwh.getTxMobile() > max ? nwh.getTxMobile() : max;
            i++;
        }
        return max;
    }

}
