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

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.widget.EditText;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.jbasx.base.TextUtils;
import com.pepperonas.m104.R;
import com.pepperonas.m104.model.Database;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogDecryptDatabase {

    private boolean mDoCheck = false;


    public DialogDecryptDatabase(Context ctx, final CheckBoxPreference cbxEncrypt,
        final Database db) {
        new MaterialDialog.Builder(ctx).customView(R.layout.dialog_set_password)
            .title(ctx.getString(R.string.dialog_enter_password_title))
            .message(ctx.getString(R.string.dialog_enter_password_to_decrypt_msg))
            .positiveText(ctx.getString(R.string.ok)).negativeText(ctx.getString(R.string.cancel))
            .buttonCallback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    super.onPositive(dialog);

                    EditText etInput = (EditText) dialog.findViewById(R.id.et_set_password);
                    if (!TextUtils.isEmpty(etInput.getText().toString())) {
                        if (etInput.getText().toString()
                            .equals(AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, ""))) {
                            ToastUtils.toastShort(R.string.clipboard_decrypted);

                            AesPrefs.putRes(R.string.ENCRYPTION_PASSWORD, "");
                            mDoCheck = false;
                            db.decryptClipboard(etInput.getText().toString());

                        } else {
                            ToastUtils.toastShort(R.string.wrong_password);
                        }
                    } else {
                        ToastUtils.toastShort(R.string.invalid_input);
                    }
                }


                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);

                    mDoCheck = false;
                    cbxEncrypt.setChecked(false);
                    AesPrefs.putBooleanRes(R.string.ENCRYPT_CLIPBOARD, false);
                }
            }).dismissListener(new MaterialDialog.DismissListener() {
            @Override
            public void onDismiss() {
                super.onDismiss();

                cbxEncrypt.setChecked(mDoCheck);
                AesPrefs.putBooleanRes(R.string.ENCRYPT_CLIPBOARD, mDoCheck);
            }
        }).show();
    }

}
