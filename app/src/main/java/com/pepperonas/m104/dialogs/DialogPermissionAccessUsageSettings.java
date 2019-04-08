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

import android.content.Intent;
import android.provider.Settings;

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
public class DialogPermissionAccessUsageSettings {

    @SuppressWarnings("unused")
    private static final String TAG = "DialogPermissionAccessUsageSettings";

    public DialogPermissionAccessUsageSettings(final MainActivity mainActivity) {
        new MaterialDialog.Builder(mainActivity)
                .icon(new IconicsDrawable(mainActivity, CommunityMaterial.Icon.cmd_lock)
                        .colorRes(R.color.dialog_icon)
                        .sizeDp(Const.NAV_DRAWER_ICON_SIZE))
                .title(mainActivity.getString(R.string.dialog_set_permission_title))
                .message(R.string.dialog_set_permission_access_usage_settings)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            mainActivity.startActivity(intent);
                            mainActivity.setResumeWithNetworkFragment(true);
                        } else {
                            mainActivity.setResumeWithNetworkFragment(true);
                            ToastUtils.toastLong(R.string.android_version_incompatible);
                        }
                    }
                })
                .show();
    }

}
