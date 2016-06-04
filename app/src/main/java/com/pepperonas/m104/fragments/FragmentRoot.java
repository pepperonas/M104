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

package com.pepperonas.m104.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.datatype.InstalledApp;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.m104.R;
import com.pepperonas.m104.adapter.ViewPagerAdapter;
import com.pepperonas.m104.model.InstalledBasic;
import com.pepperonas.m104.fragments.tabs.SlidingTabLayout;
import com.pepperonas.m104.interfaces.IInstalledBasicsCommunicator;
import com.pepperonas.m104.utils.Filter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class FragmentRoot extends Fragment implements IInstalledBasicsCommunicator {

    private static final String TAG = "FragmentBatteryStats";

    private static final String FILE_WAKELOCK = "wakelock";

    private static final String FILE_FREQ_CUR = "freq_cur";
    private static final String FILE_FREQ_MIN = "freq_min";
    private static final String FILE_FREQ_MAX = "freq_max";

    // list all services: adb shell service list

    // all...
    private static final String FILE_DUMB_SYS = "DUMB_SYS"; // dumbsys

    // adb shell dumpsys [SERVICE] (options --hours 3)
    public static final String FILE_PROC_ACT = "proc_act"; // activity
    public static final String FILE_PROC_STS = "proc_sts"; // procstats
    public static final String FILE_MEM_INFO = "mem_info"; // meminfo

    public static final String FILE_USGE_STS = "usge_sts"; // usagestats
    public static final String FILE_CPU_INFO = "cpu_info"; // cpuinfo
    public static final String FILE_NWK_INFO = "nwk_info"; // network_management
    public static final String FILE_ALARMSTS = "alrm_sts"; // alarm
    public static final String FILE_BTRY_STS = "btry_sts"; // batterystats

    public static final int AMOUNT_OF_TABS = 8;

    private Toolbar mToolbar;
    private float mToolbarElevation;

    private CharSequence mTitles[];

    private List<InstalledBasic> mInstalledBasics;


    public static FragmentRoot newInstance(int i) {
        FragmentRoot fragment = new FragmentRoot();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_root, container, false);
        getActivity().setTitle(getString(R.string.root));
        mTitles = new CharSequence[]{
                getString(R.string.tab1), getString(R.string.tab2),
                getString(R.string.tab3), getString(R.string.tab4),
                getString(R.string.tab5), getString(R.string.tab6),
                getString(R.string.tab7), getString(R.string.tab8)
        };

        new LoaderTask().execute("");

        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarElevation = mToolbar.getElevation();
            mToolbar.setElevation(0);
        }

        final ViewPagerAdapter adapter = new ViewPagerAdapter(this, getActivity().getSupportFragmentManager(), mTitles);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }


            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: adapter.getItem(position);
                        break;
                    case 1: adapter.getItem(position);
                        break;
                    case 2: adapter.getItem(position);
                        break;
                    case 3: adapter.getItem(position);
                        break;
                    case 4: adapter.getItem(position);
                        break;
                    case 5: adapter.getItem(position);
                        break;
                    case 6: adapter.getItem(position);
                        break;
                    case 7: adapter.getItem(position);
                        break;
                }
            }


            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        // TODO: check!
        pager.setOffscreenPageLimit(0);

        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setViewPager(pager);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(mToolbarElevation);
        }
    }


    @Override
    public List<InstalledBasic> onInstalledBasicsRequested() {
        return mInstalledBasics;
    }


    class LoaderTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... arg) {
            try {

                mInstalledBasics = new ArrayList<>();

                for (InstalledApp installedApp : SystemUtils.getInstalledApps()) {
                    String tmpAppName = installedApp.getApplicationName();
                    int tmpUid = installedApp.getApplicationInfo().uid;

                    if (Filter.filterApps(installedApp, tmpAppName, tmpUid)) continue;

                    mInstalledBasics.add(new InstalledBasic(
                            installedApp.getApplicationInfo(),
                            installedApp.getApplicationName(),
                            installedApp.getApplicationInfo()
                                        .loadIcon(getActivity().getPackageManager())
                    ));
                }


                /*Wakelocks*/
                FileOutputStream fos = AndBasx.getContext().openFileOutput(FILE_WAKELOCK, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Current frequency*/
                fos = AndBasx.getContext().openFileOutput(FILE_FREQ_CUR, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Min frequency*/
                fos = AndBasx.getContext().openFileOutput(FILE_FREQ_MIN, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Max frequency*/
                fos = AndBasx.getContext().openFileOutput(FILE_FREQ_MAX, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();


                /*Process Activity*/
                fos = AndBasx.getContext().openFileOutput(FILE_PROC_ACT, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Process Stats*/
                fos = AndBasx.getContext().openFileOutput(FILE_PROC_STS, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Mem-Info*/
                fos = AndBasx.getContext().openFileOutput(FILE_MEM_INFO, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();


                /*Usage-Stats*/
                fos = AndBasx.getContext().openFileOutput(FILE_USGE_STS, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*CPU-Info*/
                fos = AndBasx.getContext().openFileOutput(FILE_CPU_INFO, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Network-Info*/
                fos = AndBasx.getContext().openFileOutput(FILE_NWK_INFO, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Alarm-Stats*/
                fos = AndBasx.getContext().openFileOutput(FILE_ALARMSTS, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

                /*Battery-Stats*/
                fos = AndBasx.getContext().openFileOutput(FILE_BTRY_STS, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String uid = String.valueOf(AndBasx.getContext().getPackageManager().getApplicationInfo("com.pepperonas.m104", PackageManager.GET_META_DATA).uid);

                /*Wakelocks*/
                SystemUtils.runAsRoot(new String[]{"cat /sys/kernel/debug/wakeup_sources > /data/user/0/com.pepperonas.m104/files/" + FILE_WAKELOCK});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_WAKELOCK});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_WAKELOCK});


                /*Current frequency*/
                SystemUtils.runAsRoot(new String[]{"cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq > /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_CUR});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_CUR});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_CUR});

                /*Min frequency*/
                SystemUtils.runAsRoot(new String[]{"cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq > /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_MIN});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_MIN});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_MIN});

                /*Max frequency*/
                SystemUtils.runAsRoot(new String[]{"cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq > /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_MAX});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_MAX});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_FREQ_MAX});


                /*Process Activity*/
                SystemUtils.runAsRoot(new String[]{"dumpsys activity > /data/user/0/com.pepperonas.m104/files/" + FILE_PROC_ACT});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_PROC_ACT});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_PROC_ACT});

                /*Process Stats*/
                SystemUtils.runAsRoot(new String[]{"dumpsys procstats > /data/user/0/com.pepperonas.m104/files/" + FILE_PROC_STS});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_PROC_STS});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_PROC_STS});

                /*Mem-Info*/
                SystemUtils.runAsRoot(new String[]{"dumpsys meminfo > /data/user/0/com.pepperonas.m104/files/" + FILE_MEM_INFO});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_MEM_INFO});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_MEM_INFO});


                /*Usage-Stats*/
                SystemUtils.runAsRoot(new String[]{"dumpsys usagestats > /data/user/0/com.pepperonas.m104/files/" + FILE_USGE_STS});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_USGE_STS});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_USGE_STS});

                /*CPU-Info*/
                SystemUtils.runAsRoot(new String[]{"dumpsys cpuinfo > /data/user/0/com.pepperonas.m104/files/" + FILE_CPU_INFO});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_CPU_INFO});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_CPU_INFO});

                /*Network-Info*/
                SystemUtils.runAsRoot(new String[]{"dumpsys network_management > /data/user/0/com.pepperonas.m104/files/" + FILE_NWK_INFO});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_NWK_INFO});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_NWK_INFO});

                /*Alarm-Stats*/
                SystemUtils.runAsRoot(new String[]{"dumpsys alarm > /data/user/0/com.pepperonas.m104/files/" + FILE_ALARMSTS});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_ALARMSTS});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_ALARMSTS});

                /*Battery-Stats*/
                SystemUtils.runAsRoot(new String[]{"dumpsys batterystats > /data/user/0/com.pepperonas.m104/files/" + FILE_BTRY_STS});
                SystemUtils.runAsRoot(new String[]{"chmod 660 /data/user/0/com.pepperonas.m104/files/" + FILE_BTRY_STS});
                SystemUtils.runAsRoot(new String[]{"chown " + uid + "." + uid + " /data/user/0/com.pepperonas.m104/files/" + FILE_BTRY_STS});

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String arg) {
            try {
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute " + e.getMessage());
            }
        }

    }


    public List<InstalledBasic> getInstalledBasics() {
        return mInstalledBasics;
    }
}