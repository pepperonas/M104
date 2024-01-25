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

package com.pepperonas.m104.config;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class Const {

    public static final String CHANNEL_ID = "com.pepperonas.m104.notification";

    public static final float DIALOG_DIM_AMOUNT = 0.67f;
    public static final float RELATIVE_DIALOG_WIDTH = 0.8f;

    public static final int NOTIFICATION_BATTERY = 1;
    public static final int NOTIFICATION_NETWORK = 2;
    public static final int NOTIFICATION_CLIPBOARD = 3;

    public static int DIALOG_NETWORK_HISTORY_IN_MINUTES = 5;

    public static final int VALUE_UNSET = -1;

    public static final int DEFAULT_RANGE_IN_HOURS = 24;

    public static final int DEFAULT_MAX_CLIPS_IN_RECYCLER = Integer.MAX_VALUE;
    public static final int DEFAULT_NWK_RECORD_INTERVAL = 2;
    public static final long DELAY_ON_BACK_PRESSED = 2000L;
    public static final int NAV_DRAWER_ICON_SIZE = 24;

    public static final int DELETE_NWK_STATS_OLDER_THAN_IN_HOURS = 12;
    public static final int TEST_PERIOD_IN_DAYS = 21;
}
