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

package com.pepperonas.m104;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.andbasx.base.Loader;
import com.pepperonas.m104.custom.CustomAppIntroFragment;

public class AppIntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(CustomAppIntroFragment.newInstance(
                getString(R.string.app_intro_title_clipboard),
                getString(R.string.app_intro_description_clipboard),
                R.drawable.intro_n6p_clipboard,
                Loader.getColor(R.color.colorPrimary)));

        addSlide(CustomAppIntroFragment.newInstance(
                getString(R.string.app_intro_title_body),
                getString(R.string.app_intro_description_body),
                R.drawable.intro_n6p_body,
                Loader.getColor(R.color.colorPrimary))
        );

        addSlide(CustomAppIntroFragment.newInstance(
                getString(R.string.app_intro_title_icon),
                getString(R.string.app_intro_description_icon),
                R.drawable.intro_n6p_icon,
                Loader.getColor(R.color.colorPrimary))
        );

        addSlide(CustomAppIntroFragment.newInstance(
                getString(R.string.app_intro_title_order),
                getString(R.string.app_intro_description_order),
                R.drawable.intro_n6p_order,
                Loader.getColor(R.color.colorPrimary))
        );

        setProgressButtonEnabled(true);
    }


    @Override
    public void onDonePressed() {
        AesPrefs.putBooleanRes(R.string.SHOW_APP_INTRO, false);
        finish();
    }


    @Override
    public void onSlideChanged() {

    }


    @Override
    public void onNextPressed() {

    }

}
