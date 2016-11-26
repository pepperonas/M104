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

package com.pepperonas.m104.dialogs;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.jbasx.base.TextUtils;
import com.pepperonas.m104.notification.ClipboardDialogActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.model.Database;
import com.pepperonas.materialdialog.MaterialDialog;

import java.text.MessageFormat;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogEnterPassword {

    public DialogEnterPassword(final ClipboardDialogActivity cda, final Database db) {
        new MaterialDialog.Builder(cda, R.style.AppTheme_Dialog_EnterPassword)
                .customView(R.layout.dialog_enter_password)
                .title(cda.getString(R.string.dialog_enter_password_title))
                .message(cda.getString(R.string.dialog_enter_password_msg))
                .positiveText(cda.getString(R.string.ok))
                .negativeText(cda.getString(R.string.cancel))
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        EditText etInput = (EditText) dialog.findViewById(R.id.et_enter_password);

                        if (!TextUtils.isEmpty(etInput.getText().toString())) {
                            if (etInput.getText().toString().equals(AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, ""))) {
                                // password matches
                                final SeekBar sb = (SeekBar) dialog.findViewById(R.id.seekBar);

                                int mins = getMins(getPos(sb.getProgress()));
                                AesPrefs.putLongRes(R.string.LOGOUT_TIME, System.currentTimeMillis() + (mins * 1000 * 60));
                                AesPrefs.putIntRes(R.string.LAST_PROGRESS_LOCK_TIME, sb.getProgress());

                                cda.ensureInitLockButton();
                                cda.setData();
                            } else {
                                AesPrefs.putLongRes(R.string.LOGOUT_TIME, 0);
                                ToastUtils.toastShort(R.string.wrong_password);
                            }
                        } else {
                            ToastUtils.toastShort(R.string.invalid_input);
                            AesPrefs.putLongRes(R.string.LOGOUT_TIME, 0);
                        }
                    }


                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                    }
                })
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(AlertDialog dialog) {
                        super.onShow(dialog);

                        final String[] items = new String[]{
                                "1 " + cda.getString(R.string.minute),
                                "5 " + cda.getString(R.string.minutes),
                                "15 " + cda.getString(R.string.minutes),
                                "30 " + cda.getString(R.string.minutes),
                                "60 " + cda.getString(R.string.minutes)};

                        final TextView tvInfo = (TextView) dialog.findViewById(R.id.tv_password_expiration_time);
                        final SeekBar sb = (SeekBar) dialog.findViewById(R.id.seekBar);
                        sb.setEnabled(false);
                        sb.setProgress(AesPrefs.getIntRes(R.string.LAST_PROGRESS_LOCK_TIME, 85));

                        tvInfo.setEnabled(false);
                        tvInfo.setText("");

                        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                int pos = getPos(progress);
                                tvInfo.setText(MessageFormat.format("{0} {1}", cda.getString(R.string.seekbar_prompt_password_expiration_time), items[pos]));
                            }


                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }


                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        ((EditText) dialog.findViewById(R.id.et_enter_password)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                sb.setEnabled(count != 0);
                                String text;
                                if (count != 0) {
                                    text = MessageFormat.format("{0} {1}", cda.getString(R.string.seekbar_prompt_password_expiration_time), items[getPos(sb.getProgress())]);
                                } else text = "";
                                tvInfo.setText(text);
                            }


                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }


                            @Override
                            public void afterTextChanged(Editable s) {

                            }

                        });
                    }
                }).show();
    }


    private int getPos(int progress) {
        if (progress <= 20) return 0;
        if (progress > 21 && progress < 40) return 1;
        if (progress > 41 && progress < 60) return 2;
        if (progress > 61 && progress < 80) return 3;
        else return 4;
    }


    private int getMins(int selectedItemPosition) {
        switch (selectedItemPosition) {
            case 1: return 5;
            case 2: return 15;
            case 3: return 30;
            case 4: return 60;
            case 0:
            default: return 1;
        }
    }

}
