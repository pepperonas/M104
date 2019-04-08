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

package com.pepperonas.m104.utils;

import android.content.Context;
import android.os.BatteryManager;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.m104.R;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class BatteryUtils {

    @SuppressWarnings("unused")
    private static final String TAG = "BatteryUtils";

    /**
     * Gets battery capacity.
     *
     * @return the battery capacity
     */
    public static double getBatteryCapacity() {
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            Object profile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class)
                    .newInstance(AndBasx.getContext());
            return (double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(profile, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1d;
    }

    /**
     * Gets makeRemainingInfo energy.
     * <p/>
     * Battery makeRemainingInfo energy in microwatt-hours, as a long integer.
     *
     * @return the makeRemainingInfo energy
     */
    public static long getRemainingEnergy() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) AndBasx.getContext().getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                return batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER) / 1000L;
            }
        }
        return -1L;
    }

    /**
     * Gets makeRemainingInfo.
     *
     * @param level      the level
     * @param isCharging the is charging
     * @return the makeRemainingInfo
     */
    public static float getRemaining(int level, boolean isCharging) {
        if (!isCharging) {
            float dischargePerHour = AesPrefs.getFloatRes(R.string.CYCLIC_CONSUMPTION_PER_HOUR, 4.5f);
            return (float) level / dischargePerHour;
        } else {
            float chargePerHour = AesPrefs.getFloatRes(R.string.CYCLIC_CHARGE_PER_HOUR, 12.5f);
            return (float) (100 - level) / chargePerHour;
        }
    }

    /**
     * Gets absolute m ah.
     *
     * @return the absolute m ah
     */
    public static double getAbsolute_mAh() {
        return (AesPrefs.getIntRes(R.string.BATTERY_CAPACITY, 0));
    }

    /**
     * Gets relative m ah.
     *
     * @param level the level
     * @return the relative m ah
     */
    public static double getRelative_mAh(int level) {
        return Calculations.getRemainingCapacity(level);
    }

    /**
     * Gets charge.
     * <p/>
     * Instantaneous battery current in microamperes, as an integer.
     * Positive values indicate net current entering the battery from a charge source,
     * negative values indicate net current discharging from the battery.
     *
     * @return the charge
     */
    public static int getCharge() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) AndBasx.getContext().getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            }
        }
        return -1;
    }

    /**
     * Gets current average.
     * <p/>
     * Average battery current in microamperes, as an integer. Positive values indicate net current
     * entering the battery from a charge source, negative values indicate net current discharging
     * from the battery. The time period over which the average is computed may depend on the fuel
     * gauge hardware and its configuration.
     *
     * @return the current average
     */
    public static int getCurrentAverage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) AndBasx.getContext().getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
            }
        }
        return -1;
    }

    /**
     * Gets charge counter.
     * <p/>
     * Battery capacity in microampere-hours, as an integer.
     *
     * @return the charge counter
     */
    public static int getChargeCounter() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) AndBasx.getContext().getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            }
        }
        return -1;
    }

}
