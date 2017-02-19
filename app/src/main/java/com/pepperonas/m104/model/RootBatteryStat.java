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

import android.graphics.drawable.Drawable;

import com.pepperonas.jbasx.base.StringUtils;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class RootBatteryStat {

    //    private InstalledAppSortable installedAppSortable;
    private Drawable icon;
    private String applicationName;


    public RootBatteryStat(String line) {
        String pkgName = StringUtils.fastSplit(line, ' ')[0].replace("package=", "");
        //        ApplicationInfo info = null;
        this.applicationName = pkgName;



        //        ApplicationInfo info = null;
        //        try {
        //            info =
        //        } catch (PackageManager.NameNotFoundException e) {
        //            e.printStackTrace();
        //        }
        //
        //        List<InstalledAppSortable> installedApps = new ArrayList<>();
        //        for (InstalledApp installedApp : SystemUtils.getInstalledApps()) {
        //            String tmpAppName = installedApp.getApplicationName();
        //            int tmpUid = installedApp.getApplicationInfo().uid;



    }


    public Drawable getIcon() {
        return icon;
    }


    public String getApplicationName() {
        return applicationName;
    }
}
