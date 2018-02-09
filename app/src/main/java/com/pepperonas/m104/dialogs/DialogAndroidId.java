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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.materialdialog.MaterialDialog;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
class DialogAndroidId {

    DialogAndroidId(final Context context) {
        new MaterialDialog.Builder(context)
                .title(context.getString(R.string.dialog_android_id_title))
                .message(context.getString(R.string.dialog_android_id_msg))
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_information)
                        .colorRes(R.color.dialog_icon)
                        .sizeDp(Const.NAV_DRAWER_ICON_SIZE))
                .customView(R.layout.dialog_android_id)
                .showListener(new MaterialDialog.ShowListener() {
                    @Override
                    public void onShow(final AlertDialog dialog) {
                        super.onShow(dialog);
                        TextView tvAndroidId = dialog
                                .findViewById(R.id.tv_dialog_android_id_id_info);
                        tvAndroidId.setText(SystemUtils.getAndroidId());
                        tvAndroidId.setClickable(true);
                        tvAndroidId.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendKey(context, SystemUtils.getAndroidId());
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .dismissListener(new MaterialDialog.DismissListener() {
                    @Override
                    public void onDismiss() {
                        super.onDismiss();
                    }
                })
                .canceledOnTouchOutside(false)
                .show();
    }

    private void sendKey(Context ctx, String key) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "" + key);
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }

}
