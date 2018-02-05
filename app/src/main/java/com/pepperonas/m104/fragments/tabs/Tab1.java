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

package com.pepperonas.m104.fragments.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pepperonas.m104.R;
import com.pepperonas.m104.fragments.FragmentRoot;
import com.pepperonas.m104.interfaces.IInstalledBasicsCommunicator;
import com.pepperonas.m104.model.InstalledBasic;
import com.pepperonas.m104.utils.DumpLoader;

import java.util.List;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class Tab1 extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = "Tab1";

    private static IInstalledBasicsCommunicator mInstalledBasicsCommunicator;

    public static Tab1 getInstance(FragmentRoot fragmentRoot, int i) {
        Tab1 fragment = new Tab1();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        mInstalledBasicsCommunicator = fragmentRoot;

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<InstalledBasic> installedBasicList = mInstalledBasicsCommunicator.onInstalledBasicsRequested();
        Log.d(TAG, "onViewCreated installedAppSize: " + installedBasicList.size());

        //        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        new LoaderTask().execute("");
    }

    //    @Override
    //    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    //        super.onViewCreated(view, savedInstanceState);
    //        //        try {
    //        //            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_tab1);
    //        //            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    //        //            mRecyclerView.setLayoutManager(linearLayoutManager);
    //        //            mRecyclerView.setHasFixedSize(true);
    //        //            new LoaderTask().execute("");
    //        //        } catch (Exception e) {
    //        //            ToastUtils.toastShort(R.string.error_loading_data);
    //        //        }
    //    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (getActivity() != null) {
                (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Log.e(TAG, "onDetach " + e.getMessage());
        }
    }

    class LoaderTask extends AsyncTask<String, String, String> {

        private String dumpData = "";
        //        private ProgressBar progressBar;

        LoaderTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg) {
            dumpData = DumpLoader.loadDump(FragmentRoot.FILE_PROC_ACT);
            return null;
        }

        @Override
        protected void onPostExecute(String arg) {
            //            progressBar.setVisibility(View.INVISIBLE);

            try {
                if (getActivity() != null) {
                    ((TextView) getActivity().findViewById(R.id.tv_root_tab_1)).setText(dumpData);
                }
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: Error while set text.");
            }
        }
    }

    //    class LoaderTask extends AsyncTask<String, String, String> {
    //
    //        private RootBatteryAdapter adapter;
    //
    //
    //        @Override
    //        protected String doInBackground(String... arg) {
    //
    //            String dumpData = DumpLoader.loadDump(FragmentRoot.FILE_USGE_STS);
    //
    //            Log.e(TAG, "DUMP_DATA: " + dumpData);
    //            Log.e(TAG, "DUMP_DATA: " + dumpData);
    //            Log.e(TAG, "DUMP_DATA: " + dumpData);
    //            Log.e(TAG, "DUMP_DATA: " + dumpData);
    //
    //            List<RootBatteryStat> rootBatteryStats = new ArrayList<>();
    //
    //            String[] lines = dumpData.split("\n");
    //            for (String line : lines) {
    //                Log.d(TAG, "doInBackground " + line);
    //                if (!line.contains("package=")) continue;
    //                rootBatteryStats.add(new RootBatteryStat(line));
    //            }
    //
    //
    //            //                String tmpAppName = installedApp.getApplicationName();
    //            //                int tmpUid = installedApp.getApplicationInfo().uid;
    //            //                if (filterApps(installedApp, tmpAppName, tmpUid)) continue;
    //
    //            //            for (int i = 0; i < 100; i++) {
    //            //                rootBatteryStats.add(new RootBatteryStat("~" + i));
    //            //            }
    //
    //            //            Collections.sort(rootBatteryStats, InstalledAppSortable.DESCENDING_COMPARATOR);
    //
    //            adapter = new RootBatteryAdapter(getContext(), rootBatteryStats);
    //            return null;
    //        }
    //
    //
    //        @Override
    //        protected void onPostExecute(String arg) {
    //            try {
    //                adapter.setHasStableIds(true);
    //                mRecyclerView.setAdapter(adapter);
    //            } catch (Exception e) {
    //                Log.e(TAG, "onPostExecute " + e.getMessage());
    //            }
    //        }
    //
    //
    //        private boolean filterApps(InstalledApp installedApp, String tmpAppName, int tmpUid) {
    //            // starts with (toLowerCase!)
    //            if (tmpAppName.toLowerCase().startsWith("com.")
    //                || tmpAppName.toLowerCase().startsWith("de.")
    //                || tmpAppName.toLowerCase().startsWith("es.")
    //                || tmpAppName.toLowerCase().startsWith("fr.")
    //                || tmpAppName.toLowerCase().startsWith("org.")) {
    //                return true;
    //            }
    //
    //            // equals (toLowerCase!)
    //            String tmpPkgName = installedApp.getApplicationInfo().packageName;
    //            if (tmpPkgName.toLowerCase().equals("com.google.android.gsf.login")
    //                || tmpPkgName.toLowerCase().equals("com.google.android.gsf")
    //                || tmpPkgName.toLowerCase().equals("com.google.android.backuptransport")
    //                || tmpPkgName.toLowerCase().equals("com.qualcomm.cabl")
    //                || tmpPkgName.toLowerCase().equals("com.huawei.mmitest")
    //                || tmpPkgName.toLowerCase().equals("android")
    //                || tmpPkgName.toLowerCase().equals("com.android.calllogbackup")
    //                || tmpPkgName.toLowerCase().equals("com.android.providers.settings")
    //                || tmpPkgName.toLowerCase().equals("com.android.providers.media")
    //                || tmpPkgName.toLowerCase().equals("com.android.providers.downloads.ui")
    //                || tmpPkgName.toLowerCase().equals("com.android.providers.userdictionary")
    //                || tmpPkgName.toLowerCase().equals("com.android.server.telecom")
    //                || tmpPkgName.toLowerCase().equals("com.android.keychain")
    //                || tmpPkgName.toLowerCase().equals("com.android.location.fused")
    //                || tmpPkgName.toLowerCase().equals("com.android.location.fused")
    //                || tmpPkgName.toLowerCase().equals("com.android.inputdevices")) {
    //                return true;
    //            }
    //
    //            // no traffic
    //            return TrafficStats.getUidRxBytes(tmpUid) == 0 && TrafficStats.getUidTxBytes(tmpUid) == 0;
    //        }
    //    }
}
