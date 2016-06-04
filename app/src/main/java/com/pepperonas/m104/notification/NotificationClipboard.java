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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.DrawableUtils;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class NotificationClipboard {

    private static final String TAG = "NotificationClipboard";

    public static final String EXTRA_START_CLIPBOARD = "cbd";

    private Context mCtx;

    private static NotificationManager mNotificationManager;

    private static NotificationCompat.Builder mBuilder;

    private final RemoteViews mRemoteViews;


    /**
     * Instantiates a new Notification panel.
     *
     * @param context       the context
     * @param clipDataCount the clip data count
     */
    public NotificationClipboard(Context context, int clipDataCount) {
        this.mCtx = context;

        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title_battery))
                .setSmallIcon(R.drawable.ic_attachment_white_24dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);

        mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view_clipboard);

        Drawable d = Loader.getDrawable(R.drawable.ic_attachment_white_24dp);
        Bitmap bm = DrawableUtils.toBitmap(d);

        mRemoteViews.setImageViewBitmap(R.id.iv_icon, bm);

        mRemoteViews.setTextViewText(R.id.tv_s_notification_center_bottom, makeClipDataCountInfo(clipDataCount));

        initClipboardNotificationIntent();

        mBuilder.setContent(mRemoteViews);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (AesPrefs.getBoolean(mCtx.getString(R.string.SHOW_CLIPBOARD_NOTIFICATION), true)) {
            mNotificationManager.notify(Const.NOTIFICATION_CLIPBOARD, mBuilder.build());
        } else {
            mNotificationManager.cancel(Const.NOTIFICATION_CLIPBOARD);
        }

    }


    /**
     * Make clip data count info char sequence.
     *
     * @param clipDataCount the clip data count
     * @return the char sequence
     */
    private CharSequence makeClipDataCountInfo(int clipDataCount) {
        if (clipDataCount == 0) return mCtx.getString(R.string.no_entries);
        if (clipDataCount == 1) return mCtx.getString(R.string.one_entry);

        return clipDataCount + " " + mCtx.getString(R.string.entries);
    }


    /**
     * Init clipboard notification intent.
     */
    public void initClipboardNotificationIntent() {
        Intent clipboardIntent = new Intent(mCtx, ClipboardDialogActivity.class);

        clipboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                 | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, clipboardIntent, 0);

        mRemoteViews.setOnClickPendingIntent(R.id.notification_container, pendingIntent);

        // launch main when circle is clicked
        Intent launch = new Intent(mCtx, MainActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        launch.putExtra("start_fragment", EXTRA_START_CLIPBOARD);

        /**
         * Important: set {@link PendingIntent.FLAG_UPDATE_CURRENT}
         * */
        PendingIntent btnLaunch = PendingIntent.getActivity(mCtx, Const.NOTIFICATION_CLIPBOARD, launch, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notification_circle_left, btnLaunch);
    }


    /**
     * Update.
     *
     * @param clipDataCount the clip data count
     */
    public void update(int clipDataCount) {
        mBuilder.setSmallIcon(R.drawable.ic_attachment_white_24dp)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true);
        mRemoteViews.setTextViewText(R.id.tv_s_notification_center_bottom, makeClipDataCountInfo(clipDataCount));

        if (AesPrefs.getBooleanRes(R.string.SHOW_CLIPBOARD_NOTIFICATION, true)) {
            Log.i(TAG, "---UPDATE---");
            mNotificationManager.notify(Const.NOTIFICATION_CLIPBOARD, mBuilder.build());
        } else {
            mNotificationManager.cancel(Const.NOTIFICATION_CLIPBOARD);
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
        mNotificationManager.cancel(Const.NOTIFICATION_CLIPBOARD);

        if (mBuilder != null) {
            mBuilder.setWhen(System.currentTimeMillis());
        } else Log.w(TAG, "updateSetWhen " + "Error refreshing notification");

        if (AesPrefs.getBooleanRes(R.string.SHOW_CLIPBOARD_NOTIFICATION, true)) {
            if (mBuilder == null) {
                mBuilder = new NotificationCompat.Builder(AndBasx.getContext())
                        .setContentTitle(AndBasx.getContext().getString(R.string.notification_title_battery))
                        .setSmallIcon(R.drawable.ic_attachment_white_24dp)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true);
            }

            mNotificationManager.notify(Const.NOTIFICATION_CLIPBOARD, mBuilder.build());
        }
    }

}
