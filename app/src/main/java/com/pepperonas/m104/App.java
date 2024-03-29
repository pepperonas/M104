/*
 * Copyright (c) 2019 Martin Pfeffer
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

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.utils.AesConst;
import com.pepperonas.m104.utils.BatteryUtils;
import com.pepperonas.m104.utils.Log;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class App extends Application {

    @SuppressWarnings("unused")
    private static final String TAG = "App";

    //    private Tracker mTracker;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AndBasx.init(this);
        AesPrefs.init(this, AesConst.AES_PREFS_FILE_NAME, "the_apps_password", null);
        initStartConfig();
        AesPrefs.putIntRes(R.string.BATTERY_CAPACITY, (int) BatteryUtils.getBatteryCapacity());

        if (BuildConfig.is_dev) {
            Log.init(getApplicationContext());
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (AesPrefs.getBooleanRes(R.string.SHOW_NETWORK_NOTIFICATION, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(Const.CHANNEL_ID,
                        getString(R.string.notification_title_network),
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(false);
                channel.setSound(null, null);
                mNotificationManager.createNotificationChannel(channel);
            }
        } else {
            mNotificationManager.cancel(Const.NOTIFICATION_NETWORK);
        }
    }

    private void initStartConfig() {
        AesPrefs.initInstallationDate();
        AesPrefs.initOrIncrementLaunchCounter();
        AesPrefs.initOrCheckVersionCode();

        AesPrefs.initBooleanRes(R.string.IS_CHARGING, false);
    }

    //    /**
    //     * Gets the default {@link Tracker} for this {@link Application}.
    //     *
    //     * @return tracker
    //     */
    //    synchronized public Tracker getDefaultTracker() {
    //        if (mTracker == null) {
    //            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
    //            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
    //            mTracker = analytics.newTracker(R.xml.global_tracker);
    //        }
    //        return mTracker;
    //    }

}
