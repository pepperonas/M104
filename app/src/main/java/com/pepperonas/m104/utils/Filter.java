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

package com.pepperonas.m104.utils;

import android.net.TrafficStats;

import com.pepperonas.m104.model.InstalledAppM104;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class Filter {

    public static boolean filterApps(InstalledAppM104 installedApp, String tmpAppName, int tmpUid) {
        // starts with (toLowerCase!)
        if (tmpAppName.toLowerCase().startsWith("com.") || tmpAppName.toLowerCase()
                .startsWith("de.") || tmpAppName.toLowerCase().startsWith("es.") || tmpAppName
                .toLowerCase().startsWith("fr.") || tmpAppName.toLowerCase().startsWith("org.")) {
            return true;
        }

        // equals (toLowerCase!)
        String tmpPkgName = installedApp.getApplicationInfo().packageName;
        if (tmpPkgName.toLowerCase().equals("com.google.android.gsf.login") || tmpPkgName
                .toLowerCase().equals("com.google.android.gsf") || tmpPkgName.toLowerCase()
                .equals("com.google.android.backuptransport") || tmpPkgName.toLowerCase()
                .equals("com.qualcomm.cabl") || tmpPkgName.toLowerCase().equals("com.huawei.mmitest")
                || tmpPkgName.toLowerCase().equals("android") || tmpPkgName.toLowerCase()
                .equals("com.android.calllogbackup") || tmpPkgName.toLowerCase()
                .equals("com.android.providers.settings") || tmpPkgName.toLowerCase()
                .equals("com.android.providers.media") || tmpPkgName.toLowerCase()
                .equals("com.android.providers.downloads.ui") || tmpPkgName.toLowerCase()
                .equals("com.android.providers.userdictionary") || tmpPkgName.toLowerCase()
                .equals("com.android.server.telecom") || tmpPkgName.toLowerCase()
                .equals("com.android.keychain") || tmpPkgName.toLowerCase()
                .equals("com.android.location.fused") || tmpPkgName.toLowerCase()
                .equals("com.android.location.fused") || tmpPkgName.toLowerCase()
                .equals("com.android.inputdevices")) {
            return true;
        }

        // no traffic
        return TrafficStats.getUidRxBytes(tmpUid) == 0 && TrafficStats.getUidTxBytes(tmpUid) == 0;
    }

}
