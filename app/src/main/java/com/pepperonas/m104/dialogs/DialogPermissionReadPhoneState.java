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

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogPermissionReadPhoneState {

    @SuppressWarnings("unused")
    private static final String TAG = "DialogPermissionReadPhoneState";

    public DialogPermissionReadPhoneState(final Activity activity) {
        new MaterialDialog.Builder(activity)
                .icon(R.drawable.ic_launcher)
                .title(activity.getString(R.string.dialog_set_permission_title))
                .message(R.string.set_permission_read_phone_state_info)
                .icon(new IconicsDrawable(activity, CommunityMaterial.Icon.cmd_access_point)
                        .colorRes(R.color.dialog_icon)
                        .sizeDp(Const.NAV_DRAWER_ICON_SIZE))
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, MainActivity.REQUEST_PERMISSION_PHONE_STATE);
                    }
                })
                .show();
    }

}
