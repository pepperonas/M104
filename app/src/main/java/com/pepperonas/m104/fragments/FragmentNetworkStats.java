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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.andbasx.base.ToastUtils;
import com.pepperonas.andbasx.concurrency.ThreadUtils;
import com.pepperonas.andbasx.datatype.InstalledApp;
import com.pepperonas.andbasx.system.SystemUtils;
import com.pepperonas.jbasx.log.Log;
import com.pepperonas.m104.MainActivity;
import com.pepperonas.m104.R;
import com.pepperonas.m104.adapter.InstalledAppAdapter;
import com.pepperonas.m104.custom.CustomRecyclerView;
import com.pepperonas.m104.model.InstalledAppSortable;
import com.pepperonas.m104.utils.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Martin Pfeffer (pepperonas)
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_network_stats, container, false);
        (getActivity()).setTitle(getString(R.string.menu_network_stats));
        return v;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle sis) {
        super.onViewCreated(view, sis);

        try {
            (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated " + e.getMessage());
        }

        try {
            ThreadUtils.runDelayed(300, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    (getActivity().findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                    return null;
                }
            });

            mRecyclerView = (CustomRecyclerView) view.findViewById(R.id.recycler_view_network_stats);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);

            new AppLoaderTask().execute("");

        } catch (Exception e) {
            ToastUtils.toastShort(R.string.error_loading_data);
        }
    }


    class AppLoaderTask extends AsyncTask<String, String, String> {

        private InstalledAppAdapter mAdapter;


        @Override
        protected String doInBackground(String... arg) {
            List<InstalledAppSortable> installedApps = new ArrayList<>();
            for (InstalledApp installedApp : SystemUtils.getInstalledApps()) {
                String tmpAppName = installedApp.getApplicationName();
                int tmpUid = installedApp.getApplicationInfo().uid;

                if (Filter.filterApps(installedApp, tmpAppName, tmpUid)) continue;

                installedApps.add(new InstalledAppSortable(AndBasx.getContext(), installedApp.getApplicationInfo(),
                        installedApp.getApplicationName()));
            }

            Collections.sort(installedApps, InstalledAppSortable.DESCENDING_COMPARATOR);

            mAdapter = new InstalledAppAdapter(AndBasx.getContext(), installedApps);
            return null;
        }


        @Override
        protected void onPostExecute(String arg) {
            try {
                (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
                mRecyclerView.setAdapter(mAdapter);
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute " + e.getMessage());
            }
        }

    }

}
