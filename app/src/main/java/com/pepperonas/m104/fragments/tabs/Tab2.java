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

package com.pepperonas.m104.fragments.tabs;

import android.os.AsyncTask;
import android.os.Bundle;
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
import com.pepperonas.m104.utils.DumpLoader;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class Tab2 extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = "Tab2";

    private static IInstalledBasicsCommunicator mInstalledBasicsCommunicator;


    public static Tab2 getInstance(FragmentRoot fragmentRoot, int i) {
        Tab2 fragment = new Tab2();

        Bundle args = new Bundle();
        args.putInt("the_id", i);
        fragment.setArguments(args);

        mInstalledBasicsCommunicator = fragmentRoot;

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_2, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        new LoaderTask().execute("");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            (getActivity().findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "onDetach " + e.getMessage());
        }
    }


    class LoaderTask extends AsyncTask<String, String, String> {

        private String dumpData = "";
        //        private ProgressBar progressBar;


        public LoaderTask() {

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                //                progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
                //                progressBar.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "onPreExecute " + e.getMessage());
            }
        }


        @Override
        protected String doInBackground(String... arg) {
            dumpData = DumpLoader.loadDump(FragmentRoot.FILE_PROC_STS);
            return null;
        }


        @Override
        protected void onPostExecute(String arg) {
            //            progressBar.setVisibility(View.INVISIBLE);

            try {
                ((TextView) getActivity().findViewById(R.id.tv_root_tab_2)).setText(dumpData);
            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: Error while set text.");
            }
        }
    }

}
