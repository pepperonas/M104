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

package com.pepperonas.m104.utils;

import android.content.Context;
import android.os.BatteryManager;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.andbasx.system.DeviceUtils;
import com.pepperonas.jbasx.math.ConvertUtils;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;

import java.text.NumberFormat;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class StringFactory {

    private static final String TAG = "StringFactory";


    public static CharSequence makeLevelInfo(Context ctx, int level) {
        return level != Const.VALUE_UNSET ? (level + " %") : (ctx.getString(R.string.ns));
    }


    public static CharSequence makeRemainingInfo(Context ctx, int level, boolean isCharging) {
        float remaining = BatteryUtils.getRemaining(level, isCharging);

        if (level == 100) {
            return ctx.getString(R.string.fully_charged);
        }

        long last = (long) (remaining * 1000f * 60f * 60f);

        if (!isCharging) {
            return String.format(ctx.getString(R.string.discharging_message), formatRemaining(last));
        } else return String.format(ctx.getString(R.string.charging_message), formatRemaining(last));
    }


    public static CharSequence makeAbsolute_mAhValueInfo(Context ctx, int level) {
        NumberFormat numberFormat = NumberFormat.getInstance(DeviceUtils.getLocale());
        String relative = numberFormat.format((int) BatteryUtils.getRelative_mAh(level));
        String total = numberFormat.format((int) BatteryUtils.getAbsolute_mAh());
        return relative + "/" + total + " " + ctx.getString(R.string._unit_milli_ampere);
    }


    public static CharSequence makeStatusInfo(Context ctx, int status, int plugged) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_FULL: return ctx.getString(R.string.battery_charge_full);
            case BatteryManager.BATTERY_STATUS_CHARGING: return ctx.getString(R.string.battery_charge_charging_over) + " " + plugged(ctx, plugged);
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return ctx.getString(R.string.battery_charge_discharging);
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return ctx.getString(R.string.battery_charge_not_charging);
            default: return ctx.getString(R.string.battery_charge_unknown);
        }
    }


    public static CharSequence makeTemperatureInfo(Context ctx, double temperature) {
        NumberFormat numberFormat = NumberFormat.getInstance(DeviceUtils.getLocale());
        return AesPrefs.getBooleanRes(R.string.UNITS_CELSIUS, true) ? (numberFormat.format((float) temperature) + " " + ctx.getString(R.string._unit_celsius))
                                                                    : (numberFormat.format((float) ConvertUtils.celsiusToFahrenheit(temperature)) + " " + ctx.getString(R.string._unit_fahrenheit));
    }


    public static CharSequence makeRelative_mAhInfo(Context ctx, Integer current_mAh) {
        if (current_mAh != Const.VALUE_UNSET) {
            current_mAh /= 1000;
            NumberFormat numberFormat = NumberFormat.getInstance(DeviceUtils.getLocale());
            return numberFormat.format((int) current_mAh) + " " + ctx.getString(R.string._unit_milli_ampere_per_hour);
        } else return ctx.getString(R.string.no_data_current_now);
    }


    public static CharSequence plugged(Context ctx, int plugged) {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC: return ctx.getString(R.string.battery_plugged_ac);
            case BatteryManager.BATTERY_PLUGGED_USB: return ctx.getString(R.string.battery_plugged_usb);
            case BatteryManager.BATTERY_PLUGGED_WIRELESS: return ctx.getString(R.string.battery_plugged_wireless);
            default: return ctx.getString(R.string.unknown);
        }
    }


    public static CharSequence makeVoltage(Context ctx, int voltage) {
        NumberFormat numberFormat = NumberFormat.getInstance(DeviceUtils.getLocale());
        return numberFormat.format((float) voltage / 1000f) + " V";
    }


    public static CharSequence makePercentagePerHourInfo(Context ctx, boolean isCharging) {
        NumberFormat percentFormat = NumberFormat.getPercentInstance(DeviceUtils.getLocale());
        percentFormat.setMinimumFractionDigits(1);
        percentFormat.setMaximumFractionDigits(1);

        if (isCharging) {
            float value = AesPrefs.getFloatRes(R.string.CYCLIC_CHARGE_PER_HOUR, Float.MIN_VALUE) / 100f;
            if (value == Float.MIN_VALUE) {
                return ctx.getString(R.string.ns);
            }
            return "+" + String.format(ctx.getString(R.string.per_hour_format), percentFormat.format(value));
        } else {
            float value = AesPrefs.getFloatRes(R.string.CYCLIC_CONSUMPTION_PER_HOUR, Float.MIN_VALUE) / 100f;
            if (value == Float.MIN_VALUE) {
                return ctx.getString(R.string.ns);
            }
            if (value == 0.0f) return "0 %/h";
            return "-" + String.format(ctx.getString(R.string.per_hour_format), percentFormat.format(value));
        }

    }


    public static CharSequence makeHealthInfo(Context ctx, int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_good));
            case BatteryManager.BATTERY_HEALTH_DEAD: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_dead));
            case BatteryManager.BATTERY_HEALTH_COLD: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_cold));
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_over_voltage));
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_overheat));
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_unspecified_failure));
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default: return String.format(ctx.getString(R.string.health_format), ctx.getString(R.string.battery_health_unknown));
        }

    }


    public static String formatRemaining(long millis) {
        int h = (int) (millis / 1000) / 3600;
        int m = (int) (millis / 1000) % 3600 / 60;
        int s = (int) (millis / 1000) % 60;

        if (h != 0) {
            return h + " " + Loader.gStr(R.string.abbr_hours) + " " +
                   m + " " + Loader.gStr(R.string.minutes);
        }
        return m + " " + Loader.gStr(R.string.minutes);
    }

}
