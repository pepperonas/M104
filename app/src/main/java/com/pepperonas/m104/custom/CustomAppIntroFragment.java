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

package com.pepperonas.m104.custom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntroFragment;
import com.pepperonas.m104.R;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class CustomAppIntroFragment extends AppIntroFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";
    private static final String ARG_DRAWABLE = "drawable";
    private static final String ARG_BG_COLOR = "bg_color";
    private static final String ARG_TITLE_COLOR = "title_color";
    private static final String ARG_DESC_COLOR = "desc_color";

    public static CustomAppIntroFragment newInstance(String title, String description,
                                                     int imageDrawable, int bgColor) {
        CustomAppIntroFragment sampleSlide = new CustomAppIntroFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public static CustomAppIntroFragment newInstance(String title, String description,
                                                     int imageDrawable, int bgColor, int titleColor, int descColor) {
        CustomAppIntroFragment sampleSlide = new CustomAppIntroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_TITLE_COLOR, titleColor);
        args.putInt(ARG_DESC_COLOR, descColor);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    private int drawable, bgColor, titleColor, descColor;
    private String title, description;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().size() != 0) {
            drawable = getArguments().getInt(ARG_DRAWABLE);
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESC);
            bgColor = getArguments().getInt(ARG_BG_COLOR);
            titleColor =
                    getArguments().containsKey(ARG_TITLE_COLOR) ? getArguments().getInt(ARG_TITLE_COLOR)
                            : 0;
            descColor =
                    getArguments().containsKey(ARG_DESC_COLOR) ? getArguments().getInt(ARG_DESC_COLOR)
                            : 0;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_fragment_intro, container, false);
        TextView t = v.findViewById(com.github.paolorotolo.appintro.R.id.title);
        TextView d = v.findViewById(com.github.paolorotolo.appintro.R.id.description);
        ImageView i = v.findViewById(com.github.paolorotolo.appintro.R.id.image);

        LinearLayout m = v.findViewById(com.github.paolorotolo.appintro.R.id.main);
        t.setText(title);
        if (titleColor != 0) {
            t.setTextColor(titleColor);
        }
        d.setText(description);
        if (descColor != 0) {
            d.setTextColor(descColor);
        }
        i.setImageDrawable(CustomResourceUtils.getDrawable(getActivity(), drawable));
        m.setBackgroundColor(bgColor);
        return v;
    }

}
