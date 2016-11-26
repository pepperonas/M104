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

package com.pepperonas.m104.utils;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.m104.R;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class Calculations {

    /**
     * Gets makeRemainingInfo capacity.
     *
     * @param level the level
     * @return the makeRemainingInfo capacity
     */
    public static double getRemainingCapacity(int level) {
        return (AesPrefs.getIntRes(R.string.BATTERY_CAPACITY, 0) * (double) level / 100d);
    }


    /**
     * Gets makeRemainingInfo capacity while charging.
     *
     * @param level the level
     * @return the makeRemainingInfo capacity while charging
     */
    public static double getRemainingCapacityWhileCharging(int level) {
        return (AesPrefs.getIntRes(R.string.BATTERY_CAPACITY, 0) - getRemainingCapacity(level));
    }


    /**
     * Gets hours.
     *
     * @param hours the hours
     * @return the hours
     */
    public static long getHours(int hours) {
        return System.currentTimeMillis() - (hours * 60 * 60 * 1000);
    }


    /**
     * Gets minutes.
     *
     * @param millis the millis
     * @return the minutes
     */
    public static long getMinutes(long millis) {
        return System.currentTimeMillis() - (millis * 60 * 1000);
    }

}
