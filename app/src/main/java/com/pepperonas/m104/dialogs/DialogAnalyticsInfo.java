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

import android.content.Context;
import android.preference.CheckBoxPreference;

import com.pepperonas.aesprefs.AesPrefs;
import com.pepperonas.m104.R;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DialogAnalyticsInfo {

    boolean mDoUncheck = false;


    public DialogAnalyticsInfo(Context ctx, final CheckBoxPreference cbxP) {
        new MaterialDialog.Builder(ctx)
                .title(ctx.getString(R.string.dialog_analytics_title))
                .message(ctx.getString(R.string.dialog_analytics_msg))
                .positiveText(ctx.getString(R.string.ok))
                .negativeText(ctx.getString(R.string.cancel))
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mDoUncheck = false;
                    }


                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        mDoUncheck = true;
                    }
                })
                .dismissListener(new MaterialDialog.DismissListener() {
                    @Override
                    public void onDismiss() {
                        super.onDismiss();
                        cbxP.setChecked(mDoUncheck);
                        AesPrefs.putBooleanRes(R.string.IS_ANALYTICS, mDoUncheck);
                    }
                })
                .show();
    }

}