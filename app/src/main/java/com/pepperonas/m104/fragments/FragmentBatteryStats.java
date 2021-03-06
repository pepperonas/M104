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

package com.pepperonas.m104.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.interfaces.IBatteryInformer;
import com.pepperonas.m104.utils.BatteryUtils;
import com.pepperonas.m104.utils.StringFactory;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class FragmentBatteryStats extends Fragment implements IBatteryInformer {

    @SuppressWarnings("unused")
    private static final String TAG = "FragmentBatteryStats";

    private TextView mTvHead;

    private TextView mTvStatus;
    private TextView mTvCurrent_mAh;
    private TextView mTvPercent;
    private TextView mTvTemperature;

    private TextView mTvPercentPerHour;
    private TextView mTvVolt;
    private TextView mTvAbs_mAh;
    private TextView mTvHealth;

    private TextView mTvScreenOnBoot;
    private TextView mTvScreenOnGlob;
    private TextView mTvScreenOffBoot;
    private TextView mTvScreenOffGlob;

    public static FragmentBatteryStats newInstance(int i) {
        FragmentBatteryStats fragment = new FragmentBatteryStats();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery_stats, container, false);
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.menu_battery_stats));
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cv1 = view.findViewById(R.id.cv_01);
        CardView cv2 = view.findViewById(R.id.cv_02);
        CardView cv3 = view.findViewById(R.id.cv_03);

        cv1.setCardBackgroundColor(Loader.getColor(R.color.sa_bg));
        cv2.setCardBackgroundColor(Loader.getColor(R.color.sa_bg));
        cv3.setCardBackgroundColor(Loader.getColor(R.color.sa_bg));

        mTvHead = view.findViewById(R.id.tv_head);

        mTvStatus = view.findViewById(R.id.tv_status);
        mTvCurrent_mAh = view.findViewById(R.id.tv_current_mah);
        mTvPercent = view.findViewById(R.id.tv_percent);
        if (getActivity() != null) {
            mTvTemperature = getActivity().findViewById(R.id.tv_temperature);
        }

        mTvPercentPerHour = view.findViewById(R.id.tv_percent_per_hour);
        mTvVolt = view.findViewById(R.id.tv_volt);
        mTvAbs_mAh = view.findViewById(R.id.tv_abs_mah);
        mTvHealth = view.findViewById(R.id.tv_health);

        mTvScreenOnBoot = getActivity().findViewById(R.id.tv_screen_on_boot_value);
        mTvScreenOnGlob = getActivity().findViewById(R.id.tv_screen_on_global_value);
        mTvScreenOffBoot = getActivity().findViewById(R.id.tv_screen_off_boot_value);
        mTvScreenOffGlob = getActivity().findViewById(R.id.tv_screen_off_global_value);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // register the battery informer
        if (getActivity() != null) {
            ((MainActivity) getActivity()).mBatteryInformer = this;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            ((MainActivity) getActivity()).sendBroadcastRequestBatteryInfo();
        }
    }

    @Override
    public void onBatteryUpdate(Context ctx, boolean isCharging, int level, double temperature, int voltage, int plugged, int health, int status) {
        mTvHead.setText(StringFactory.makeRemainingInfo(ctx, level, isCharging));
        mTvStatus.setText(StringFactory.makeStatusInfo(ctx, status, plugged));
        mTvCurrent_mAh.setText(StringFactory.makeRelative_mAhInfo(ctx, BatteryUtils.getCharge()));
        mTvPercent.setText(StringFactory.makeLevelInfo(ctx, level));
        mTvTemperature.setText(StringFactory.makeTemperatureInfo(ctx, temperature));

        mTvPercentPerHour.setText(StringFactory.makePercentagePerHourInfo(ctx, isCharging));
        mTvVolt.setText(StringFactory.makeVoltage(voltage));
        mTvAbs_mAh.setText(StringFactory.makeAbsolute_mAhValueInfo(ctx, level));
        mTvHealth.setText(StringFactory.makeHealthInfo(ctx, health));

        mTvScreenOnBoot.setText(StringFactory.formatRemaining(AesPrefs.getLongRes(R.string.CYCLIC_SCREEN_ON_VALUE, 0)));
        mTvScreenOffBoot.setText(StringFactory.formatRemaining(AesPrefs.getLongRes(R.string.CYCLIC_SCREEN_OFF_VALUE, 0)));
        mTvScreenOnGlob.setText(StringFactory.formatRemaining(AesPrefs.getLongRes(R.string.GLOBAL_SCREEN_ON_VALUE, 0)));
        mTvScreenOffGlob.setText(StringFactory.formatRemaining(AesPrefs.getLongRes(R.string.GLOBAL_SCREEN_OFF_VALUE, 0)));
    }

}
