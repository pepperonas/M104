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

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import java.util.Comparator;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class InstalledBasic implements Comparator<String> {

    private Drawable icon = null;

    private String packageName;

    private String appName;

    private final int uid;

    private final String processName;


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
    public InstalledBasic(ApplicationInfo applicationInfo, String applicationName, Drawable icon) {
        this.packageName = applicationInfo.packageName;
        this.appName = applicationName;
        this.icon = icon;
        this.uid = applicationInfo.uid;
        this.processName = applicationInfo.processName;
    }


    @Override
    public boolean equals(Object o) {
        return o.toString().equals(packageName);
    }


    @Override
    public String toString() {
        return packageName;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }


    @Override
    public int compare(String lhs, String rhs) {
        return 0;
    }

}

