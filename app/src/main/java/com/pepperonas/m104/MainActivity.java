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

package com.pepperonas.m104;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeTransform;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.dialogs.DialogAbout;
import com.pepperonas.m104.dialogs.DialogPermissionAccessUsageSettings;
import com.pepperonas.m104.dialogs.DialogPermissionReadPhoneState;
import com.pepperonas.m104.fragments.FragmentBatteryStats;
import com.pepperonas.m104.fragments.FragmentNetworkStats;
import com.pepperonas.m104.fragments.FragmentRoot;
import com.pepperonas.m104.fragments.FragmentSettings;
import com.pepperonas.m104.interfaces.IBatteryInformer;
import com.pepperonas.m104.model.Database;
import com.pepperonas.m104.notification.NotificationBattery;
import com.pepperonas.m104.notification.NotificationClipboard;
import com.pepperonas.m104.notification.NotificationNetwork;
import com.pepperonas.m104.receiver.AlarmReceiver;
import com.pepperonas.m104.utils.Log;
import com.pepperonas.m104.utils.StringFactory;

import static com.pepperonas.andbasx.AndBasx.getContext;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int MENU_ITEM_ROOT = 3;
    public static final int REQUEST_PERMISSION_PHONE_STATE = 1;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    /* Fragment communication */
    public IBatteryInformer mBatteryInformer;
    public FragmentNetworkStats mNetworkInformer;
    private Database mDb;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    /* Fragment */
    private Fragment mFragment;
    private NavigationView mNavView;
    private Intent mMainServiceIntent;

    private boolean mIsExitPressedOnce;

    private boolean mBtyIsCharging;
    private int mBtyLevel;

    @SuppressWarnings("FieldCanBeLocal")
    private int mBtyPlugged, mBtyStatus;

    private MenuItem mItemNetwork;
    private boolean mResumeWithNetworkFragment = false;

    //    private Tracker mTracker;

    private BroadcastReceiver mMainServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mBtyIsCharging = intent.getBooleanExtra("is_charging", false);
            mBtyLevel = intent.getIntExtra("level", 0);
            double temperature = intent.getDoubleExtra("temperature", 0);
            int voltage = intent.getIntExtra("voltage", 0);
            mBtyPlugged = intent.getIntExtra("plugged", 0);
            int health = intent.getIntExtra("health", 0);
            mBtyStatus = intent.getIntExtra("status", 0);

            if (mBatteryInformer != null) {
                mBatteryInformer.onBatteryUpdate(
                        MainActivity.this, mBtyIsCharging, mBtyLevel, temperature,
                        voltage, mBtyPlugged, health, mBtyStatus);
            } else {
                Log.w(TAG, "onReceive: Can't update battery info.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Loader.getColor(R.color.colorPrimary));
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (AesPrefs.getBooleanRes(R.string.SHOW_APP_INTRO, true)) {
            startActivity(new Intent(MainActivity.this, AppIntroActivity.class));
        }

        checkForKey();
        applyPremiumState();
        initToolbar();
        initNavView(savedInstanceState == null);
        initNavDrawer();

        mMainServiceIntent = new Intent(this, MainService.class);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            ContextCompat.startForegroundService(this, mMainServiceIntent);
        } else {
            startService(mMainServiceIntent);
        }
        sendBroadcastRequestBatteryInfo();

        final String androidId = SystemUtils.getAndroidId();

        Log.d(TAG, "onCreate " + "androidId: " + androidId);

        if (getIntent() != null && getIntent().getStringExtra("start_fragment") != null) {
            String startFragment = getIntent().getStringExtra("start_fragment");
            getIntent().removeExtra("start_fragment");
            if (startFragment.equals(NotificationNetwork.EXTRA_START_NETWORK)) {
                selectNavViewItem(mItemNetwork);
            }
        }

        //        initAnalytics();
        startAlarm();

        if (!checkPermissionReadPhoneState(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    public void startAlarm() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        //        Intent alarmIntent = new Intent("com.pepperonas.m104.START_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //        int interval = 1000 * 60 * 60 * 2; // 1h
        int interval = 60000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: Permission Granted!");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: Permission Denied!");
                }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getStringExtra("start_fragment") != null) {
            String startFragment = intent.getStringExtra("start_fragment");

            getIntent().removeExtra("start_fragment");

            Log.d(TAG, "onNewIntent " + "startFragment=" + startFragment);
            if (startFragment.equals(NotificationBattery.EXTRA_START_BATTERY)) {
                makeFragmentTransaction(FragmentBatteryStats.newInstance(0));
            }
            if (startFragment.equals(NotificationNetwork.EXTRA_START_NETWORK)) {
                selectNavViewItem(mItemNetwork);
            }
            if (startFragment.equals(NotificationClipboard.EXTRA_START_CLIPBOARD)) {
                makeFragmentTransaction(FragmentBatteryStats.newInstance(0));
            }

        } else {
            Log.w(TAG, "onCreate intent can't be resolved...");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AesPrefs.getBooleanRes(R.string.IS_ROOT_MODE, false)) {
            SystemUtils.runAsRoot(new String[]{""});
        }

        if (!SystemUtils.isServiceRunning(this, MainService.class)) {
            mMainServiceIntent = new Intent((MainActivity.this), MainService.class);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                ContextCompat.startForegroundService(this, mMainServiceIntent);
            } else {
                startService(mMainServiceIntent);
            }
        }

        registerReceiver(mMainServiceReceiver, new IntentFilter(MainService.BROADCAST_BATTERY_INFO));

        ensureResumeNetworkFragment();
    }

    private void ensureResumeNetworkFragment() {
        if (mResumeWithNetworkFragment) {
            selectNavViewItem(mItemNetwork);
        }
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(mMainServiceReceiver);
        } catch (Exception e) {
            Log.e(TAG, "onPause failed when unregister receiver");
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mDb != null) {
            mDb.close();
        }

        super.onDestroy();
    }

    /**
     * Overriding to be able to close the {@link NavigationView}.
     * when the BACK-key is touched.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            touchTwiceToExit();
        }
    }

    /**
     * Check for key.
     */
    private void checkForKey() {
        Log.i(TAG, "Checking key...");
        PackageManager manager = getPackageManager();
        if (manager.checkSignatures("com.pepperonas.m104", "com.pepperonas.m104.key") == PackageManager.SIGNATURE_MATCH) {
            AesPrefs.putBooleanRes(R.string.IS_PREMIUM, true);
        } else {
            AesPrefs.putBooleanRes(R.string.IS_PREMIUM, false);
        }
    }

    private void applyPremiumState() {
        if (!AesPrefs.getBooleanRes(R.string.IS_PREMIUM, false)) {
            AesPrefs.putBooleanRes(R.string.SHOW_BATTERY_NOTIFICATION, false);
            AesPrefs.putBooleanRes(R.string.SHOW_CLIPBOARD_NOTIFICATION, false);
        }
    }

    /**
     * Init toolbar.
     */
    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    /**
     * Init nav view.
     *
     * @param doTransaction the do transaction
     */
    private void initNavView(boolean doTransaction) {
        mNavView = findViewById(R.id.navigation_view);

        initNavDrawerIcons();
        ensureInitItemRoot();

        mNavView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(menuItem.isChecked());
                mDrawerLayout.closeDrawers();

                return selectNavViewItem(menuItem);
            }
        });

        if (doTransaction) {
            // selecting the first item
            selectNavViewItem(getNavigationView().getMenu().getItem(0).getSubMenu().getItem(0));
        }
    }

    /**
     * Init nav drawer icons.
     */
    private void initNavDrawerIcons() {
        // first sub menu
        MenuItem itemBattery = mNavView.getMenu().getItem(0).getSubMenu().getItem(0);
        mItemNetwork = mNavView.getMenu().getItem(0).getSubMenu().getItem(1);
        itemBattery.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_battery_std).colorRes(R.color.sa_teal)
                .sizeDp(Const.NAV_DRAWER_ICON_SIZE));
        mItemNetwork.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_network_wifi)
                .colorRes(R.color.sa_teal).sizeDp(Const.NAV_DRAWER_ICON_SIZE));

        // second sub menu
        MenuItem itemSettings = mNavView.getMenu().getItem(1).getSubMenu().getItem(0);
        MenuItem itemAbout = mNavView.getMenu().getItem(1).getSubMenu().getItem(1);
        itemSettings.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings).colorRes(R.color.sa_teal)
                .sizeDp(Const.NAV_DRAWER_ICON_SIZE));
        itemAbout.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_info_outline)
                .colorRes(R.color.sa_teal).sizeDp(Const.NAV_DRAWER_ICON_SIZE));
    }

    /**
     * Init nav drawer.
     */
    private void initNavDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.open, R.string.close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();

                TextView tvNavViewSubtitle = findViewById(R.id.nav_view_header_subtitle);
                tvNavViewSubtitle.setText(StringFactory.makeRemainingInfo(MainActivity.this, mBtyLevel, mBtyIsCharging));
            }
        };

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        // updating Drawer's state
        actionBarDrawerToggle.syncState();
    }

    /**
     * Send broadcast request battery info.
     * <p/>
     * Gets called when {@link FragmentBatteryStats} is shown.
     */
    public void sendBroadcastRequestBatteryInfo() {
        sendBroadcast(new Intent(MainService.BROADCAST_MAIN_STARTED));
    }

    /**
     * Select nav view item boolean.
     *
     * @param menuItem the menu item
     * @return the boolean
     */
    public boolean selectNavViewItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_item_battery_stats: {
                if (mFragment instanceof FragmentBatteryStats) {
                    return true;
                }
                makeFragmentTransaction(FragmentBatteryStats.newInstance(0));
                return true;
            }

            case R.id.nav_item_network_stats: {
                if (!checkPermissionReadPhoneState(Manifest.permission.READ_PHONE_STATE)) {
                    new DialogPermissionReadPhoneState(this);
                } else {
                    if (mFragment instanceof FragmentNetworkStats) {
                        return true;
                    }
                    int mode = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                        if (appOps != null) {
                            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), getPackageName());
                        }
                        if (mode == AppOpsManager.MODE_ALLOWED) {
                            makeFragmentTransaction(FragmentNetworkStats.newInstance(1));
                        } else {
                            new DialogPermissionAccessUsageSettings(this);
                        }
                    }
                }
                return true;
            }

            case MENU_ITEM_ROOT: {
                if (mFragment instanceof FragmentRoot) {
                    return true;
                }
                makeFragmentTransaction(FragmentRoot.newInstance(2));
                return true;
            }

            case R.id.nav_item_settings: {
                if (mFragment instanceof FragmentSettings) {
                    return true;
                }
                makeFragmentTransaction(FragmentSettings.newInstance(3));
                return true;
            }

            case R.id.nav_item_about: {
                new DialogAbout(this);
                return true;
            }
        }
        return false;
    }

    private boolean checkPermissionReadPhoneState(String permission) {
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Replace the fragment
     *
     * @param fragment the fragment
     */
    public void makeFragmentTransaction(Fragment fragment) {
        mFragment = fragment;
        android.support.v4.app.FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition in = new ChangeTransform();
            Transition out = new Slide(Gravity.BOTTOM);

            mFragment.setEnterTransition(in);
            mFragment.setExitTransition(out);
        } else {
            // older apis
            fragmentTransaction.setCustomAnimations(R.anim.anim_appear, R.anim.s_down);
        }

        fragmentTransaction.replace(R.id.main_frame, mFragment);
        fragmentTransaction.commit();
    }

    /**
     * Check double-press config and close the app if needed.
     */
    private void touchTwiceToExit() {
        if (!AesPrefs.getBooleanRes(R.string.TOUCH_TWICE_TO_EXIT, true)) {
            mIsExitPressedOnce = true;
        }
        if (mIsExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        ToastUtils.toastShort(R.string.touch_twice_to_close);

        mIsExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsExitPressedOnce = false;
            }
        }, Const.DELAY_ON_BACK_PRESSED);
    }

    /**
     * Gets navigation view.
     *
     * @return the navigation view
     */
    public NavigationView getNavigationView() {
        return mNavView;
    }

    /**
     * Gets database.
     *
     * @return the database
     */
    public Database getDatabase() {
        if (mDb == null) {
            mDb = new Database(this);
        }
        return mDb;
    }

    /**
     * Ensure init item root.
     */
    private void ensureInitItemRoot() {
        if (AesPrefs.getBooleanRes(R.string.IS_ROOT_MODE, false)) {
            if (mNavView.getMenu().getItem(0).getSubMenu().size() == 2) {
                mNavView.getMenu().getItem(0).getSubMenu()
                        .add(0, MENU_ITEM_ROOT, 2, getString(R.string.root));
                MenuItem itemRoot = mNavView.getMenu().getItem(0).getSubMenu()
                        .findItem(MENU_ITEM_ROOT);
                itemRoot.setIcon(
                        new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_android)
                                .colorRes(R.color.sa_teal).sizeDp(Const.NAV_DRAWER_ICON_SIZE));

                itemRoot.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return false;
                    }
                });
            }
        }
    }

    /**
     * Add item root.
     */
    public void addItemRoot() {
        if (mNavView.getMenu().getItem(0).getSubMenu().size() == 2) {
            mNavView.getMenu().getItem(0).getSubMenu()
                    .add(0, MENU_ITEM_ROOT, 2, getString(R.string.root));
            MenuItem itemRoot = mNavView.getMenu().getItem(0).getSubMenu().findItem(MENU_ITEM_ROOT);
            itemRoot.setIcon(new IconicsDrawable(MainActivity.this, GoogleMaterial.Icon.gmd_android)
                    .colorRes(R.color.sa_teal).sizeDp(Const.NAV_DRAWER_ICON_SIZE));

            itemRoot.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });
        }
    }

    /**
     * Remove item root.
     */
    public void removeItemRoot() {
        try {
            mNavView.getMenu().getItem(0).getSubMenu().removeItem(MENU_ITEM_ROOT);
        } catch (Exception e) {
            Log.e(TAG, "removeItemRoot " + e.getMessage());
        }
    }

    public void setResumeWithNetworkFragment(boolean resumeWithNetworkFragment) {
        mResumeWithNetworkFragment = resumeWithNetworkFragment;
    }
}
