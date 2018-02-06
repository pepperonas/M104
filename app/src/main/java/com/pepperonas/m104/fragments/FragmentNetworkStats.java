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

package com.pepperonas.m104.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.concurrency.ThreadUtils;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.adapter.InstalledAppAdapter;
import com.pepperonas.m104.custom.CustomRecyclerView;
import com.pepperonas.m104.model.InstalledAppM104;
import com.pepperonas.m104.model.InstalledAppSortable;
import com.pepperonas.m104.utils.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class FragmentNetworkStats extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = "FragmentNetworkStats";

    private CustomRecyclerView mRecyclerView;

    public static FragmentNetworkStats newInstance(int i) {
        FragmentNetworkStats fragment = new FragmentNetworkStats();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // register the network informer
        ((MainActivity) context).mNetworkInformer = this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_network_stats, container, false);
        if (getActivity() != null) {
            (getActivity()).setTitle(getString(R.string.menu_network_stats));
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle sis) {
        super.onViewCreated(view, sis);

        try {
            if (getActivity() != null) {
                (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated " + e.getMessage());
        }

        try {
            ThreadUtils.runDelayed(300, new Callable<Void>() {
                @Override
                public Void call() {
                    (getActivity().findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                    return null;
                }
            });

            mRecyclerView = view.findViewById(R.id.recycler_view_network_stats);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);

            new AppLoaderTask().execute("");

        } catch (Exception e) {
            ToastUtils.toastShort(R.string.error_loading_data);
        }
    }

    public List<InstalledAppM104> getInstalledApps() {
        List<InstalledAppM104> installedApps = new ArrayList<>();

        if (getActivity() != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(Context.NETWORK_STATS_SERVICE);

            PackageManager packageManager = AndBasx.getContext().getApplicationContext().getPackageManager();
            for (ApplicationInfo ai : SystemUtils.getInstalledAppInfos()) {
                NetworkStats.Bucket bucketWifi = new NetworkStats.Bucket();
                NetworkStats.Bucket bucketMobile = new NetworkStats.Bucket();
                try {

                    NetworkStats networkStatsWifi = null;
                    NetworkStats networkStatsMobile = null;
                    try {
                        if (networkStatsManager != null) {
                            networkStatsWifi = networkStatsManager.queryDetailsForUid(
                                    ConnectivityManager.TYPE_WIFI, "",
                                    0, System.currentTimeMillis(), ai.uid);

                            networkStatsMobile = networkStatsManager.queryDetailsForUid(
                                    ConnectivityManager.TYPE_MOBILE,
                                    getSubscriberId(getContext(), ConnectivityManager.TYPE_MOBILE),
                                    0, System.currentTimeMillis(), ai.uid);
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "getInstalledApps: ", e);
                    }
                    if (networkStatsWifi != null) {
                        networkStatsWifi.getNextBucket(bucketWifi);
                    }
                    if (networkStatsMobile != null) {
                        networkStatsMobile.getNextBucket(bucketMobile);
                    }
                    installedApps.add(new InstalledAppM104(ai, (String) (ai != null ? packageManager.getApplicationLabel(ai) : "(unknown)"),
                            bucketWifi.getRxBytes(), bucketWifi.getTxBytes(),
                            bucketMobile.getRxBytes(), bucketMobile.getTxBytes()));
                } catch (final Exception e) {
                    Log.e(TAG, "getInstalledApps: ", e);
                }
            }
        }
        return installedApps;
    }

    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (getContext() != null && tm != null) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "getSubscriberId: missing permission 'READ_PHONE_STATE'...");
                } else {
                    return tm.getSubscriberId();
                }
            }
        }
        return "";
    }

    @SuppressLint("StaticFieldLeak")
    class AppLoaderTask extends AsyncTask<String, String, String> {

        private InstalledAppAdapter mAdapter;

        @Override
        protected String doInBackground(String... arg) {
            List<InstalledAppSortable> installedApps = new ArrayList<>();
            for (InstalledAppM104 installedApp : getInstalledApps()) {
                String tmpAppName = installedApp.getApplicationName();
                int tmpUid = installedApp.getApplicationInfo().uid;

                if (Filter.filterApps(installedApp, tmpAppName, tmpUid)) {
                    continue;
                }

                installedApps.add(new InstalledAppSortable(installedApp.getApplicationInfo(),
                        installedApp.getApplicationName(), installedApp));
            }

            Collections.sort(installedApps, InstalledAppSortable.DESCENDING_COMPARATOR);

            mAdapter = new InstalledAppAdapter(AndBasx.getContext(), installedApps);
            return null;
        }

        @Override
        protected void onPostExecute(String arg) {
            try {
                if (getActivity() != null) {
                    (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                }
                mRecyclerView.setAdapter(mAdapter);
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute " + e.getMessage());
            }
        }
    }
}
