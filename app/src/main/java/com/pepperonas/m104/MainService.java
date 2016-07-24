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

package com.pepperonas.m104;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.system.NetworkUtils;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.jbasx.math.ConvertUtils;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.model.BatteryStat;
import com.pepperonas.m104.model.ChargeMode;
import com.pepperonas.m104.model.ClipDataAdvanced;
import com.pepperonas.m104.model.Database;
import com.pepperonas.m104.model.NotificationOrder;
import com.pepperonas.m104.notification.NetworkDialogActivity;
import com.pepperonas.m104.notification.NotificationBattery;
import com.pepperonas.m104.notification.NotificationClipboard;
import com.pepperonas.m104.notification.NotificationNetwork;
import com.pepperonas.m104.utils.Calculations;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class MainService extends Service {

    private static final String TAG = "MainService";

    public static final String BROADCAST_BATTERY_INFO = "broadcast.battery.info";
    public static final String BROADCAST_NETWORK_INFO = "broadcast.network.info";
    public static final String BROADCAST_MAIN_STARTED = "broadcast.main.started";
    public static final String BROADCAST_CLIP_DELETED = "broadcast.clip.deleted";

    /**
     * Android 4.4 throws a {@link java.lang.SecurityException}
     * "gps" location provider requires ACCESS_FINE_LOCATION permission.
     * Feature is disabled now.
     */
    public static final boolean GPS_BOOLEAN_DISABLED = false;

    private int mLevel = Const.VALUE_UNSET;

    private NotificationBattery mNotificationBattery;
    private NotificationNetwork mNotificationNetwork;
    private NotificationClipboard mNotificationClipboard;

    private boolean mIsFirstRun = true;

    private Database mDb;

    /**
     * Battery
     */
    private boolean mIsCharging;
    private double mTemperature;
    private int mStatus;
    private int mPlugged;
    private int mHealth;
    private int mVoltage;

    /**
     * Network (internet speeds)
     * NOTE: these values represent all bytes!
     * Do NOT use them to show the current speed.
     */
    private long mTmpLastRx = 0L;
    private long mTmpLastTx = 0L;
    private long mTmpLastRxMobile = 0L;
    private long mTmpLastTxMobile = 0L;

    private final Handler mHandler = new Handler();
    private boolean mNetworkCheckerRunning = false;

    private Tracker mTracker;

    private boolean mIsScreenOn = true;

    /**
     * Receiver to react when battery changes.
     */
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {

            mLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, Const.VALUE_UNSET);
            mTemperature = getTemperature(intent);
            mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, Const.VALUE_UNSET);

            mPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, Const.VALUE_UNSET);
            mHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, Const.VALUE_UNSET);
            mVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, Const.VALUE_UNSET);

            //            mNotificationBattery.update(mLevel, mTemperature, mIsCharging);


            mIsCharging = mStatus == BatteryManager.BATTERY_STATUS_CHARGING;
            ChargeMode cm = ChargeMode.getInstance(mPlugged);

            if (mDb != null) {
                long tsBatteryStat = mDb.addBatteryStat(new BatteryStat(
                        System.currentTimeMillis(),
                        mIsCharging,
                        cm,
                        mStatus == BatteryManager.BATTERY_STATUS_FULL,
                        mLevel,
                        (float) mTemperature,
                        (float) mVoltage,
                        SystemUtils.isScreenOn(),
                        SystemUtils.getSystemScreenBrightness(),
                        SystemUtils.isWifiEnabled(),
                        NetworkUtils.isWifiConnected(),
                        GPS_BOOLEAN_DISABLED,
                        GPS_BOOLEAN_DISABLED,
                        GPS_BOOLEAN_DISABLED,
                        SystemUtils.isMasterSyncEnabled(),
                        SystemUtils.isAirplaneModeEnabled(),
                        (int) Calculations.getRemainingCapacity(mLevel)
                ));

                AesPrefs.putLongRes(R.string.LAST_BATTERY_STAT, tsBatteryStat);
            }


            ensurePercentResolvable();


            /**
             * On state changed.
             * */
            if (AesPrefs.getBooleanResNoLog(R.string.IS_CHARGING, false) != mIsCharging) {
                // state changed, store it...
                AesPrefs.putBooleanRes(R.string.IS_CHARGING, mIsCharging);

                onStateChanged(mIsCharging);
            }

            estimateBatteryLife(mIsCharging);

            ensureStoreMaxTemperature(mTemperature);

            sendBatteryBroadcast();

        } // end of onReceive()


        private void ensurePercentResolvable() {
            if (AesPrefs.getIntResNoLog(R.string.LAST_CHARGED_LEVEL, Integer.MIN_VALUE) == Integer.MIN_VALUE) {
                AesPrefs.putLongRes(R.string.LAST_CHARGED_STAMP, System.currentTimeMillis());
                AesPrefs.putIntRes(R.string.LAST_CHARGED_LEVEL, mLevel);
            }
            if (AesPrefs.getIntResNoLog(R.string.LAST_DISCHARGED_LEVEL, Integer.MIN_VALUE) == Integer.MIN_VALUE) {
                AesPrefs.putLongRes(R.string.LAST_DISCHARGED_STAMP, System.currentTimeMillis());
                AesPrefs.putIntRes(R.string.LAST_DISCHARGED_LEVEL, mLevel);
            }
        }


        private void estimateBatteryLife(boolean isCharging) {
            if (!isCharging) {
                estimateOnDischarging();
            } else {
                estimateOnCharging();
            }
        }


        private void estimateOnDischarging() {

            int lvlDiff = AesPrefs.getIntResNoLog(R.string.LAST_CHARGED_LEVEL, 0) - mLevel;

            long timeDiff = System.currentTimeMillis() - AesPrefs.getLongResNoLog(R.string.LAST_CHARGED_STAMP, -1);
            if (timeDiff <= 0
                || lvlDiff <= 0) {
                Log.w(TAG, "estimateOnDischarging " + "MAD VALUES! Return...");
                return;
            }

            float consumptionPerHour = (float) ConvertUtils.hourToMillisecond(lvlDiff) / (float) (timeDiff);

            AesPrefs.putFloatRes(R.string.CYCLIC_CONSUMPTION_PER_HOUR, consumptionPerHour);

        }


        private void estimateOnCharging() {

            int lvlDiff = mLevel - AesPrefs.getIntRes(R.string.LAST_DISCHARGED_LEVEL, 0);

            long timeDiff = System.currentTimeMillis() - AesPrefs.getLongRes(R.string.LAST_DISCHARGED_STAMP, -1);
            if (timeDiff <= 0
                || lvlDiff <= 0) {
                Log.w(TAG, "estimateOnCharging " + "MAD VALUES! Return...");
                return;
            }

            float loadPerHour = (float) ConvertUtils.hourToMillisecond(lvlDiff) / (float) (timeDiff);

            AesPrefs.putFloatRes(R.string.CYCLIC_CHARGE_PER_HOUR, loadPerHour);

        }


        private void onStateChanged(boolean isCharging) {
            Log.i(TAG, "onReceive ---STATE CHANGED---");

            mDb.addChargeState(System.currentTimeMillis(), AesPrefs.getLongResNoLog(R.string.LAST_BATTERY_STAT, 0), isCharging);

            AesPrefs.putDoubleRes(R.string.CYCLIC_MAX_TEMP_VALUE, 0);

            resetCurrentScreenTracker();

            if (!isCharging) {
                AesPrefs.putLongRes(R.string.LAST_UNPLUGGED, System.currentTimeMillis());

                AesPrefs.putIntRes(R.string.LAST_CHARGED_LEVEL, mLevel);
                AesPrefs.putLongRes(R.string.LAST_CHARGED_STAMP, System.currentTimeMillis());
            } else {
                AesPrefs.putIntRes(R.string.LAST_DISCHARGED_LEVEL, mLevel);
                AesPrefs.putLongRes(R.string.LAST_DISCHARGED_STAMP, System.currentTimeMillis());
            }

        }


        private double getTemperature(Intent intent) {
            int result = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Const.VALUE_UNSET);
            return result != Const.VALUE_UNSET ? (result / 10d) : Const.VALUE_UNSET;
        }
    };


    /**
     * Send values by broadcast.
     */
    private void sendBatteryBroadcast() {
        Intent bcI = new Intent(BROADCAST_BATTERY_INFO);
        bcI.putExtra("is_charging", mIsCharging);
        bcI.putExtra("level", mLevel);
        bcI.putExtra("temperature", mTemperature);
        bcI.putExtra("voltage", mVoltage);
        bcI.putExtra("plugged", mPlugged);
        bcI.putExtra("health", mHealth);
        bcI.putExtra("status", mStatus);
        sendBroadcast(bcI);

        // and update battery notification
        mNotificationBattery.update(mLevel, mTemperature, mIsCharging);

    }


    /**
     * Send values by broadcast.
     *
     * @param rx  the rx
     * @param tx  the tx
     * @param rxm the rxm
     * @param txm the txm
     */
    private void sendNetworkBroadcast(long rx, long tx, long rxm, long txm) {
        Intent bcI = new Intent(BROADCAST_NETWORK_INFO);
        bcI.putExtra("rx", rx);
        bcI.putExtra("tx", tx);
        bcI.putExtra("rxm", rxm);
        bcI.putExtra("txm", txm);
        sendBroadcast(bcI);

        // and update battery notification
        //        mNotificationBattery.update(mLevel, mTemperature, mIsCharging);

    }


    /**
     * Update clipboard count.
     */
    private void updateClipboardCount() {
        mNotificationClipboard.update(mDb.getClipDataCount());
    }


    /**
     * Ensure store max temperature.
     *
     * @param temperature the temperature
     */
    private void ensureStoreMaxTemperature(double temperature) {
        if (AesPrefs.getDoubleResNoLog(R.string.CYCLIC_MAX_TEMP_VALUE, 0) < temperature) {
            AesPrefs.putDoubleRes(R.string.CYCLIC_MAX_TEMP_VALUE, temperature);
        }
        if (AesPrefs.getDoubleResNoLog(R.string.GLOBAL_MAX_TEMP_VALUE, 0) < temperature) {
            AesPrefs.putDoubleRes(R.string.GLOBAL_MAX_TEMP_VALUE, temperature);
        }
    }


    /**
     * Reset current screen tracker.
     */
    private void resetCurrentScreenTracker() {
        AesPrefs.putLongRes(R.string.CYCLIC_SCREEN_ON_VALUE, 0);
        AesPrefs.putLongRes(R.string.CYCLIC_SCREEN_OFF_VALUE, 0);
    }


    /**
     * Receiver to react when the screen turns on.
     */
    private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            sendBatteryBroadcast();

            mDb.addScreenState(System.currentTimeMillis(), AesPrefs.getLongResNoLog(R.string.LAST_BATTERY_STAT, 0), true);

            mIsScreenOn = true;

            trackOnScreenOn();

            startRepeatingTask();
        }
    };


    /**
     * Track on screen on.
     */
    private void trackOnScreenOn() {
        long trackedScreenOff = AesPrefs.getLongResNoLog(R.string.SCREEN_TRACKER_OFF, System.currentTimeMillis());
        long screenOff = System.currentTimeMillis() - trackedScreenOff;

        Log.d(TAG, "trackOnScreenOn screen was off for " + screenOff / 1000 + " s.");

        AesPrefs.putLongRes(R.string.CYCLIC_SCREEN_OFF_VALUE, AesPrefs.getLongResNoLog(R.string.CYCLIC_SCREEN_OFF_VALUE, 0) + screenOff);
        AesPrefs.putLongRes(R.string.GLOBAL_SCREEN_OFF_VALUE, AesPrefs.getLongResNoLog(R.string.GLOBAL_SCREEN_OFF_VALUE, 0) + screenOff);

        AesPrefs.putLongRes(R.string.SCREEN_TRACKER_ON, System.currentTimeMillis());

    }


    /**
     * Receiver to react when the screen turns off.
     */
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            mDb.addScreenState(System.currentTimeMillis(), AesPrefs.getLongResNoLog(R.string.LAST_BATTERY_STAT, 0), false);

            mIsScreenOn = false;

            trackOnScreenOff();

            sendBatteryBroadcast();

            stopRepeatingTask();
        }
    };


    /**
     * Track on screen off.
     */
    private void trackOnScreenOff() {
        long trackedScreenOn = AesPrefs.getLongResNoLog(R.string.SCREEN_TRACKER_ON, System.currentTimeMillis());
        long screenOn = System.currentTimeMillis() - trackedScreenOn;

        Log.d(TAG, "trackOnScreenOff screen was on for " + screenOn / 1000 + " s.");

        AesPrefs.putLongRes(R.string.CYCLIC_SCREEN_ON_VALUE, AesPrefs.getLongResNoLog(R.string.CYCLIC_SCREEN_ON_VALUE, 0) + screenOn);
        AesPrefs.putLongRes(R.string.GLOBAL_SCREEN_ON_VALUE, AesPrefs.getLongResNoLog(R.string.GLOBAL_SCREEN_ON_VALUE, 0) + screenOn);

        AesPrefs.putLongRes(R.string.SCREEN_TRACKER_OFF, System.currentTimeMillis());
    }


    /**
     * Receiver to react when activity gets started.
     */
    private BroadcastReceiver mActivityStartedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            Log.d(TAG, "onReceive activity started");
            sendBatteryBroadcast();

            startRepeatingTask();
        }
    };

    /**
     * Receiver to update notification when a clip is deleted.
     */
    private BroadcastReceiver mClipDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            Log.d(TAG, "onReceive clip deleted");

            updateClipboardCount();
        }
    };

    private final Runnable mRunnableNetworkCheck = new Runnable() {
        @Override
        public void run() {

            int uprateInSeconds = AesPrefs.getIntResNoLog(R.string.CONNECTION_MEASUREMENT_INTERVAL, Const.DEFAULT_NWK_RECORD_INTERVAL);

            // get the difference to get the current speed
            long rx_ivl = (long) ((TrafficStats.getTotalRxBytes() - mTmpLastRx) / (float) uprateInSeconds);
            long tx_ivl = (long) ((TrafficStats.getTotalTxBytes() - mTmpLastTx) / (float) uprateInSeconds);
            long rxm_ivl = (long) ((TrafficStats.getMobileRxBytes() - mTmpLastRxMobile) / (float) uprateInSeconds);
            long txm_ivl = (long) ((TrafficStats.getMobileTxBytes() - mTmpLastTxMobile) / (float) uprateInSeconds);

            if (mIsFirstRun) {
                mIsFirstRun = false;
                rx_ivl = 0;
                tx_ivl = 0;
                rxm_ivl = 0;
                txm_ivl = 0;
            }

            sendNetworkBroadcast(rx_ivl, tx_ivl, rxm_ivl, txm_ivl);

            mTmpLastRx = TrafficStats.getTotalRxBytes();
            mTmpLastTx = TrafficStats.getTotalTxBytes();
            mTmpLastRxMobile = TrafficStats.getMobileRxBytes();
            mTmpLastTxMobile = TrafficStats.getMobileTxBytes();

            mNotificationNetwork.update(
                    TrafficStats.getTotalRxBytes(), TrafficStats.getTotalTxBytes(),
                    TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes(),
                    rx_ivl, tx_ivl,
                    SystemUtils.getNetworkType());


            /**
             * Required for {@link NetworkDialogActivity#loadHistoryChart} - currently not processed.
             * */
            try {
                mDb.addNetworkStat(System.currentTimeMillis(), rx_ivl, tx_ivl, rxm_ivl, txm_ivl, "x");
            } catch (Exception e) {
                Log.e(TAG, "Writing in database failed.");
            }

            mHandler.postDelayed(mRunnableNetworkCheck, (long) (uprateInSeconds * 1000));
        }
    };


    /**
     * Instantiates a new Notification service.
     */
    public MainService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (!AesPrefs.getBooleanRes(R.string.IS_PREMIUM, false) && AesPrefs.getBooleanRes(R.string.TEST_PHASE_EXPIRED, false)) {
            stopSelf();
            return;
        }

        Log.d(TAG, "onCreate " + "");

        mDb = new Database(getApplicationContext());

        mDb.cleanBatteryStats();

        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(new ClipboardListener());

        mNotificationBattery = new NotificationBattery(getApplicationContext());

        mNotificationNetwork = new NotificationNetwork(getApplicationContext());

        mNotificationClipboard = new NotificationClipboard(getApplicationContext(), mDb.getClipDataCount());

        registerReceiver();

        startRepeatingTask();

        initAnalytics();
    }


    @Override
    public void onDestroy() {
        if (mDb != null) {
            mDb.close();
        }

        //        try {
        //            unregisterReceiver(mActivityStartedReceiver);
        //            unregisterReceiver(mBatteryInfoReceiver);
        //            unregisterReceiver(mClipDeletedReceiver);
        //            unregisterReceiver(mScreenOffReceiver);
        //            unregisterReceiver(mScreenOffReceiver);
        //        } catch (Exception e) {
        //            Log.e(TAG, "onDestroy: Unregister receivers.");
        //        }

        doAnalyticsOnLifecycle("onDestroy");

        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand " + "");

        try {
            NotificationOrder.checkInit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NotificationOrder.callOrderedUpdate();

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Register receiver.
     * <p/>
     * Listen to events, which concern
     * - the battery
     * - screen switches state (on/off)
     * - {@link MainActivity} gets started
     * - the clipboard
     */
    private void registerReceiver() {
        registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mScreenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        registerReceiver(mActivityStartedReceiver, new IntentFilter(BROADCAST_MAIN_STARTED));

        registerReceiver(mClipDeletedReceiver, new IntentFilter(BROADCAST_CLIP_DELETED));
    }


    /**
     * Start repeating task.
     */
    private void startRepeatingTask() {
        // don't updateSetWhen repeating task if notification is disabled
        if (!AesPrefs.getBoolean(getString(R.string.SHOW_NETWORK_NOTIFICATION), true)) {
            Log.w(TAG, "startRepeatingTask ISSUE?!");
            return;
        }

        if (mNetworkCheckerRunning) {
            Log.w(TAG, "startRepeatingTask " + "network-traffic is already observed.");
            return;
        }
        mNetworkCheckerRunning = true;

        Log.d(TAG, "startRepeatingTask " + "");
        mIsFirstRun = true;
        mRunnableNetworkCheck.run();
    }


    /**
     * Stop repeating task.
     */
    private void stopRepeatingTask() {
        mNetworkCheckerRunning = false;

        Log.d(TAG, "stopRepeatingTask");
        mHandler.removeCallbacks(mRunnableNetworkCheck);
    }


    class ClipboardListener implements ClipboardManager.OnPrimaryClipChangedListener {

        private long mLastAddedClip = 0;
        private long ENSURE_1000MS = 1000;


        public void onPrimaryClipChanged() {

            if (!AesPrefs.getBooleanRes(R.string.SHOW_CLIPBOARD_NOTIFICATION, true)) return;

            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData cd = clipboardManager.getPrimaryClip();
                ClipData.Item item = cd.getItemAt(0);
                String text = item.getText().toString();

                Log.d(TAG, "onPrimaryClipChanged " + text);

                if ((System.currentTimeMillis() - mLastAddedClip) > ENSURE_1000MS) {
                    mLastAddedClip = System.currentTimeMillis();

                    mDb.addClipData(ClipDataAdvanced.TYPE_DEFAULT, text, System.currentTimeMillis());
                }

                mNotificationClipboard.update(mDb.getClipDataCount());

            } catch (Exception e) {
                Log.d(TAG, "onPrimaryClipChanged " + "Error while getting clip-data");
            }
        }
    }


    private void initAnalytics() {
        if (!AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true)) return;

        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        if (mTracker != null) {
            mTracker.send(new HitBuilders.EventBuilder("Service", "start").setLabel("onCreate").build());
        }
    }


    private void doAnalyticsOnLifecycle(String method) {
        if (!AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true) || mTracker == null) return;

        mTracker.send(new HitBuilders.EventBuilder()
                              .setCategory("Service")
                              .setLabel(method).build());
    }

}

