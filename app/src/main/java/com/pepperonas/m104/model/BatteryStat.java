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

package com.pepperonas.m104.model;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class BatteryStat {

    private long stamp;

    private boolean isCharging;
    private ChargeMode chargeMode;
    private boolean isCharged;

    private int level;
    private float temperature;
    private float voltage;

    private boolean isScreenOn;
    private int screenBrightness;

    private boolean isWifiEnabled;
    private boolean isWifiConnected;

    private boolean isGpsEnabled;
    private boolean isGpsNetworkEnabled;
    private boolean isGpsPassiveEnabled;

    private boolean isSyncEnabled;
    private boolean isAirPlaneModeEnabled;

    private int remainingCapacity_mAh;


    /**
     * Instantiates a new Battery stat.
     *
     * @param stamp the stamp
     * @param isCharging the is charging
     * @param chargeMode the charge mode
     * @param isCharged the is charged
     * @param level the level
     * @param temperature the temperature
     * @param voltage the makeVoltage
     * @param isScreenOn the is screen on
     * @param screenBrightness the screen brightness
     * @param isWifiEnabled the is wifi enabled
     * @param isWifiConnected the is wifi connected
     * @param isGpsEnabled the is gps enabled
     * @param isGpsNetworkEnabled the is gps network enabled
     * @param isGpsPassiveEnabled the is gps passive enabled
     * @param isSyncEnabled the is sync enabled
     * @param isAirPlaneModeEnabled the is air plane mode enabled
     * @param remainingCapacity_mAh the remainingCapacity_mAh
     */
    public BatteryStat(long stamp, boolean isCharging, ChargeMode chargeMode, boolean isCharged,
        int level, float temperature, float voltage, boolean isScreenOn, int screenBrightness,
        boolean isWifiEnabled, boolean isWifiConnected, boolean isGpsEnabled,
        boolean isGpsNetworkEnabled, boolean isGpsPassiveEnabled, boolean isSyncEnabled,
        boolean isAirPlaneModeEnabled, int remainingCapacity_mAh) {

        this.stamp = stamp;
        this.isCharging = isCharging;
        this.chargeMode = chargeMode;
        this.isCharged = isCharged;
        this.level = level;
        this.temperature = temperature;
        this.voltage = voltage;
        this.isScreenOn = isScreenOn;
        this.screenBrightness = screenBrightness;
        this.isWifiEnabled = isWifiEnabled;
        this.isWifiConnected = isWifiConnected;
        this.isGpsEnabled = isGpsEnabled;
        this.isGpsNetworkEnabled = isGpsNetworkEnabled;
        this.isGpsPassiveEnabled = isGpsPassiveEnabled;
        this.isSyncEnabled = isSyncEnabled;
        this.isAirPlaneModeEnabled = isAirPlaneModeEnabled;
        this.remainingCapacity_mAh = remainingCapacity_mAh;
    }


    /**
     * Gets stamp.
     *
     * @return the stamp
     */
    public long getStamp() {
        return stamp;
    }


    /**
     * Sets stamp.
     *
     * @param stamp the stamp
     */
    public void setStamp(long stamp) {
        this.stamp = stamp;
    }


    /**
     * Is charging boolean.
     *
     * @return the boolean
     */
    public boolean isCharging() {
        return isCharging;
    }


    /**
     * Sets charging.
     *
     * @param charging the charging
     */
    public void setCharging(boolean charging) {
        isCharging = charging;
    }


    /**
     * Gets charge mode.
     *
     * @return the charge mode
     */
    public ChargeMode getChargeMode() {
        return chargeMode;
    }


    /**
     * Sets charge mode.
     *
     * @param chargeMode the charge mode
     */
    public void setChargeMode(ChargeMode chargeMode) {
        this.chargeMode = chargeMode;
    }


    /**
     * Is charged boolean.
     *
     * @return the boolean
     */
    public boolean isCharged() {
        return isCharged;
    }


    /**
     * Sets charged.
     *
     * @param charged the charged
     */
    public void setCharged(boolean charged) {
        isCharged = charged;
    }


    /**
     * Gets level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }


    /**
     * Sets level.
     *
     * @param level the level
     */
    public void setLevel(int level) {
        this.level = level;
    }


    /**
     * Gets temperature.
     *
     * @return the temperature
     */
    public float getTemperature() {
        return temperature;
    }


    /**
     * Sets temperature.
     *
     * @param temperature the temperature
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }


    /**
     * Gets makeVoltage.
     *
     * @return the makeVoltage
     */
    public float getVoltage() {
        return voltage;
    }


    /**
     * Sets makeVoltage.
     *
     * @param voltage the makeVoltage
     */
    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }


    /**
     * Is screen on boolean.
     *
     * @return the boolean
     */
    public boolean isScreenOn() {
        return isScreenOn;
    }


    /**
     * Sets screen on.
     *
     * @param screenOn the screen on
     */
    public void setScreenOn(boolean screenOn) {
        isScreenOn = screenOn;
    }


    /**
     * Gets screen brightness.
     *
     * @return the screen brightness
     */
    public int getScreenBrightness() {
        return screenBrightness;
    }


    /**
     * Sets screen brightness.
     *
     * @param screenBrightness the screen brightness
     */
    public void setScreenBrightness(int screenBrightness) {
        this.screenBrightness = screenBrightness;
    }


    /**
     * Is wifi enabled boolean.
     *
     * @return the boolean
     */
    public boolean isWifiEnabled() {
        return isWifiEnabled;
    }


    /**
     * Sets wifi enabled.
     *
     * @param wifiEnabled the wifi enabled
     */
    public void setWifiEnabled(boolean wifiEnabled) {
        isWifiEnabled = wifiEnabled;
    }


    /**
     * Is wifi connected boolean.
     *
     * @return the boolean
     */
    public boolean isWifiConnected() {
        return isWifiConnected;
    }


    /**
     * Sets wifi connected.
     *
     * @param wifiConnected the wifi connected
     */
    public void setWifiConnected(boolean wifiConnected) {
        isWifiConnected = wifiConnected;
    }


    /**
     * Is gps enabled boolean.
     *
     * @return the boolean
     */
    public boolean isGpsEnabled() {
        return isGpsEnabled;
    }


    /**
     * Sets gps enabled.
     *
     * @param gpsEnabled the gps enabled
     */
    public void setGpsEnabled(boolean gpsEnabled) {
        isGpsEnabled = gpsEnabled;
    }


    /**
     * Is gps network enabled boolean.
     *
     * @return the boolean
     */
    public boolean isGpsNetworkEnabled() {
        return isGpsNetworkEnabled;
    }


    /**
     * Sets gps network enabled.
     *
     * @param gpsNetworkEnabled the gps network enabled
     */
    public void setGpsNetworkEnabled(boolean gpsNetworkEnabled) {
        isGpsNetworkEnabled = gpsNetworkEnabled;
    }


    /**
     * Is gps passive enabled boolean.
     *
     * @return the boolean
     */
    public boolean isGpsPassiveEnabled() {
        return isGpsPassiveEnabled;
    }


    /**
     * Sets gps passive enabled.
     *
     * @param gpsPassiveEnabled the gps passive enabled
     */
    public void setGpsPassiveEnabled(boolean gpsPassiveEnabled) {
        isGpsPassiveEnabled = gpsPassiveEnabled;
    }


    /**
     * Is sync enabled boolean.
     *
     * @return the boolean
     */
    public boolean isSyncEnabled() {
        return isSyncEnabled;
    }


    /**
     * Sets sync enabled.
     *
     * @param syncEnabled the sync enabled
     */
    public void setSyncEnabled(boolean syncEnabled) {
        isSyncEnabled = syncEnabled;
    }


    /**
     * Is air plane mode enabled boolean.
     *
     * @return the boolean
     */
    public boolean isAirPlaneModeEnabled() {
        return isAirPlaneModeEnabled;
    }


    /**
     * Sets air plane mode enabled.
     *
     * @param airPlaneModeEnabled the air plane mode enabled
     */
    public void setAirPlaneModeEnabled(boolean airPlaneModeEnabled) {
        isAirPlaneModeEnabled = airPlaneModeEnabled;
    }


    /**
     * Gets current now.
     *
     * @return the current now
     */
    public int getRemainingCapacity_mAh() {
        return remainingCapacity_mAh;
    }


    /**
     * Sets current now.
     *
     * @param remainingCapacity_mAh the current now
     */
    public void setRemainingCapacity_mAh(int remainingCapacity_mAh) {
        this.remainingCapacity_mAh = remainingCapacity_mAh;
    }
}
