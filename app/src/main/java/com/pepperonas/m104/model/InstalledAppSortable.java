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

package com.pepperonas.m104.model;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;

import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.andbasx.system.DeviceUtils;
import com.pepperonas.jbasx.base.Binary;
import com.pepperonas.m104.R;
import com.pepperonas.m104.utils.Log;

import java.text.NumberFormat;
import java.util.Comparator;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class InstalledAppSortable extends InstalledAppM104 implements Comparator<InstalledAppM104> {

    @SuppressWarnings("unused")
    private static final String TAG = "InstalledAppSortable";

    private CharSequence formattedRxBytes;
    private CharSequence formattedTxBytes;
    private CharSequence formattedTotalBytes;
    private Drawable icon;

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
     * @param applicationInfo the application info
     * @param applicationName the application name
     */
    public InstalledAppSortable(ApplicationInfo applicationInfo, String applicationName, InstalledAppM104 installedAppM104) {
        super(applicationInfo, applicationName, installedAppM104);
        formattedRxBytes = initFormattedRxBytes();
        formattedTxBytes = initFormattedTxBytes();
        formattedTotalBytes = initFormattedTotalBytes();
        icon = initIcon();
    }

    public static final Comparator<InstalledAppSortable> DESCENDING_COMPARATOR = new Comparator<InstalledAppSortable>() {
        public int compare(InstalledAppSortable lhs, InstalledAppSortable rhs) {
            long rhsBytes = lhs.getBytesRxMobile() + lhs.getBytesTxMobile();
            long lhsBytes = rhs.getBytesRxMobile() + rhs.getBytesTxMobile();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(lhsBytes, rhsBytes);
            } else {
                return -1;
            }
        }
    };

    @Override
    public int compare(InstalledAppM104 lhs, InstalledAppM104 rhs) {
        long rhsBytes = lhs.getBytesRxMobile() + lhs.getBytesTxMobile();
        long lhsBytes = rhs.getBytesRxMobile() + rhs.getBytesTxMobile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Long.compare(lhsBytes, rhsBytes);
        } else {
            return -1;
        }
    }

    /**
     * Init formatted rx bytes char sequence.
     *
     * @return the char sequence
     */
    private CharSequence initFormattedRxBytes() {
        long traffic = getBytesRxMobile();
        return getCharSequence(traffic);
    }

    /**
     * Init formatted tx bytes char sequence.
     *
     * @return the char sequence
     */
    private CharSequence initFormattedTxBytes() {
        long traffic = getBytesTxMobile();
        return getCharSequence(traffic);
    }

    /**
     * Init formatted total bytes char sequence.
     *
     * @return the char sequence
     */
    private CharSequence initFormattedTotalBytes() {
        long traffic = getBytesRxMobile() + getBytesTxMobile();
        return getCharSequence(traffic);
    }

    /**
     * Init icon drawable.
     *
     * @return the drawable
     */
    private Drawable initIcon() {
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

        Log.i(TAG, "getCharSequence: " + getApplicationName() + " traffic=" + traffic);

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

