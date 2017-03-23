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

package com.pepperonas.m104.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.m104.MainService;
import com.pepperonas.m104.R;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";


    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.d(TAG, "onReceive " + "");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (AesPrefs.getBoolean(ctx.getString(R.string.IS_AUTO_START), true)) {
                Log.d(TAG, "onReceive " + "--- AUTO START ---");
                Intent serviceIntent = new Intent(ctx, MainService.class);
                ctx.startService(serviceIntent);
            }
        }
    }
}
