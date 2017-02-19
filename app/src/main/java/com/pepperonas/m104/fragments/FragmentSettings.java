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

package com.pepperonas.m104.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.view.View;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.concurrency.ThreadUtils;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.andbasx.system.UsabilityUtils;
import com.pepperonas.m104.AppIntroActivity;
import com.pepperonas.m104.BuildConfig;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.dialogs.DialogAnalyticsInfo;
import com.pepperonas.m104.dialogs.DialogChangelog;
import com.pepperonas.m104.dialogs.DialogDecryptDatabase;
import com.pepperonas.m104.dialogs.DialogDeleteDatabase;
import com.pepperonas.m104.dialogs.DialogLicense;
import com.pepperonas.m104.dialogs.DialogSetPassword;
import com.pepperonas.m104.model.NotificationOrder;
import com.pepperonas.materialdialog.MaterialDialog;

import java.util.concurrent.Callable;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentSettings
        extends com.github.machinarius.preferencefragment.PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private static final String TAG = "FragmentSettings";

    //    private Tracker mTracker;


    /**
     * New instance fragment settings.
     *
     * @param i the
     * @return the fragment settings
     */
    public static FragmentSettings newInstance(int i) {
        FragmentSettings fragment = new FragmentSettings();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);

        AesPrefs.setClickListenersOnPreferences(
                this,
                findPreference(getString(R.string.IS_AUTO_START)),
                findPreference(getString(R.string.SHOW_BATTERY_NOTIFICATION)),
                findPreference(getString(R.string.SHOW_NETWORK_NOTIFICATION)),
                findPreference(getString(R.string.SHOW_CLIPBOARD_NOTIFICATION)),
                findPreference(getString(R.string.ENCRYPT_CLIPBOARD)),
                findPreference(getString(R.string.DELETE_CLIPBOARD_DATABASE)),
                findPreference(getString(R.string.TOUCH_TWICE_TO_EXIT)),
                findPreference(getString(R.string.RATE_APP)),
                findPreference(getString(R.string.SHARE_APP)),
                findPreference(getString(R.string.SHOW_APP_INTRO_AGAIN)),
                findPreference(getString(R.string.LICENSE)),
                findPreference(getString(R.string.BUILD_VERSION)),
                findPreference(getString(R.string.ANALYTICS)),
                findPreference(getString(R.string.UNITS_CELSIUS))
        );

        ensureRootMode();

        addPrefIcons();

        addBuildPref();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity main = (MainActivity) getActivity();
        main.setTitle(getString(R.string.settings));

        //        initAnalytics();

        updateSummaries();
    }


    @Override
    public void onPause() {
        //        doAnalyticsOnLifecycle("onPause");
        super.onPause();
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        CheckBoxPreference cbxP;

        if (preference.getKey().equals(getString(R.string.IS_ROOT_MODE))) {
            SystemUtils.runAsRoot(new String[]{""});
            ThreadUtils.runDelayed(3000, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    CheckBoxPreference cbxP = ((CheckBoxPreference) findPreference(getString(R.string.IS_ROOT_MODE)));
                    AesPrefs.putBooleanRes(R.string.IS_ROOT_MODE, cbxP.isChecked());
                    ensureRootMode();
                    if (cbxP.isChecked()) {
                        ((MainActivity) getActivity()).addItemRoot();
                    } else ((MainActivity) getActivity()).removeItemRoot();
                    return null;
                }
            });
        } else if (preference.getKey().equals(getString(R.string.IS_AUTO_START))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.IS_AUTO_START));
            AesPrefs.putBooleanRes(R.string.IS_AUTO_START, cbxP.isChecked());
        } else if (preference.getKey().equals(getString(R.string.SHOW_BATTERY_NOTIFICATION))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.SHOW_BATTERY_NOTIFICATION));
            AesPrefs.putBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, cbxP.isChecked());
            applyNotificationState(R.string.SHOW_BATTERY_NOTIFICATION, cbxP.isChecked());
        } else if (preference.getKey().equals(getString(R.string.SHOW_NETWORK_NOTIFICATION))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.SHOW_NETWORK_NOTIFICATION));
            AesPrefs.putBooleanRes(R.string.SHOW_NETWORK_NOTIFICATION, cbxP.isChecked());
            applyNotificationState(R.string.SHOW_NETWORK_NOTIFICATION, cbxP.isChecked());
        } else if (preference.getKey().equals(getString(R.string.SHOW_CLIPBOARD_NOTIFICATION))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.SHOW_CLIPBOARD_NOTIFICATION));
            AesPrefs.putBooleanRes(R.string.SHOW_CLIPBOARD_NOTIFICATION, cbxP.isChecked());
            applyNotificationState(R.string.SHOW_CLIPBOARD_NOTIFICATION, cbxP.isChecked());
        } else if (preference.getKey().equals(getString(R.string.TOUCH_TWICE_TO_EXIT))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.TOUCH_TWICE_TO_EXIT));
            AesPrefs.putBooleanRes(R.string.TOUCH_TWICE_TO_EXIT, cbxP.isChecked());
        } else if (preference.getKey().equals(getString(R.string.ENCRYPT_CLIPBOARD))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.ENCRYPT_CLIPBOARD));
            AesPrefs.putBooleanRes(R.string.ENCRYPT_CLIPBOARD, cbxP.isChecked());
            onClickEncryptDatabase(cbxP);
        } else if (preference.getKey().equals(getString(R.string.DELETE_CLIPBOARD_DATABASE))) {
            onClickDeleteClipboardDatabase();
        } else if (preference.getKey().equals(getString(R.string.SHOW_APP_INTRO_AGAIN))) {
            AesPrefs.putBooleanRes(R.string.SHOW_APP_INTRO, true);
            startActivity(new Intent(getActivity(), AppIntroActivity.class));
        } else if (preference.getKey().equals(getString(R.string.RATE_APP))) {
            onRate();
        } else if (preference.getKey().equals(getString(R.string.SHARE_APP))) {
            onShare();
        } else if (preference.getKey().equals(getString(R.string.LICENSE))) {
            new DialogLicense(getContext());
        } else if (preference.getKey().equals(getString(R.string.BUILD_VERSION))) {
            new DialogChangelog(getContext());
        } else if (preference.getKey().equals(getString(R.string.ANALYTICS))) {
            cbxP = (CheckBoxPreference) findPreference(getString(R.string.ANALYTICS));
            if (AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true)) {
                new DialogAnalyticsInfo(getContext(), cbxP);
                return false;
            } else AesPrefs.putBooleanRes(R.string.IS_ANALYTICS, true);
        } else if (preference.getKey().equals(getString(R.string.UNITS_CELSIUS))) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.pref_title_temperature_unit)
                    .listItemsSingleSelection(true, getString(R.string.celsius), getString(R.string.fahrenheit))
                    .selection(AesPrefs.getBooleanRes(R.string.UNITS_CELSIUS, true) ? 0 : 1)
                    .itemClickListener(new MaterialDialog.ItemClickListener() {
                        @Override
                        public void onClick(View v, int position, long id) {
                            super.onClick(v, position, id);
                            AesPrefs.putBooleanRes(R.string.UNITS_CELSIUS, position == 0);
                            findPreference(getString(R.string.UNITS_CELSIUS))
                                    .setSummary(getString(AesPrefs.getBooleanRes(R.string.UNITS_CELSIUS, true) ? R.string
                                            ._unit_celsius : R.string._unit_fahrenheit));
                        }
                    })
                    .show();
        }
        return true;
    }


    /**
     * Ensure root mode.
     */
    private void ensureRootMode() {
        if (SystemUtils.isRooted()) {
            AesPrefs.setClickListenersOnPreferences(
                    this, findPreference(getString(R.string.IS_ROOT_MODE))
            );
            ((CheckBoxPreference) findPreference(getString(R.string.IS_ROOT_MODE)))
                    .setChecked(AesPrefs.getBooleanRes(R.string.IS_ROOT_MODE, false));
        } else {
            ((PreferenceGroup) findPreference("pref_cat_main"))
                    .removePreference(findPreference(getString(R.string.IS_ROOT_MODE)));
        }
    }


    /**
     * Add pref icons.
     */
    private void addPrefIcons() {
        int color = R.color.sa_teal;
        findPreference(getString(R.string.RATE_APP)).setIcon(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_star)
                .colorRes(color).sizeDp(Const.NAV_DRAWER_ICON_SIZE));
        findPreference(getString(R.string.SHARE_APP)).setIcon(new IconicsDrawable(getContext(), GoogleMaterial.Icon
                .gmd_tag_faces).colorRes(color).sizeDp(Const.NAV_DRAWER_ICON_SIZE));
        findPreference(getString(R.string.LICENSE)).setIcon(new IconicsDrawable(getContext(), CommunityMaterial.Icon
                .cmd_github_circle).colorRes(color).sizeDp(Const.NAV_DRAWER_ICON_SIZE));
        findPreference(getString(R.string.SHOW_APP_INTRO_AGAIN)).setIcon(new IconicsDrawable(getContext(), GoogleMaterial.Icon
                .gmd_info_outline).colorRes(color).sizeDp(Const.NAV_DRAWER_ICON_SIZE));
        findPreference(getString(R.string.BUILD_VERSION)).setIcon(new IconicsDrawable(getContext(), CommunityMaterial.Icon
                .cmd_leaf).colorRes(color).sizeDp(Const.NAV_DRAWER_ICON_SIZE));
    }


    /**
     * Add build pref.
     */
    private void addBuildPref() {
        Preference p = findPreference(getString(R.string.BUILD_VERSION));
        p.setTitle(R.string.pref_title_build_version);
        String summary = BuildConfig.VERSION_NAME;
        if (AesPrefs.getBooleanRes(R.string.IS_PREMIUM, false)) {
            summary += "-pro";
        }
        p.setSummary(summary);
    }


    /**
     * Update summaries.
     */
    private void updateSummaries() {
        CheckBoxPreference chbP = (CheckBoxPreference) findPreference(getString(R.string.SHOW_BATTERY_NOTIFICATION));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, true));

        chbP = (CheckBoxPreference) findPreference(getString(R.string.SHOW_NETWORK_NOTIFICATION));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.SHOW_NETWORK_NOTIFICATION, true));

        chbP = (CheckBoxPreference) findPreference(getString(R.string.SHOW_CLIPBOARD_NOTIFICATION));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.SHOW_CLIPBOARD_NOTIFICATION, true));

        chbP = (CheckBoxPreference) findPreference(getString(R.string.IS_AUTO_START));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.IS_AUTO_START, true));

        chbP = (CheckBoxPreference) findPreference(getString(R.string.ENCRYPT_CLIPBOARD));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.ENCRYPT_CLIPBOARD, false));

        chbP = (CheckBoxPreference) findPreference(getString(R.string.TOUCH_TWICE_TO_EXIT));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.TOUCH_TWICE_TO_EXIT, true));

        chbP = (CheckBoxPreference) findPreference(getString(R.string.ANALYTICS));
        chbP.setChecked(AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true));

        Preference p = findPreference(getString(R.string.UNITS_CELSIUS));
        p.setSummary(getString(AesPrefs.getBooleanRes(R.string.UNITS_CELSIUS, true) ? R.string._unit_celsius
                : R.string._unit_fahrenheit));
    }


    /**
     * On rate.
     */
    private void onRate() {
        UsabilityUtils.launchAppStore(getActivity(), "com.pepperonas.m104");
        //        doAnalyticsOnAction("onRate");
    }


    /**
     * On share.
     */
    private void onShare() {
        UsabilityUtils.launchShareAppIntent(getActivity(), "com.pepperonas.m104", getString(R.string.share_app_intro_text));
        //        doAnalyticsOnAction("onShare");
    }


    /**
     * On click encrypt database.
     *
     * @param cbxEncrypt the cbx encrypt
     */
    private void onClickEncryptDatabase(CheckBoxPreference cbxEncrypt) {
        if (cbxEncrypt.isChecked()) {
            if (AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, "").equals("")) {
                new DialogSetPassword(getContext(), cbxEncrypt, ((MainActivity) getActivity()).getDatabase());
            }
        } else new DialogDecryptDatabase(getContext(), cbxEncrypt, ((MainActivity) getActivity()).getDatabase());
    }


    /**
     * On click delete clipboard database.
     */
    private void onClickDeleteClipboardDatabase() {
        new DialogDeleteDatabase(getActivity());
    }


    /**
     * Apply notification state.
     *
     * @param which     the which
     * @param isChecked the is checked
     */
    private void applyNotificationState(int which, boolean isChecked) {
        if (isChecked) {
            ((MainActivity) getActivity()).sendBroadcastRequestBatteryInfo();
        }

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context
                .NOTIFICATION_SERVICE);
        switch (which) {
            case R.string.SHOW_BATTERY_NOTIFICATION: {
                if (isChecked) {
                    NotificationOrder.setNotificationBattery();
                } else notificationManager.cancel(Const.NOTIFICATION_BATTERY);
                break;
            }
            case R.string.SHOW_NETWORK_NOTIFICATION: {
                if (isChecked) {
                    NotificationOrder.setNotificationNetwork();
                } else notificationManager.cancel(Const.NOTIFICATION_NETWORK);
                break;
            }
            case R.string.SHOW_CLIPBOARD_NOTIFICATION: {
                if (isChecked) {
                    NotificationOrder.setNotificationClipboard();
                } else notificationManager.cancel(Const.NOTIFICATION_CLIPBOARD);
                break;
            }
        }

        NotificationOrder.callOrderedUpdate();

    }


    //    /**
    //     * Init analytics.
    //     */
    //    private void initAnalytics() {
    //        if (!AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true)) return;
    //
    //        App application = (App) getActivity().getApplication();
    //        mTracker = application.getDefaultTracker();
    //        if (mTracker != null) {
    //            mTracker.setScreenName("FragmentSettings");
    //            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    //        }
    //    }


    //    /**
    //     * Do analytics on action.
    //     *
    //     * @param action the action
    //     */
    //    private void doAnalyticsOnAction(String action) {
    //        if (!AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true) || mTracker == null) return;
    //
    //        mTracker.send(new HitBuilders.EventBuilder()
    //                .setCategory("Action")
    //                .setCustomDimension(Analyst.ANDROID_ID, SystemUtils.getAndroidId())
    //                .setAction(action)
    //                .build());
    //    }
    //
    //
    //    /**
    //     * Do analytics on lifecycle.
    //     *
    //     * @param method the method
    //     */
    //    private void doAnalyticsOnLifecycle(String method) {
    //        if (!AesPrefs.getBooleanRes(R.string.IS_ANALYTICS, true) || mTracker == null) return;
    //
    //        mTracker.send(new HitBuilders.EventBuilder()
    //                .setCategory("Lifecycle")
    //                .setLabel(method)
    //                .setCustomDimension(Analyst.PREMIUM, String.valueOf(AesPrefs.getBooleanRes(R.string.IS_PREMIUM, false)))
    //                .setCustomDimension(Analyst.BATTERY_NOTIFICATION, String.valueOf(AesPrefs.getBooleanRes(R.string
    //                        .SHOW_BATTERY_NOTIFICATION, false)))
    //                .setCustomDimension(Analyst.NETWORK_NOTIFICATION, String.valueOf(AesPrefs.getBooleanRes(R.string
    //                        .SHOW_NETWORK_NOTIFICATION, false)))
    //                .setCustomDimension(Analyst.CLIPBOARD_NOTIFICATION, String.valueOf(AesPrefs.getBooleanRes(R.string
    //                        .SHOW_CLIPBOARD_NOTIFICATION, false)))
    //                .setCustomDimension(Analyst.AUTO_START, String.valueOf(AesPrefs.getBooleanRes(R.string.IS_AUTO_START,
    // false)))
    //                .setCustomDimension(Analyst.TOUCH_TWICE, String.valueOf(AesPrefs.getBooleanRes(R.string.TOUCH_TWICE_TO_EXIT,
    //                        false)))
    //                .setCustomDimension(Analyst.ENCRYPT_CLIPBOARD, String.valueOf(AesPrefs.getBooleanRes(R.string
    //                        .ENCRYPT_CLIPBOARD, false)))
    //                .build());
    //    }

}

