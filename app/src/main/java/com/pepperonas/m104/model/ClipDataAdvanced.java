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

import com.pepperonas.andbasx.system.DeviceUtils;
import com.pepperonas.m104.custom.SizedText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class ClipDataAdvanced {

    public static final int TYPE_DEFAULT = 0;
    private long timestamp;

    private int type;
    private String clipText;
    private SizedText sizedText;
    private final long iv;


    public SizedText getSizedText() {
        return sizedText;
    }


    /**
     * Instantiates a new Clip data advanced.
     *
     * @param timestamp the timestamp
     * @param type      the type
     * @param clipText  the clipText
     */
    public ClipDataAdvanced(long timestamp, int type, String clipText, long iv) {
        this.timestamp = timestamp;
        this.type = type;
        this.clipText = clipText;
        this.sizedText = initSizedText();
        this.iv = iv;
    }


    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }


    public String getCreationDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM", DeviceUtils.getLocale());
        String tmp = dateFormat.format(timestamp);
        return tmp.split("</>")[0];

    }


    public String getCreationTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", DeviceUtils.getLocale());
        String tmp = dateFormat.format(timestamp);
        return tmp;
    }


    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }


    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(int type) {
        this.type = type;
    }


    /**
     * Gets clipText.
     *
     * @return the clipText
     */
    public String getClipText() {
        return clipText;
    }


    /**
     * Sets clipText.
     *
     * @param clipText the clipText
     */
    public void setClipText(String clipText) {
        this.clipText = clipText;
    }


    private SizedText initSizedText() {
        SizedText sizedText = new SizedText();
        String tmpText = clipText;

        if (tmpText.contains("\n")) {
            sizedText.setTextSize(12f);
            tmpText = tmpText.replace("\n", "</>");
        }

        if (tmpText.length() > 20) {
            sizedText.setTextSize(12f);
        }

        if (tmpText.length() > 30) {
            sizedText.setTextSize(12f);
            tmpText = tmpText.substring(0, 29) + "...";
        }

        sizedText.setText(tmpText);
        return sizedText;
    }


    public long getIv() {
        return iv;
    }
}
