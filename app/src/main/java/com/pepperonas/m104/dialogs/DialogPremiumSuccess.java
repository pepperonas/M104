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

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogPremiumSuccess {

    public DialogPremiumSuccess(final MainActivity mainActivity) {

        AesPrefs.putBooleanRes(R.string.SHOW_DIALOG_SUCCESS, false);

        new MaterialDialog.Builder(mainActivity).title(R.string.dialog_promo_success_title)
                .message(R.string.dialog_promo_success_msg).positiveText(R.string.ok)
                .dismissListener(new MaterialDialog.DismissListener() {
                    @Override
                    public void onDismiss() {
                        super.onDismiss();
                        mainActivity.recreate();
                    }
                }).show();
    }

}
