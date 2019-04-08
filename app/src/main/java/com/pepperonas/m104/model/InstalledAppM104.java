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

/**
 * The type Installed app.
 *
 * @author Martin Pfeffer (pepperonas)
 */
public class InstalledAppM104 {

    private ApplicationInfo applicationInfo;

    private String applicationName;

    private long bytesRx;
    private long bytesTx;
    private long bytesRxMobile;
    private long bytesTxMobile;

    public InstalledAppM104(ApplicationInfo applicationInfo, String applicationName,
                            long bytesRx, long bytesTx, long bytesRxMobile, long bytesTxMobile) {
        this.applicationInfo = applicationInfo;
        this.applicationName = applicationName;
        this.bytesRx = bytesRx;
        this.bytesTx = bytesTx;
        this.bytesRxMobile = bytesRxMobile;
        this.bytesTxMobile = bytesTxMobile;
    }

    public InstalledAppM104(ApplicationInfo applicationInfo, String applicationName, InstalledAppM104 installedAppM104) {
        this.applicationInfo = applicationInfo;
        this.applicationName = applicationName;
        bytesRx = installedAppM104.getBytesRx();
        bytesTx = installedAppM104.getBytesTx();
        bytesRxMobile = installedAppM104.getBytesRxMobile();
        bytesTxMobile = installedAppM104.getBytesTxMobile();
    }

    /**
     * Gets application info.
     *
     * @return the application info
     */
    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    /**
     * Sets application info.
     *
     * @param applicationInfo the application info
     */
    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    /**
     * Gets application name.
     *
     * @return the application name
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Sets application name.
     *
     * @param applicationName the application name
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getBytesRx() {
        return bytesRx;
    }

    public void setBytesRx(long bytesRx) {
        this.bytesRx = bytesRx;
    }

    public long getBytesTx() {
        return bytesTx;
    }

    public void setBytesTx(long bytesTx) {
        this.bytesTx = bytesTx;
    }

    public long getBytesRxMobile() {
        return bytesRxMobile;
    }

    public void setBytesRxMobile(long bytesRxMobile) {
        this.bytesRxMobile = bytesRxMobile;
    }

    public long getBytesTxMobile() {
        return bytesTxMobile;
    }

    public void setBytesTxMobile(long bytesTxMobile) {
        this.bytesTxMobile = bytesTxMobile;
    }
}
