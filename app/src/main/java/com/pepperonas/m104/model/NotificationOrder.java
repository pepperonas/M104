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

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.m104.notification.NotificationBattery;
import com.pepperonas.m104.notification.NotificationClipboard;
import com.pepperonas.m104.notification.NotificationNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class NotificationOrder {

    private static final String TAG = "NotificationOrder";


    public static void setNotificationBattery() {
        AesPrefs.putLong("n1", System.currentTimeMillis());
    }


    public static void setNotificationNetwork() {
        AesPrefs.putLong("n2", System.currentTimeMillis());
    }


    public static void setNotificationClipboard() {
        AesPrefs.putLong("n3", System.currentTimeMillis());
    }


    public static void callOrderedUpdate() {
        List<Long> longs = new ArrayList<>();
        longs.add(AesPrefs.getLong("n1", 0L));
        longs.add(AesPrefs.getLong("n2", 0L));
        longs.add(AesPrefs.getLong("n3", 0L));
        Collections.sort(longs, Collections.reverseOrder());
        for (int i = 0; i < longs.size(); i++) {
            Log.d(TAG, "callOrderedUpdate " + longs.get(i));
            if (AesPrefs.getLong("n1", -1) == longs.get(i)) {
                NotificationBattery.updateSetWhen();
                Log.w(TAG, "callOrderedUpdate BATTERY");
            }
            if (AesPrefs.getLong("n2", -1) == longs.get(i)) {
                NotificationNetwork.updateSetWhen();
                Log.w(TAG, "callOrderedUpdate NETWORK");
            }
            if (AesPrefs.getLong("n3", -1) == longs.get(i)) {
                NotificationClipboard.updateSetWhen();
                Log.w(TAG, "callOrderedUpdate CLIPBOARD");
            }
        }

    }


    public static void checkInit() throws InterruptedException {
        if ((AesPrefs.getLong("n1", 0L) == 0L) && (AesPrefs.getLong("n2", 0L) == 0L) && (AesPrefs.getLong("n3", 0L) == 0L)) {
            setNotificationBattery();
            Thread.sleep(1);
            setNotificationNetwork();
            Thread.sleep(1);
            setNotificationClipboard();
        }
    }
}
