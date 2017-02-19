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

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pepperonas.m104.R;
import com.pepperonas.m104.model.RootBatteryStat;

import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class RootBatteryAdapter extends RecyclerView.Adapter<RootBatteryAdapter.InstalledAppViewHolder> {

    private static final int ICON_SIZE = 16;

    private List<RootBatteryStat> mRootBatteryStats;


    public RootBatteryAdapter(Context ctx, List<RootBatteryStat> rootBatteryStats) {

        this.mRootBatteryStats = rootBatteryStats;

        @ColorRes int color = R.color.stock_android_accent;

        //        mDrawableRx = new IconicsDrawable(ctx, Typeicons.Icon.typ_arrow_sorted_down).colorRes(color).sizeDp(ICON_SIZE);
        //        mDrawableTx = new IconicsDrawable(ctx, Typeicons.Icon.typ_arrow_sorted_up).colorRes(color).sizeDp(ICON_SIZE);
        //        mDrawableTotal = new IconicsDrawable(ctx, Typeicons.Icon.typ_arrow_unsorted).colorRes(color).sizeDp(ICON_SIZE);

    }


    @Override
    public int getItemCount() {
        return mRootBatteryStats.size();
    }


    @Override
    public InstalledAppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_app_root_battery, parent, false);
        return new InstalledAppViewHolder(v);
    }


    @Override
    public void onBindViewHolder(InstalledAppViewHolder holder, final int pos) {
        //        setAnimation(holder.cv, pos);

        //        holder.icon.setImageDrawable(mRootBatteryStats.get(pos).getIcon());
        //        holder.ivL.setImageDrawable(mDrawableRx);
        //        holder.ivC.setImageDrawable(mDrawableTx);
        //        holder.ivR.setImageDrawable(mDrawableTotal);
        holder.tvAppName.setText(mRootBatteryStats.get(pos).getApplicationName());
        //        holder.tvL.setText(mRootBatteryStats.get(pos).getFormattedRxBytes());
        //        holder.tvC.setText(mRootBatteryStats.get(pos).getFormattedTxBytes());
        //        holder.tvR.setText(mRootBatteryStats.get(pos).getFormattedTotalBytes());

    }


    //    @Override
    //    public void onViewDetachedFromWindow(InstalledAppViewHolder holder) {
    //        super.onViewDetachedFromWindow(holder);
    //        holder.cv.clearAnimation();
    //    }


    //    private void setAnimation(View viewToAnimate, int position) {
    //        Animation animation = AnimationUtils
    //                .loadAnimation(mCtx, (position > -1) ? R.anim.fade_in_fast : R.anim.fade_out_fast);
    //        viewToAnimate.startAnimation(animation);
    //    }


    public class InstalledAppViewHolder extends RecyclerView.ViewHolder {

        private CardView cv;
        private ImageView ivL;
        private ImageView ivC;
        private ImageView icon;
        private ImageView ivR;
        private TextView tvAppName;
        private TextView tvL;
        private TextView tvC;
        private TextView tvR;


        public InstalledAppViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_root_battery);
            icon = (ImageView) itemView.findViewById(R.id.iv_root_battery_app_icon);
            ivL = (ImageView) itemView.findViewById(R.id.iv_root_battery_l);
            ivC = (ImageView) itemView.findViewById(R.id.iv_root_battery_c);
            ivR = (ImageView) itemView.findViewById(R.id.iv_root_battery_r);
            tvAppName = (TextView) itemView.findViewById(R.id.tv_card_root_battery_app_name);
            tvL = (TextView) itemView.findViewById(R.id.tv_card_root_battery_l);
            tvC = (TextView) itemView.findViewById(R.id.tv_card_root_battery_c);
            tvR = (TextView) itemView.findViewById(R.id.tv_card_root_battery_r);
        }
    }
}
