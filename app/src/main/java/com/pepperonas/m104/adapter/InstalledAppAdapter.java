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

package com.pepperonas.m104.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.typeicons_typeface_library.Typeicons;
import com.pepperonas.m104.R;
import com.pepperonas.m104.model.InstalledAppSortable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.InstalledAppViewHolder> {

    private static final int ICON_SIZE = 16;

    private Context mCtx;

    private List<InstalledAppSortable> mInstalledApps = new ArrayList<>();

    private Drawable mDrawableRx;
    private Drawable mDrawableTx;
    private Drawable mDrawableTotal;


    public InstalledAppAdapter(Context ctx, List<InstalledAppSortable> mInstalledApps) {
        mCtx = ctx;

        @ColorRes int color = R.color.stock_android_accent;

        mDrawableRx = new IconicsDrawable(ctx, Typeicons.Icon.typ_arrow_sorted_down).colorRes(color).sizeDp(ICON_SIZE);

        mDrawableTx = new IconicsDrawable(ctx, Typeicons.Icon.typ_arrow_sorted_up).colorRes(color).sizeDp(ICON_SIZE);

        mDrawableTotal = new IconicsDrawable(ctx, Typeicons.Icon.typ_arrow_unsorted).colorRes(color).sizeDp(ICON_SIZE);

        this.mInstalledApps = mInstalledApps;
    }


    @Override
    public int getItemCount() {
        return mInstalledApps.size();
    }


    @Override
    public InstalledAppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_app_network_stat, parent, false);
        return new InstalledAppViewHolder(v);
    }


    @Override
    public void onBindViewHolder(InstalledAppViewHolder holder, final int pos) {
        setAnimation(mCtx, holder.cv, pos);

        holder.icon.setImageDrawable(mInstalledApps.get(pos).getIcon());
        holder.ivRx.setImageDrawable(mDrawableRx);
        holder.ivTx.setImageDrawable(mDrawableTx);
        holder.ivTrafficTotal.setImageDrawable(mDrawableTotal);
        holder.tvAppName.setText(mInstalledApps.get(pos).getApplicationName());
        holder.tvRx.setText(mInstalledApps.get(pos).getFormattedRxBytes());
        holder.tvTx.setText(mInstalledApps.get(pos).getFormattedTxBytes());
        holder.tvTrafficTotal.setText(mInstalledApps.get(pos).getFormattedTotalBytes());

    }


    @Override
    public void onViewDetachedFromWindow(InstalledAppViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.cv.clearAnimation();
    }


    private void setAnimation(Context ctx, View viewToAnimate, int position) {
        Animation animation = AnimationUtils
                .loadAnimation(ctx, (position > -1) ? R.anim.fade_in_fast : R.anim.fade_out_fast);
        viewToAnimate.startAnimation(animation);
    }


    public class InstalledAppViewHolder extends RecyclerView.ViewHolder {

        private CardView cv;
        private ImageView ivRx;
        private ImageView ivTx;
        private ImageView icon;
        private ImageView ivTrafficTotal;
        private TextView tvAppName;
        private TextView tvRx;
        private TextView tvTx;
        private TextView tvTrafficTotal;


        public InstalledAppViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_installed_app);
            icon = (ImageView) itemView.findViewById(R.id.iv_network_card_app_icon);
            ivRx = (ImageView) itemView.findViewById(R.id.iv_rx);
            ivTx = (ImageView) itemView.findViewById(R.id.iv_tx);
            ivTrafficTotal = (ImageView) itemView.findViewById(R.id.iv_total);
            tvAppName = (TextView) itemView.findViewById(R.id.tv_card_network_app_name);
            tvRx = (TextView) itemView.findViewById(R.id.tv_card_network_traffic_rx);
            tvTx = (TextView) itemView.findViewById(R.id.tv_card_network_traffic_tx);
            tvTrafficTotal = (TextView) itemView.findViewById(R.id.tv_card_network_traffic_total);
        }
    }
}
