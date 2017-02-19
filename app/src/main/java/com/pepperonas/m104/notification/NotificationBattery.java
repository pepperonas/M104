/*
 * Copyright (c) 2017 Martin Pfeffer
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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.utils.BatteryUtils;
import com.pepperonas.m104.utils.StringFactory;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class NotificationBattery {

    private static final String TAG = "NotificationBattery";

    public static final String EXTRA_START_BATTERY = "bty";

    private Context mCtx;

    private static NotificationManager mNotificationManager;

    private static NotificationCompat.Builder mBuilder;

    private final RemoteViews mRemoteViews;


    /**
     * Instantiates a new Notification panel.
     *
     * @param context the context
     */
    public NotificationBattery(Context context) {
        this.mCtx = context;

        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title_battery))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view_battery);

        initBatteryNotificationIntent();

        mBuilder.setContent(mRemoteViews);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (AesPrefs.getBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, true)) {
            mNotificationManager.notify(Const.NOTIFICATION_BATTERY, mBuilder.build());
        }
    }


    /**
     * Handle interaction in {@link BatteryDialogActivity}.
     */
    public void initBatteryNotificationIntent() {
        Intent chartIntent = new Intent(mCtx, BatteryDialogActivity.class);

        chartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                             | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, chartIntent, 0);

        mRemoteViews.setOnClickPendingIntent(R.id.notification_container, pendingIntent);

        // launch main when circle is clicked
        Intent launch = new Intent(mCtx, MainActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        launch.putExtra("start_fragment", EXTRA_START_BATTERY);

        /**
         * Important: set {@link PendingIntent.FLAG_UPDATE_CURRENT}
         * */
        PendingIntent btnLaunch = PendingIntent.getActivity(mCtx, Const.NOTIFICATION_BATTERY, launch, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notification_circle_left, btnLaunch);
    }


    /**
     * Update the notification, which is shown in {@link R.layout#notification_view_network}.
     *
     * @param level      the level
     * @param temp       the makeTemperatureInfo
     * @param isCharging the is charging
     */
    public void update(int level, double temp, boolean isCharging) {

        int imageResourceId;
        if (level == -1) {
            imageResourceId = Loader.resolveDrawableId("_0");
        } else imageResourceId = Loader.resolveDrawableId("_" + String.valueOf(level));

        mBuilder.setSmallIcon(imageResourceId)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        mRemoteViews.setTextViewText(R.id.tv_notification_circle_value, StringFactory.makeLevelInfo(mCtx, level));
        mRemoteViews.setTextViewText(R.id.tv_m_notification_center_top, StringFactory.makeRemainingInfo(mCtx, level, isCharging));
        mRemoteViews.setTextViewText(R.id.tv_s_notification_center_bottom, StringFactory.makePercentagePerHourInfo(mCtx, isCharging));
        mRemoteViews.setTextViewText(R.id.tv_s_notification_right_top, StringFactory.makeTemperatureInfo(mCtx, temp));
        mRemoteViews.setTextViewText(R.id.tv_s_notification_right_bottom, StringFactory.makeRelative_mAhInfo(mCtx, BatteryUtils.getCharge()));

        if (AesPrefs.getBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, true)) {
            Log.i(TAG, "---UPDATE---");
            mNotificationManager.notify(Const.NOTIFICATION_BATTERY, mBuilder.build());
        } else {
            mNotificationManager.cancel(Const.NOTIFICATION_BATTERY);
        }

    }


    /**
     * Update set when, so the notifications will be forced to be shown in the correct order.
     */
    public static void updateSetWhen() {
        Log.i(TAG, "---UPDATE (set when)---");

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) AndBasx.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(Const.NOTIFICATION_BATTERY);

        if (mBuilder != null) {
            mBuilder.setWhen(System.currentTimeMillis());
        } else Log.w(TAG, "updateSetWhen " + "Error refreshing notification");
    }
}
