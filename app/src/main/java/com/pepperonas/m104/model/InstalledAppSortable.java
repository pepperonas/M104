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

package com.pepperonas.m104.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.support.annotation.NonNull;

import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.andbasx.datatype.InstalledApp;
import com.pepperonas.andbasx.system.DeviceUtils;
import com.pepperonas.jbasx.base.Binary;
import com.pepperonas.m104.R;

import java.text.NumberFormat;
import java.util.Comparator;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class InstalledAppSortable extends InstalledApp implements Comparator<InstalledApp> {

    private CharSequence formattedRxBytes = "";
    private CharSequence formattedTxBytes = "";
    private CharSequence formattedTotalBytes = "";
    private Drawable icon = null;


    /**
     * Gets formatted rx bytes.
     *
     * @return the formatted rx bytes
     */
    public CharSequence getFormattedRxBytes() {
        return formattedRxBytes;
    }


    /**
     * Gets formatted tx bytes.
     *
     * @return the formatted tx bytes
     */
    public CharSequence getFormattedTxBytes() {
        return formattedTxBytes;
    }


    /**
     * Gets formatted total bytes.
     *
     * @return the formatted total bytes
     */
    public CharSequence getFormattedTotalBytes() {
        return formattedTotalBytes;
    }


    /**
     * Gets icon.
     *
     * @return the icon
     */
    public Drawable getIcon() {
        return icon;
    }


    /**
     * Instantiates a new InstalledBasic app sortable.
     *
     * @param ctx             the ctx
     * @param applicationInfo the application info
     * @param applicationName the application name
     */
    public InstalledAppSortable(Context ctx, ApplicationInfo applicationInfo, String applicationName) {
        super(applicationInfo, applicationName);
        formattedRxBytes = initFormattedRxBytes();
        formattedTxBytes = initFormattedTxBytes();
        formattedTotalBytes = initFormattedTotalBytes();
        icon = initIcon(ctx);
    }


    public static final Comparator<InstalledAppSortable> DESCENDING_COMPARATOR = new Comparator<InstalledAppSortable>() {
        public int compare(InstalledAppSortable lhs, InstalledAppSortable rhs) {
            long rhsBytes = (TrafficStats.getUidRxBytes(lhs.getApplicationInfo().uid) + TrafficStats.getUidTxBytes(lhs.getApplicationInfo().uid));
            long lhsBytes = (TrafficStats.getUidRxBytes(rhs.getApplicationInfo().uid) + TrafficStats.getUidTxBytes(rhs.getApplicationInfo().uid));
            return lhsBytes < rhsBytes ? -1 : lhsBytes == rhsBytes ? 0 : 1;

        }
    };


    @Override
    public int compare(InstalledApp lhs, InstalledApp rhs) {
        long rhsBytes = (TrafficStats.getUidRxBytes(lhs.getApplicationInfo().uid) + TrafficStats.getUidTxBytes(lhs.getApplicationInfo().uid));
        long lhsBytes = (TrafficStats.getUidRxBytes(rhs.getApplicationInfo().uid) + TrafficStats.getUidTxBytes(rhs.getApplicationInfo().uid));
        return lhsBytes < rhsBytes ? -1 : lhsBytes == rhsBytes ? 0 : 1;
    }


    /**
     * Init formatted rx bytes char sequence.
     *
     * @return the char sequence
     */
    private CharSequence initFormattedRxBytes() {
        long traffic = TrafficStats.getUidRxBytes(this.getApplicationInfo().uid);
        return getCharSequence(traffic);
    }


    /**
     * Init formatted tx bytes char sequence.
     *
     * @return the char sequence
     */
    private CharSequence initFormattedTxBytes() {
        long traffic = TrafficStats.getUidTxBytes(this.getApplicationInfo().uid);
        return getCharSequence(traffic);

    }


    /**
     * Init formatted total bytes char sequence.
     *
     * @return the char sequence
     */
    private CharSequence initFormattedTotalBytes() {
        long traffic = TrafficStats.getUidRxBytes(this.getApplicationInfo().uid) + TrafficStats.getUidTxBytes(this.getApplicationInfo().uid);
        return getCharSequence(traffic);
    }


    /**
     * Init icon drawable.
     *
     * @param ctx the ctx
     * @return the drawable
     */
    private Drawable initIcon(Context ctx) {
        return getApplicationInfo().loadIcon(AndBasx.getContext().getPackageManager());
    }


    /**
     * Gets char sequence.
     *
     * @param traffic the traffic
     * @return the char sequence
     */
    @NonNull
    private CharSequence getCharSequence(long traffic) {
        NumberFormat numberFormat = NumberFormat.getInstance(DeviceUtils.getLocale());
        numberFormat.setMaximumFractionDigits(1);

        long div;
        String unit;
        if (traffic > Binary.GIGA) {
            div = (long) Binary.GIGA;
            unit = Loader.gStr(R.string._unit_gigabytes);
        } else if (traffic > Binary.MEGA) {
            div = (long) Binary.MEGA;
            unit = Loader.gStr(R.string._unit_megabytes);
        } else if (traffic > Binary.KILO) {
            div = (long) Binary.KILO;
            unit = Loader.gStr(R.string._unit_kilobytes);
        } else {
            div = 1;
            unit = Loader.gStr(R.string._unit_bytes);
        }
        float f_traffic = (float) traffic / (float) div;
        return numberFormat.format(f_traffic) + " " + unit;
    }

}

