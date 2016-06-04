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

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class NetworkHistory {

    private long stamp;
    private long rx, tx;
    private long rxMobile, txMobile;
    private String pgkName;


    public NetworkHistory(long stamp, long rx, long tx, long rxMobile, long txMobile, String pkgName) {
        this.stamp = stamp;
        this.rx = rx;
        this.tx = tx;
        this.rxMobile = rxMobile;
        this.txMobile = txMobile;
        this.pgkName = pkgName;
    }


    public long getStamp() {
        return stamp;
    }


    public void setStamp(long stamp) {
        this.stamp = stamp;
    }


    public long getRx() {
        return rx;
    }


    public void setRx(long rx) {
        this.rx = rx;
    }


    public long getTx() {
        return tx;
    }


    public void setTx(long tx) {
        this.tx = tx;
    }


    public long getRxMobile() {
        return rxMobile;
    }


    public void setRxMobile(long rxMobile) {
        this.rxMobile = rxMobile;
    }


    public long getTxMobile() {
        return txMobile;
    }


    public void setTxMobile(long txMobile) {
        this.txMobile = txMobile;
    }


    public String getPgkName() {
        return pgkName;
    }


    public void setPgkName(String pgkName) {
        this.pgkName = pgkName;
    }
}
