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

package com.pepperonas.m104.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.jbasx.base.TextUtils;
import com.pepperonas.m104.MainService;
import com.pepperonas.m104.R;
import com.pepperonas.m104.model.Database;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogDecryptDatabase {

    @SuppressWarnings("unused")
    private static final String TAG = "DialogDecryptDatabase";

    public DialogDecryptDatabase(@NonNull final Activity activity, final CheckBoxPreference cbxEncrypt, final Database db) {
        new MaterialDialog.Builder(activity).customView(R.layout.dialog_set_password)
                .title(activity.getString(R.string.dialog_enter_password_title))
                .message(activity.getString(R.string.dialog_enter_password_to_decrypt_msg))
                .positiveText(activity.getString(R.string.ok))
                .neutralText(R.string.reset)
                .negativeText(activity.getString(R.string.cancel))
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        EditText etInput = dialog.findViewById(R.id.et_set_password);
                        if (!TextUtils.isEmpty(etInput.getText().toString())) {
                            if (etInput.getText().toString().equals(AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, ""))) {
                                ToastUtils.toastShort(R.string.clipboard_decrypted);

                                AesPrefs.putRes(R.string.ENCRYPTION_PASSWORD, "");
                                db.decryptClipboard(etInput.getText().toString());
                            } else {
                                ToastUtils.toastShort(R.string.wrong_password);
                                cbxEncrypt.setChecked(true);
                            }
                        } else {
                            ToastUtils.toastShort(R.string.invalid_input);
                            cbxEncrypt.setChecked(true);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        cbxEncrypt.setChecked(true);
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        ToastUtils.toastLong(R.string.encryption_disabled);

                        db.deleteAllClips();
                        cbxEncrypt.setChecked(false);
                        AesPrefs.putRes(R.string.ENCRYPTION_PASSWORD, "");
                        AesPrefs.putBooleanRes(R.string.ENCRYPT_CLIPBOARD, false);

                        activity.sendBroadcast(new Intent(MainService.BROADCAST_CLIP_DELETED));
                    }
                }).show();
    }

}
