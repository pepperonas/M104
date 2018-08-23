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

package com.pepperonas.m104.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.pepperonas.andbasx.AndBasx;
import com.pepperonas.m104.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * The type Utils.
 *
 * @author Martin Pfeffer  <a
 * href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">celox.io</a>
 */
public class Utils {

    private static final String TAG = "Utils";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Byte to hex str string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String byteToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static String hexToBinary(String hex) {
        String value = new BigInteger(hex, 16).toString(2);
        String formatPad = "%" + (hex.length() * 4) + "s";
        return String.format(formatPad, value).replace(" ", "0");
    }

    public static String changeEndianOfHexStr(String data) {
        if (data == null || data.isEmpty() || data.length() % 2 != 0) {
            Log.w(TAG, "Data miss-formed!");
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int ctr = 0;
        for (int i = 0; i < data.length(); i = i + 2) {
            int pre = data.length() - ctr - 2;
            int aft = data.length() - ctr;
            if (pre <= 0) {
                pre = 0;
            }
            if (aft <= 0) {
                aft = 2;
            }
            builder.append(data.substring(pre, aft));
            ctr += 2;
        }
        return builder.toString().toUpperCase();
    }

    @SuppressWarnings("unused")
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        inputStream.close();
        return result.toString();
    }

    /**
     * Run on background thread.
     *
     * @param callable the callable
     */
    public static void runOnBackgroundThread(final Callable callable) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callable.call();
                } catch (Exception e) {
                    Log.e(TAG, "runOnBackgroundThread: ", e);
                }
            }
        });
    }

    /**
     * Run on main ui thread.
     *
     * @param callable the callable
     */
    public static void runOnMainUiThread(final Callable callable) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    callable.call();
                } catch (Exception e) {
                    Log.e(TAG, "runOnMainUiThread: ", e);
                }
            }
        });
    }

    /**
     * Run delayed.
     *
     * @param callable the callable
     * @param delay    the delay
     */
    public static void runDelayed(final Callable callable, long delay) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay);
    }

    public static void runThreadSafe(final Callable callable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        Log.e(TAG, "runThreadSafe: ", e);
                    }
                }
            });
        }
    }

    /**
     * Gets readable time stamp.
     *
     * @return the readable time stamp
     */
    public static String getReadableTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.GERMANY);
        Date dt = new Date();
        return sdf.format(dt);
    }

    /**
     * Gets readable time stamp.
     *
     * @return the readable time stamp
     */
    public static String getReadableTimeStamp(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.GERMANY);
        Date dt = new Date(millis);
        return sdf.format(dt);
    }

    /**
     * Load preferences.
     */
    @SuppressWarnings("unused")
    public static void loadPreferences() {
        SharedPreferences preferences = AndBasx.getContext().getSharedPreferences(
                AesConst.AES_PREFS_FILE_NAME, MODE_PRIVATE);
        Map<String, ?> prefs = preferences.getAll();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Float) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Integer) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Long) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof String) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Set<?>) {
                printVal = key + " : " + pref;
            }

            Log.d(TAG, "loadPreferences: " + printVal);
        }
    }

    /**
     * ConvertUtils csv to chart data points list.
     *
     * @param fileName the file name
     * @return the list
     */
    public static List<Double> convertCsvToChartDataPoints(String fileName) {
        List<Double> values = new ArrayList<>();
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        Log.v(TAG, "convertCsvToChartDataPoints: " + sd.getPath());

        File file = new File(sd, fileName);
        if (!file.exists()) {
            Log.e(TAG, "convertCsvToChartDataPoints: .csv-file NOT found!");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
                line = line.replace(",", ".");
                if (line.contains(";")) {
                    try {
                        String value = line.split(";")[1];
                        if (value == null) continue;
                        System.out.println(value);
                        try {
                            double d = Double.parseDouble(value);
                            values.add(d);
                        } catch (Exception e) {
                            Log.e(TAG, "convertCsvToChartDataPoints: ", e);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "convertCsvToChartDataPoints: ", e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "convertCsvToChartDataPoints: " + sb.toString());
        return values;
    }

    /**
     * Converts a string to MD5 hash string.
     *
     * @param string value that should be hashed.
     * @return MD5 hash of the given string.
     */
    public static String stringToMd5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(string.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            return String.format("%0" + (messageDigest.length << 1) + "x", number);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceImei(Context context) {
        try {

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "getDeviceImei: Missing permission to get IMEI.");
                return "-1";
            }
            String imei = null;
            if (tm != null) {
                imei = tm.getDeviceId();
            } else {
                Log.w(TAG, "getDeviceImei: TelephonyManager is null. Can't resolve IMEI.");
                return "-1";
            }
            if (imei == null) {
                imei = Const.EMULATOR_IMEI;
            } else if (imei.equals("000000000000000")) {
                // for genymotion emulator
                imei = Const.EMULATOR_IMEI;
            }
            return imei;
        } catch (Exception e) {
            Log.e(TAG, "getDeviceImei: ", e);
            return "-1";
        }
    }

    public static String getBuildVersion(Context context) {
        String summary = BuildConfig.VERSION_NAME;
        Date date = new Date(BuildConfig.APP_CREATED);
        SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd.HH.mm.ss", Locale.GERMANY);
        return summary + "-" + sdf.format(date);
    }

    public static int getVersionCode(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @SuppressWarnings("unused")
    private int findMax(int... values) {
        int max = Integer.MIN_VALUE;
        for (int i : values) {
            if (i > max) max = i;
        }
        return max;
    }

    public static void hideSoftKeyboard(Fragment fragment) {
        View view = fragment.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else {
                Log.w(TAG, "Can't hide soft keyboard.");
            }
        }
    }

    public static String formatTimestamp(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static void setRipple(Context context, ImageButton imageButton) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        imageButton.setBackgroundResource(outValue.resourceId);
    }

    private String fmt(int number, int places) {
        return String.format("%0" + places + "d", number);
    }

    public static String formatPersonnelId(String personnelId) {
        if (personnelId.length() > 4) {
            personnelId = personnelId.substring(personnelId.length() - 4, personnelId.length());
        }
        // force length of user-id to 4 digits
        personnelId = padLeft(personnelId, 4).replace(" ", "0");
        return personnelId;
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static void copyFile(File from, File to) {
        InputStream inStream;
        OutputStream outStream;
        try {
            to.getParentFile().mkdirs();
            to.createNewFile();
            inStream = new FileInputStream(from);
            outStream = new FileOutputStream(to);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            inStream.close();
            outStream.close();

            Log.i(TAG, "copyFile: File is copied successfully!");
        } catch (IOException e) {
            Log.e(TAG, "copyFile: " + e.getMessage());
        }
    }

    public static String bytesToHuman(long size) {
        long Kb = 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size < Mb) return floatForm((double) size / Kb) + " Kb";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " Mb";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " Gb";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " Tb";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " Pb";
        if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

        return "invalid size";
    }

    private static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static String getFileFromAssets(Context context, String fileName) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "getFileFromAssets: ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "getFileFromAssets: ", e);
                }
            }
        }
        return content.toString();
    }

    public static Integer getFreeRam(Context context) {
        long start = System.currentTimeMillis();
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(mi);
            Log.d(TAG, "getFreeRam: took " + (System.currentTimeMillis() - start) + "ms");
            return (Integer) (int) (mi.availMem / 1048576L);
        }
        return null;
    }

    public static void adjustAudioVolume(Context context, boolean increase) {
        adjustAudioVolume(context, increase, 1);
    }

    public static void adjustAudioVolume(Context context, boolean increase, int steps) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            for (int i = 0; i < steps; i++) {
                audioManager.adjustVolume(increase ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND);
            }
        } else {
            Log.w(TAG, "adjustAudioVolume: can't adjust volume, AudioManager is null...");
        }
    }

    public static void setAudioEnabled(Context context, boolean mute) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustVolume(mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE,
                    AudioManager.FLAG_PLAY_SOUND);
        } else {
            Log.w(TAG, "adjustAudioVolume: can't adjust volume, AudioManager is null...");
        }
    }

    public static String formatSeconds(long seconds) {
        int hrs = (int) TimeUnit.SECONDS.toHours(seconds) % 24;
        int min = (int) TimeUnit.SECONDS.toMinutes(seconds) % 60;
        int sec = (int) TimeUnit.SECONDS.toSeconds(seconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
