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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.utils.BatteryUtils;
import com.pepperonas.m104.utils.Log;
import com.pepperonas.m104.utils.StringFactory;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class NotificationBattery {

    private static final String TAG = "NotificationBattery";

    public static final String EXTRA_START_BATTERY = "bty";
    private static final String CHANNEL_ID = "com.pepperonas.m104.notification";
    private static final String GROUP = "g";

    private Context mCtx;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private final RemoteViews mRemoteViews;

    /**
     * Instantiates a new Notification panel.
     *
     * @param context the context
     */
    public NotificationBattery(Context context) {
        this.mCtx = context;

        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title_battery))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis() + 500)
                .setGroup(GROUP)
                .setOngoing(true);

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view_battery);

        initBatteryNotificationIntent();

        mBuilder.setContent(mRemoteViews);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (AesPrefs.getBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_title_battery),
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setShowBadge(false);
                channel.setSound(null, null);
                mNotificationManager.createNotificationChannel(channel);
            }
        } else {
            mNotificationManager.cancel(Const.NOTIFICATION_CLIPBOARD);
        }
    }

    /**
     * Handle interaction in {@link BatteryDialogActivity}.
     */
    private void initBatteryNotificationIntent() {
        Intent chartIntent = new Intent(mCtx, BatteryDialogActivity.class);

        chartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, chartIntent, 0);

        mRemoteViews.setOnClickPendingIntent(R.id.notification_container, pendingIntent);

        // launch main when circle is clicked
        Intent launch = new Intent(mCtx, MainActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        launch.putExtra("start_fragment", EXTRA_START_BATTERY);

        // Important: set PendingIntent.FLAG_UPDATE_CURRENT
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
        String uri = "@drawable/";
        if (level == -1) {
            imageResourceId = mCtx.getResources().getIdentifier(uri + "_0", null, mCtx.getPackageName());
        } else {
            imageResourceId = mCtx.getResources().getIdentifier(uri + "_" + String.valueOf(level), null, mCtx.getPackageName());
        }

        mBuilder.setSmallIcon(imageResourceId);

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

    public void removeIfCanceled() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) AndBasx.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (AesPrefs.getBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, true)) {
            mNotificationManager.notify(Const.NOTIFICATION_BATTERY, mBuilder.build());
        } else {
            mNotificationManager.cancel(Const.NOTIFICATION_BATTERY);
        }
    }

    public Notification getNotification() {
        return mBuilder.build();
    }
}
