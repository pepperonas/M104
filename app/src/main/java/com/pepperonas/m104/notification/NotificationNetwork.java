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
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.RemoteViews;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.DrawableUtils;
import com.pepperonas.andbasx.system.DeviceUtils;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.jbasx.base.Binary;
import com.pepperonas.jbasx.base.Si;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;

import java.text.NumberFormat;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class NotificationNetwork {

    private static final String TAG = "NotificationNetwork";

    public static final String EXTRA_START_NETWORK = "nwk";

    private Context mCtx;

    private static NotificationManager mNotificationManager;

    private static NotificationCompat.Builder mBuilder;

    private static LruCache<String, Bitmap> mMemoryCache;

    private RemoteViews mRemoteViews;


    /**
     * Instantiates a new Notification panel.
     *
     * @param context the context
     */
    public NotificationNetwork(Context context) {
        this.mCtx = context;

        try {
            mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.notification_title_network))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOngoing(true);

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_view_network);

            initNetworkNotificationIntent();

            mBuilder.setContent(mRemoteViews);

            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (AesPrefs.getBooleanRes(R.string.SHOW_NETWORK_NOTIFICATION, true)) {
                mNotificationManager.notify(Const.NOTIFICATION_NETWORK, mBuilder.build());
            } else {
                mNotificationManager.cancel(Const.NOTIFICATION_NETWORK);
            }

        } catch (Exception e) {
            Log.e(TAG, "NotificationNetwork: Error while setting up network notification.");
        }

        initIcons();
    }


    /**
     * Init icons.
     */
    private void initIcons() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        @ColorRes int color = R.color.stock_android_accent;
        Drawable d = new IconicsDrawable(mCtx, Octicons.Icon.oct_arrow_small_down).colorRes(color).sizeDp(16);
        Bitmap bmRx = DrawableUtils.toBitmap(d);
        mRemoteViews.setImageViewBitmap(R.id.iv_rx, bmRx);
        d = new IconicsDrawable(mCtx, Octicons.Icon.oct_arrow_small_up).colorRes(color).sizeDp(16);
        Bitmap bmTx = DrawableUtils.toBitmap(d);
        mRemoteViews.setImageViewBitmap(R.id.iv_tx, bmTx);
        addBitmapToMemoryCache("rx", bmRx);
        addBitmapToMemoryCache("tx", bmTx);
    }


    /**
     * Handle interaction in {@link BatteryDialogActivity}.
     */
    public void initNetworkNotificationIntent() {
        Intent chartIntent = new Intent(mCtx, NetworkDialogActivity.class);

        chartIntent.putExtra(mCtx.getString(R.string.NETWORK_CHART_LIVE_MODE), AesPrefs.getBooleanResNoLog(R.string.NETWORK_CHART_LIVE_MODE, false));

        chartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                             | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        /**
         * Set {@link PendingIntent#FLAG_CANCEL_CURRENT} to receive
         * the {@link android.os.Bundle} object's extra in {@link NetworkDialogActivity}.
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, chartIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mRemoteViews.setOnClickPendingIntent(R.id.notification_container, pendingIntent);

        // launch main when circle is clicked
        Intent launch = new Intent(mCtx, MainActivity.class);
        launch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        launch.putExtra("start_fragment", EXTRA_START_NETWORK);

        /**
         * Important: set {@link PendingIntent.FLAG_UPDATE_CURRENT}
         * */
        PendingIntent btnLaunch = PendingIntent.getActivity(mCtx, Const.NOTIFICATION_NETWORK, launch, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.iv_notification_circle_left, btnLaunch);
    }


    /**
     * Update the notification, which is shown in {@link R.layout#notification_view_network}.
     *
     * @param totalRx       the total rx
     * @param totalTx       the total tx
     * @param mobileTotalRx the mobile total rx
     * @param mobileTotalTx the mobile total tx
     * @param currentRx     the current rx
     * @param currentTx     the current tx
     * @param networkType   the network type
     */
    public void update(final long totalRx, final long totalTx,
                       final long mobileTotalRx, final long mobileTotalTx,
                       final long currentRx, final long currentTx,
                       final SystemUtils.NetworkType networkType) {


        long totalTraffic = totalRx + totalTx;
        long mobileTotalTraffic = mobileTotalRx + mobileTotalTx;
        long currentTrafficPerSecond = currentRx + currentTx;

        int imageResourceId;
        if (currentTrafficPerSecond > Si.MEGA) {
            float f = currentTrafficPerSecond / (float) Si.MEGA;
            String fStr = String.valueOf(f);
            imageResourceId = resolveDrawableId("mbytes__" + fStr.split("\\.")[0] + "_" + String.valueOf(fStr.split("\\.")[1].charAt(0)));
        } else if (currentTrafficPerSecond != 0) {
            imageResourceId = resolveDrawableId("kbytes_" + currentTrafficPerSecond / (int) Si.KILO);
        } else {
            imageResourceId = resolveDrawableId("kbytes_" + 0);
        }


        try {
            if (imageResourceId == -1) {
                Log.e(TAG, "update: imageResourceId invalid.");
                imageResourceId = resolveDrawableId("kbytes_" + 0);
            }

            mBuilder.setSmallIcon(imageResourceId);

            makeCircle(currentTrafficPerSecond);
            makeRightSide(currentRx, true);
            makeRightSide(currentTx, false);

            makeCenterTop(networkType);

            makeCenterBottom(totalTraffic, mobileTotalTraffic);

            makeRxTxIcons();

            if (AesPrefs.getBooleanRes(R.string.SHOW_NETWORK_NOTIFICATION, true)) {
                Log.i(TAG, "---UPDATE---");
                mNotificationManager.notify(Const.NOTIFICATION_NETWORK, mBuilder.build());
            } else {
                mNotificationManager.cancel(Const.NOTIFICATION_NETWORK);
            }
        } catch (Exception e) {
            Log.e(TAG, "NotificationNetwork: Error while setting up network notification.");
        }

    }


    public static int resolveDrawableId(@NonNull String source) {
        Log.d(TAG, "resolveDrawableId SOURCE: " + source);

        String uri = "drawable/" + source;
        try {
            return AndBasx.getContext().getResources().getIdentifier(uri, null, AndBasx.getContext().getPackageName());
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * Make circle and right.
     *
     * @param currentTrafficPerSecond the current traffic per second
     */
    private void makeCircle(long currentTrafficPerSecond) {
        String unit;
        NumberFormat nf = NumberFormat.getNumberInstance(DeviceUtils.getLocale());

        if (currentTrafficPerSecond > Binary.GIGA) {
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
            unit = mCtx.getString(R.string._unit_gigabytes_per_second);
            mRemoteViews.setTextViewText(R.id.tv_notification_circle_value, nf.format(currentTrafficPerSecond / Binary.GIGA));
            mRemoteViews.setTextViewText(R.id.tv_notification_circle_values_unit, unit);
            return;
        }

        if (currentTrafficPerSecond > Binary.MEGA) {
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
            unit = mCtx.getString(R.string._unit_megabytes_per_second);
            mRemoteViews.setTextViewText(R.id.tv_notification_circle_value, nf.format(currentTrafficPerSecond / Binary.MEGA));
            mRemoteViews.setTextViewText(R.id.tv_notification_circle_values_unit, unit);
            return;
        }

        // no comma when lower than mega
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);

        if (currentTrafficPerSecond > Binary.KILO) {
            unit = mCtx.getString(R.string._unit_kilobytes_per_second);
            mRemoteViews.setTextViewText(R.id.tv_notification_circle_value, nf.format(currentTrafficPerSecond / Binary.KILO));
            mRemoteViews.setTextViewText(R.id.tv_notification_circle_values_unit, unit);
            return;
        }

        unit = mCtx.getString(R.string._unit_bytes_per_second);
        mRemoteViews.setTextViewText(R.id.tv_notification_circle_value, nf.format(currentTrafficPerSecond));
        mRemoteViews.setTextViewText(R.id.tv_notification_circle_values_unit, unit);
    }


    /**
     * Make circle and right.
     *
     * @param currentRxTx the current rx
     * @param isRx        the is rx
     */
    private void makeRightSide(long currentRxTx, boolean isRx) {
        String unit;
        NumberFormat nf = NumberFormat.getNumberInstance(DeviceUtils.getLocale());

        if (currentRxTx > Binary.GIGA) {
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
            unit = mCtx.getString(R.string._unit_gigabytes_per_second);
            if (isRx) {
                mRemoteViews.setTextViewText(R.id.tv_s_notification_right_bottom, nf.format(currentRxTx / Binary.GIGA) + " " + unit);
            } else {
                mRemoteViews.setTextViewText(R.id.tv_s_notification_right_top, nf.format(currentRxTx / Binary.GIGA) + " " + unit);
            }
            return;
        }

        if (currentRxTx > Binary.MEGA) {
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
            unit = mCtx.getString(R.string._unit_megabytes_per_second);
            if (isRx) {
                mRemoteViews.setTextViewText(R.id.tv_s_notification_right_bottom, nf.format(currentRxTx / Binary.MEGA) + " " + unit);
            } else {
                mRemoteViews.setTextViewText(R.id.tv_s_notification_right_top, nf.format(currentRxTx / Binary.MEGA) + " " + unit);
            }
            return;
        }

        // no comma when lower than mega
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);

        if (currentRxTx > Binary.KILO) {
            unit = mCtx.getString(R.string._unit_kilobytes_per_second);
            if (isRx) {
                mRemoteViews.setTextViewText(R.id.tv_s_notification_right_bottom, nf.format(currentRxTx / Binary.KILO) + " " + unit);
            } else {
                mRemoteViews.setTextViewText(R.id.tv_s_notification_right_top, nf.format(currentRxTx / Binary.KILO) + " " + unit);
            }
            return;
        }

        unit = mCtx.getString(R.string._unit_bytes_per_second);
        if (isRx) {
            mRemoteViews.setTextViewText(R.id.tv_s_notification_right_bottom, nf.format(currentRxTx) + " " + unit);
        } else {
            mRemoteViews.setTextViewText(R.id.tv_s_notification_right_top, nf.format(currentRxTx) + " " + unit);
        }
    }


    /**
     * Make center top.
     *
     * @param networkType the network type
     */
    private void makeCenterTop(SystemUtils.NetworkType networkType) {
        String networkInfo;
        if (networkType == SystemUtils.NetworkType.Mobile) {
            networkInfo = mCtx.getString(R.string.network_type_mobile) + " | " + SystemUtils.getCarrierName();
        } else {

            int wifiSignalStrength = SystemUtils.getWifiSignalStrength();

            String networkName = SystemUtils.getWifiName();
            networkName = replaceQuotes(networkName);
            networkInfo = mCtx.getString(R.string.network_type_wifi) + " | " + networkName + " " + wifiSignalStrength + " %";
        }

        mRemoteViews.setTextViewText(R.id.tv_m_notification_center_top, networkInfo);
    }


    /**
     * Make center bottom.
     *
     * @param totalTraffic       the total traffic
     * @param mobileTotalTraffic the mobile total traffic
     */
    private void makeCenterBottom(long totalTraffic, long mobileTotalTraffic) {
        String infoTotal = mCtx.getString(R.string.network_type_wifi) + ": " + getTrafficInfo(totalTraffic - mobileTotalTraffic);
        infoTotal += (" | " + mCtx.getString(R.string.network_type_mobile) + ": " + getTrafficInfo(mobileTotalTraffic));

        mRemoteViews.setTextViewText(R.id.tv_s_notification_center_bottom, infoTotal);
    }


    /**
     * Make rx tx icons.
     */
    private void makeRxTxIcons() {
        //        mRemoteViews.setImageViewBitmap(R.id.iv_rx, getBitmapFromMemCache("rx"));
        //        mRemoteViews.setImageViewBitmap(R.id.iv_tx, getBitmapFromMemCache("tx"));
    }


    /**
     * Add bitmap to memory cache.
     *
     * @param key    the key
     * @param bitmap the bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    /**
     * Gets bitmap from mem cache.
     *
     * @param key the key
     * @return the bitmap from mem cache
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    /**
     * Gets traffic info.
     *
     * @param absTraffic the abs traffic
     * @return the traffic info
     */
    private String getTrafficInfo(long absTraffic) {
        String unit;
        NumberFormat nf = NumberFormat.getNumberInstance(DeviceUtils.getLocale());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        if (absTraffic > Binary.GIGA) {
            unit = mCtx.getString(R.string._unit_gigabytes);
            return nf.format(absTraffic / Binary.GIGA) + " " + unit;

        } if (absTraffic > Binary.MEGA) {
            unit = mCtx.getString(R.string._unit_megabytes);
            return nf.format(absTraffic / Binary.MEGA) + " " + unit;

        } else {
            unit = mCtx.getString(R.string._unit_kilobytes);
            if (absTraffic != 0) {
                return nf.format(absTraffic / Binary.KILO) + " " + unit;
            } else return "0 " + unit;
        }
    }


    /**
     * Replace quotes string.
     *
     * @param networkName the network name
     * @return the string
     */
    @NonNull
    private String replaceQuotes(String networkName) {
        if (networkName.charAt(0) == '"') {
            networkName = networkName.substring(1, networkName.length() - 1);
        }
        if (networkName.charAt(networkName.length() - 1) == '"') {
            networkName = networkName.substring(0, networkName.length() - 2);
        }
        return networkName;
    }


    /**
     * Update set when, so the notifications will be forced to be shown in the correct order.
     */
    public static void updateSetWhen() {
        Log.i(TAG, "---UPDATE (set when)---");

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) AndBasx.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(Const.NOTIFICATION_NETWORK);

        if (mBuilder != null) {
            mBuilder.setWhen(System.currentTimeMillis());
        } else Log.w(TAG, "updateSetWhen " + "Error refreshing notification");
    }

}
