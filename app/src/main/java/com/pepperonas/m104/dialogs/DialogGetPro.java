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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class DialogGetPro {

    public DialogGetPro(final Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.dialog_get_pro_title))
                .message(activity.getString(R.string.dialog_get_pro_msg))
                .icon(new IconicsDrawable(activity, CommunityMaterial.Icon.cmd_coin)
                        .colorRes(R.color.dialog_icon)
                        .sizeDp(Const.NAV_DRAWER_ICON_SIZE))
                .positiveText(activity.getString(R.string.ok))
                .negativeText(activity.getString(R.string.cancel))
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(AlertDialog dialog) {
                        super.onShow(dialog);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                new DialogAndroidId(activity);
                                return false;
                            }
                        });
                    }
                })
                .buttonCallback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        activity.startActivity(new Intent("android.intent.action.VIEW",
                                Uri.parse("http://play.google" + ".com/store/apps/details?id=com.pepperonas.m104.key")));
                    }
                })
                .show();
    }

}
