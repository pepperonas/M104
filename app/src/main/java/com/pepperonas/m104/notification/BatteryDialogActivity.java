/*
 * Copyright (c) 2016 Martin Pfeffer
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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.format.TimeFormatUtilsLocalized;
import com.pepperonas.jbasx.format.TimeFormatUtils;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.model.BatteryStat;
import com.pepperonas.m104.model.Database;
import com.pepperonas.m104.utils.Calculations;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class BatteryDialogActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "BatteryDialogActivity";

    private Database mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         *  init dialog
         * */
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * Const.RELATIVE_DIALOG_WIDTH);

        setContentView(R.layout.activity_dialog_battery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = Const.DIALOG_DIM_AMOUNT;
        getWindow().setAttributes(layoutParams);
        getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        mDb = new Database(this);

        loadChart();

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                finish();
                return true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        mDb.close();

        super.onDestroy();
    }


    private void loadChart() {
        /**
         *  load battery stats from database
         * */
        final List<BatteryStat> btyStats = mDb.getBatteryStatsByDate(
                Calculations.getHours(AesPrefs.getIntRes(R.string.DIALOG_CHART_HISTORY_HOURS, Const.DEFAULT_RANGE_IN_HOURS)));

        /**
         *  calculate time variables
         * */
        long min = btyStats.get(0).getStamp();
        long max = btyStats.get(btyStats.size() - 1).getStamp();
        long diff = max - min;
        int old_pDiff = 0;

        /**
         *  set up {@link TextView}
         * */
        TextView tvHeader = (TextView) findViewById(R.id.tv_header);
        int hours;
        if (diff < (AesPrefs.getIntRes(R.string.DIALOG_CHART_HISTORY_HOURS, Const.DEFAULT_RANGE_IN_HOURS) * 1000 * 60 * 60)) {
            hours = (int) (diff / 1000) / 3600;
        } else hours = AesPrefs.getIntRes(R.string.DIALOG_CHART_HISTORY_HOURS, Const.DEFAULT_RANGE_IN_HOURS);
        if (hours == 0 || hours == 1) {
            tvHeader.setText(getString(R.string.last_hour));
        } else tvHeader.setText(MessageFormat.format("{0} {1} {2}", getString(R.string.dialog_chart_header),
                                                     hours, getString(R.string.hours)));

        List<Entry> valuesLvl = new ArrayList<>();
        List<Entry> valuesTemperature = new ArrayList<>();
        List<Entry> valuesScreenState = new ArrayList<>();

        /**
         *  store battery stats in list
         * */
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < btyStats.size(); i++) {
            double f = ((btyStats.get(i).getStamp() - (double) min) / (double) diff) * 100d;

            int pDiff = (int) f;
            if (pDiff != old_pDiff) {
                old_pDiff = pDiff;
            } else pDiff++;

            int relativeX = btyStats.size() * pDiff / 100;

            valuesLvl.add(new Entry(btyStats.get(i).getLevel(), relativeX));
            valuesTemperature.add(new Entry(btyStats.get(i).getTemperature(), relativeX));
            valuesScreenState.add(new Entry((btyStats.get(i).isScreenOn() ? 100000f : -100f), relativeX));

            xVals.add(TimeFormatUtilsLocalized.formatTime(btyStats.get(i).getStamp(), TimeFormatUtils.DEFAULT_FORMAT_MD_HM));
        }

        /**
         *  Level
         * */
        LineDataSet lineDataSetLvl = new LineDataSet(valuesLvl, getString(R.string.chart_legend_level));
        lineDataSetLvl.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetLvl.setLineWidth(2f);
        lineDataSetLvl.setDrawCircles(false);
        lineDataSetLvl.setDrawValues(false);
        lineDataSetLvl.setColor(getResources().getColor(R.color.green_500));

        /**
         *  Screen state
         * */
        LineDataSet lineDataSetScreenState = new LineDataSet(valuesScreenState, getString(R.string.chart_legend_screen_state));
        lineDataSetScreenState.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetScreenState.setLineWidth(0f);
        lineDataSetScreenState.setDrawCircles(false);
        lineDataSetScreenState.setDrawFilled(true);
        lineDataSetScreenState.setDrawValues(false);
        lineDataSetScreenState.setColor(getResources().getColor(R.color.amber_200));
        lineDataSetScreenState.setFillColor(getResources().getColor(R.color.amber_200));

        /**
         * Temperature
         * */
        LineDataSet lineDataSetTemperature = new LineDataSet(valuesTemperature, getString(R.string.chart_legend_temperature));
        lineDataSetTemperature.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSetTemperature.setLineWidth(2f);
        lineDataSetTemperature.setDrawCircles(false);
        lineDataSetTemperature.setDrawValues(false);
        lineDataSetTemperature.setColor(getResources().getColor(R.color.deep_orange_500));

        /**
         *  set data to {@link LineDataSet}
         * */
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(lineDataSetScreenState);
        lineDataSets.add(lineDataSetLvl);
        lineDataSets.add(lineDataSetTemperature);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        /**
         * X-Axis
         * */
        XAxis xAxis = chart.getXAxis();

        /**
         * Y-Axis (left)
         * */
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setValueFormatter(new PercentFormatter(new DecimalFormat("0.#")));
        yAxisLeft.setAxisMinValue(0f);
        yAxisLeft.setAxisMaxValue(100f);

        /**
         * Y-Axis (right)
         * */
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setAxisMinValue(0f);
        yAxisRight.setAxisMaxValue(60f);
        yAxisRight.setValueFormatter(new LargeValueFormatter(" °C"));

        /**
         * init chart
         * */
        LineData data = new LineData(xVals, lineDataSets);
        chart.setData(data);
        chart.setDescription(getString(R.string.chart_description_battery));
        chart.setDrawMarkerViews(false);
        chart.setMaxVisibleValueCount(btyStats.size());
        chart.setVisibleXRangeMaximum(btyStats.size());
        chart.invalidate();
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);

    }

}
