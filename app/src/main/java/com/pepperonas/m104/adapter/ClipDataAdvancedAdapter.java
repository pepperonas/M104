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

package com.pepperonas.m104.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.ClipboardUtils;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.m104.R;
import com.pepperonas.m104.model.ClipDataAdvanced;
import com.pepperonas.m104.model.Database;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class ClipDataAdvancedAdapter extends
    RecyclerView.Adapter<ClipDataAdvancedAdapter.ClipDataAdvancedViewHolder> {

    private Activity mAct;
    private List<ClipDataAdvanced> mClips = new ArrayList<>();

    private Database mDb;


    public ClipDataAdvancedAdapter(Activity activity, Database database,
        List<ClipDataAdvanced> clips) {
        mAct = activity;
        this.mDb = database;
        this.mClips = clips;
    }


    @Override
    public int getItemCount() {
        return mClips.size();
    }


    @Override
    public ClipDataAdvancedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_clip_data, parent, false);
        return new ClipDataAdvancedViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ClipDataAdvancedViewHolder holder, final int pos) {
        holder.icon.setImageDrawable(mAct.getResources().getDrawable(R.drawable.ic_launcher));

        holder.ivClipDataText.setTextSize(mClips.get(pos).getSizedText().getTextSize());
        holder.ivClipDataText.setText(mClips.get(pos).getSizedText().getText());

        holder.tvTsDate.setText(mClips.get(pos).getCreationDate());
        holder.tvTsTime.setText(mClips.get(pos).getCreationTime());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, "").equals("")
                    || AesPrefs.getLongRes(R.string.LOGOUT_TIME, 0) > System.currentTimeMillis()) {
                    //                    mDb.deleteClipData(mClips.get(pos).getTimestamp());
                    mDb.deleteClipData(mClips.get(pos).getClipText());
                    ClipboardUtils.setClipboard(mClips.get(pos).getClipText());
                    ToastUtils.toastShort(mAct.getString(R.string.copied_to_clipboard));
                    mAct.finish();
                } else {
                    ToastUtils.toastShort(mAct.getString(R.string.encrypted_text_cant_be_copied));
                }
            }
        });
    }


    public class ClipDataAdvancedViewHolder extends RecyclerView.ViewHolder {

        private CardView cv;
        private ImageView icon;
        private TextView ivClipDataText;
        private TextView tvTsDate;
        private TextView tvTsTime;


        public ClipDataAdvancedViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.clip_data_card_container);
            icon = (ImageView) itemView.findViewById(R.id.iv_clip_data_card_icon);
            ivClipDataText = (TextView) itemView.findViewById(R.id.tv_card_clip_data_text);
            tvTsDate = (TextView) itemView.findViewById(R.id.tv_clip_data_created_date);
            tvTsTime = (TextView) itemView.findViewById(R.id.tv_clip_data_created_time);
        }
    }
}
