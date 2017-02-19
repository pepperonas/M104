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

package com.pepperonas.m104.dialogs;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogTestPhaseExpired {

    public DialogTestPhaseExpired(final MainActivity mainActivity) {
        new MaterialDialog.Builder(mainActivity)
                .title(mainActivity.getString(R.string.dialog_test_phase_expired_title))
                .message(mainActivity.getString(R.string.dialog_test_phase_expired_msg))
                .positiveText(mainActivity.getString(R.string.ok))
                .negativeText(mainActivity.getString(R.string.cancel))
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(AlertDialog dialog) {
                        super.onShow(dialog);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                new DialogAndroidId(mainActivity);
                                return false;
                            }
                        });
                    }
                })
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google" +
                                ".com/store/apps/details?id=" + "com.pepperonas.m104.key")));
                    }

                })
                .dismissListener(new MaterialDialog.DismissListener() {
                    @Override
                    public void onDismiss() {
                        super.onDismiss();

                        NotificationManager notificationManager = (NotificationManager) mainActivity.getSystemService(Context
                                .NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();

                        mainActivity.finish();
                    }
                })
                .show();
    }

}
