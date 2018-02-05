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

package com.pepperonas.m104.model;

import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.m104.R;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public enum ChargeMode {
    AC(0), USB(1), WL(2);

    private final int type;

    /**
     * Instantiates a new Charge mode.
     *
     * @param type the type
     */
    ChargeMode(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        switch (this.type) {
            case 0:
                return Loader.gStr(R.string.battery_plugged_ac);
            case 1:
                return Loader.gStr(R.string.battery_plugged_usb);
            case 2:
                return Loader.gStr(R.string.battery_plugged_wireless);
            default:
                return Loader.gStr(R.string.unknown);
        }
    }

    /**
     * Gets instance.
     *
     * @param which the which
     * @return the instance
     */
    public static ChargeMode getInstance(int which) {
        switch (which) {
            case 0:
                return AC;
            case 1:
                return USB;
            case 2:
                return WL;
            default:
                return null;
        }
    }
}
