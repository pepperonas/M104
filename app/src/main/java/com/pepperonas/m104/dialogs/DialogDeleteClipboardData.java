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

package com.pepperonas.m104.dialogs;

import android.app.Activity;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogDeleteClipboardData {

    public DialogDeleteClipboardData(final Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.dialog_delete_database_title))
                .message(activity.getString(R.string.dialog_delete_database_msg))
                .icon(new IconicsDrawable(activity, CommunityMaterial.Icon.cmd_lock_reset)
                        .colorRes(R.color.dialog_icon)
                        .sizeDp(Const.NAV_DRAWER_ICON_SIZE))
                .positiveText(activity.getString(R.string.ok))
                .negativeText(activity.getString(R.string.cancel))
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        ((MainActivity) activity).getDatabase().deleteAllClips();
                        ToastUtils.toastShort(activity.getString(R.string.to_clipboard_wiped));
                    }
                })
                .show();
    }
}
