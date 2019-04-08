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

package com.pepperonas.m104.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pepperonas.m104.fragments.FragmentRoot;
import com.pepperonas.m104.fragments.tabs.Tab1;
import com.pepperonas.m104.fragments.tabs.Tab2;
import com.pepperonas.m104.fragments.tabs.Tab3;
import com.pepperonas.m104.fragments.tabs.Tab4;
import com.pepperonas.m104.fragments.tabs.Tab5;
import com.pepperonas.m104.fragments.tabs.Tab6;
import com.pepperonas.m104.fragments.tabs.Tab7;
import com.pepperonas.m104.fragments.tabs.Tab8;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence mTitles[];

    private FragmentRoot mFragmentRoot;

    public ViewPagerAdapter(FragmentRoot fragmentRoot, FragmentManager fm, CharSequence mTitles[]) {
        super(fm);

        mFragmentRoot = fragmentRoot;

        this.mTitles = mTitles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Tab1.getInstance(mFragmentRoot, 1);
            case 1:
                return Tab2.getInstance(mFragmentRoot, 2);
            case 2:
                return Tab3.getInstance(mFragmentRoot, 3);
            case 3:
                return Tab4.getInstance(mFragmentRoot, 4);
            case 4:
                return Tab5.getInstance(mFragmentRoot, 5);
            case 5:
                return Tab6.getInstance(mFragmentRoot, 6);
            case 6:
                return Tab7.getInstance(mFragmentRoot, 7);
            case 7:
                return Tab8.getInstance(mFragmentRoot, 8);
            default:
                return Tab1.getInstance(mFragmentRoot, 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return FragmentRoot.AMOUNT_OF_TABS;
    }
}
