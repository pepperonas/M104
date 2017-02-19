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

package com.pepperonas.m104.utils;

import android.support.annotation.NonNull;

import com.pepperonas.andbasx.AndBasx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class DumpLoader {

    /**
     * Load dump string.
     *
     * @param which the which
     * @return the string
     */
    @NonNull
    public static String loadDump(String which) {
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(AndBasx.getContext().getFilesDir() + File.separator + which));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
