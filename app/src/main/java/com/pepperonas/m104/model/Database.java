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

package com.pepperonas.m104.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pepperonas.aespreferences.AesPrefs;
import com.pepperonas.aespreferences.Crypt;
import com.pepperonas.m104.R;
import com.pepperonas.m104.config.Const;
import com.pepperonas.m104.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Pfeffer (celox.io)
 * @see <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 */
public class Database extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    @SuppressWarnings("WeakerAccess")
    public static String DATABASE_NAME = "bettery.db";

    private static final String TBL_BATTERY_STATS = "tbl_bty_stats";
    private static final String TBL_CHARGE_STATES = "tbl_chg_states";
    private static final String TBL_SCREEN_STATES = "tbl_scn_states";
    private static final String TBL_NETWORK_STATS = "tbl_nwk_stats";
    private static final String TBL_CLIP_DATA = "tbl_clip_data";

    private static final String ID = "id";

    /**
     * columns of {@link BatteryStat} objects
     */
    private static final String BTY_TS = "bty_ts";
    private static final String BTY_IS_CHARGING = "bty_charging";
    private static final String BTY_CHARGE_MODE = "bty_charge";
    private static final String BTY_IS_CHARGED = "bty_charged";
    private static final String BTY_LEVEL = "bty_lvl";
    private static final String BTY_TEMPERATURE = "bty_temp";
    private static final String BTY_VOLTAGE = "bty_volt";
    private static final String BTY_IS_SCREEN_ON = "bty_scn_on";
    private static final String BTY_SCREEN_BRIGHTNESS = "bty_scn_bright";
    private static final String BTY_IS_WIFI_ENABLED = "bty_wifi";
    private static final String BTY_IS_WIFI_CONNECTED = "bty_wifi_cnct";
    private static final String BTY_IS_GPS_ENABLED = "bty_gps";
    private static final String BTY_IS_GPS_NETWORK_ENABLED = "bty_gps_nw";
    private static final String BTY_IS_GPS_PASSIVE_ENABLED = "bty_gps_pv";
    private static final String BTY_IS_SYNC_ENABLED = "bty_sync";
    private static final String BTY_IS_AIRPLANE_MODE_ENABLED = "bty_ap_mode";
    private static final String BTY_CURRENT_MAH = "bty_c_mah";

    /**
     * columns of charge states
     */
    private static String CHARGE_TS = "chg_ts";
    private static final String CHG_FK_BTY_TS = BTY_TS;
    private static final String CHG_CHARGING = "chg_charging";

    /**
     * columns of screen-state
     */
    private static final String SCREEN_TS = "scn_ts";
    private static final String SCN_FK_BTY_TS = BTY_TS;
    private static final String SCN_SCREEN_ON = "scn_screen";

    /**
     * columns of network stats
     */
    private static final String NETWORK_TS = "nwk_ts";
    private static final String NWK_RX = "nwk_rx";
    private static final String NWK_TX = "nwk_tx";
    private static final String NWK_MOBILE_RX = "nwk_mrx";
    private static final String NWK_MOBILE_TX = "nwk_mtx";
    private static final String NWK_PKG_NAME = "nwk_pkg";

    /**
     * columns of clip-data
     */
    private static final String CD_TS = "cd_ts";
    private static final String CD_CONTENT = "cd_cntnt";
    private static final String CD_TEXT = "cd_txt";
    private static final String CD_IV = "cd_iv";

    private static final int FLOATER = 1000;

    /**
     * Instantiates a new Database helper.
     *
     * @param context the context
     */
    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
          battery stats table
          */
        db.execSQL("CREATE TABLE " + TBL_BATTERY_STATS + " (" + ID + " INTEGER PRIMARY KEY, " + BTY_TS + " INTEGER, " +
                BTY_IS_CHARGING + " INTEGER, " + BTY_CHARGE_MODE + " INTEGER, " + BTY_IS_CHARGED + " INTEGER, " +
                BTY_LEVEL + " INTEGER, " + BTY_TEMPERATURE + " INTEGER, " + BTY_VOLTAGE + " INTEGER, " +
                BTY_IS_SCREEN_ON + " INTEGER, " + BTY_SCREEN_BRIGHTNESS + " INTEGER, " + BTY_IS_WIFI_ENABLED + " INTEGER, " +
                BTY_IS_WIFI_CONNECTED + " INTEGER, " + BTY_IS_GPS_ENABLED + " INTEGER, " +
                BTY_IS_GPS_NETWORK_ENABLED + " INTEGER, " + BTY_IS_GPS_PASSIVE_ENABLED + " INTEGER, " +
                BTY_IS_SYNC_ENABLED + " INTEGER, " + BTY_IS_AIRPLANE_MODE_ENABLED + " INTEGER, " + BTY_CURRENT_MAH + " INTEGER);");

        /*
          charge states table
          */
        db.execSQL("CREATE TABLE " + TBL_CHARGE_STATES + " (" + ID + " INTEGER PRIMARY KEY, " + CHARGE_TS + " INTEGER, " +
                CHG_FK_BTY_TS + " INTEGER, " + CHG_CHARGING + " INTEGER);");

        /*
          screen states table
          */
        db.execSQL("CREATE TABLE " + TBL_SCREEN_STATES + " (" + ID + " INTEGER PRIMARY KEY, " + SCREEN_TS + " INTEGER, " +
                SCN_FK_BTY_TS + " INTEGER, " + SCN_SCREEN_ON + " INTEGER);");

        /*
          network stats table
          */
        db.execSQL("CREATE TABLE " + TBL_NETWORK_STATS + " (" + ID + " INTEGER PRIMARY KEY, " + NETWORK_TS + " INTEGER, " +
                NWK_RX + " INTEGER, " + NWK_TX + " INTEGER, " + NWK_MOBILE_RX + " INTEGER, " + NWK_MOBILE_TX + " INTEGER, " +
                NWK_PKG_NAME + " text);");

        /*
          clip-data table
          */
        db.execSQL("CREATE TABLE " + TBL_CLIP_DATA + " (" + ID + " INTEGER PRIMARY KEY, " + CD_TS + " INTEGER, " +
                CD_CONTENT + " INTEGER, " + CD_TEXT + " text unique, " + CD_IV + " INTEGER UNIQUE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_BATTERY_STATS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CHARGE_STATES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_SCREEN_STATES);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_NETWORK_STATS);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CLIP_DATA);

        //        db.execSQL("ALTER TABLE " + TBL_CLIP_DATA + " AUTO_INCREMENT = 1");
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    /**
     * Gets battery stats of charge.
     *
     * @param chargeId the charge id
     * @return the battery stats of charge
     */
    public List<BatteryStat> getBatteryStatsOfCharge(long chargeId) {
        List<String> timestamps = new ArrayList<>();

        String selectQuery = "SELECT " + CHARGE_TS + " FROM " + TBL_CHARGE_STATES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                timestamps.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();

        selectQuery = "SELECT * FROM " + TBL_CHARGE_STATES;

        db = this.getReadableDatabase();
        c = db.rawQuery(selectQuery, null);

        List<BatteryStat> results = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String tmp = c.getString(1);
                for (String s : timestamps) {
                    if (s.equals(tmp)) {
                        ChargeMode cm = ChargeMode.getInstance(c.getInt(2));
                        results.add(new BatteryStat(c.getInt(0), c.getInt(1) == 1, cm, c.getInt(3) == 1,
                                c.getInt(4), (float) c.getInt(5) / FLOATER,
                                (float) c.getInt(6) / FLOATER, c.getInt(7) == 1, c.getInt(8),
                                c.getInt(9) == 1, c.getInt(10) == 1, c.getInt(11) == 1,
                                c.getInt(12) == 1, c.getInt(13) == 1, c.getInt(14) == 1,
                                c.getInt(15) == 1, c.getInt(17)));
                    }
                }
            } while (c.moveToNext());
        }

        c.close();
        return results;
    }

    /**
     * Gets battery stats by date.
     *
     * @param chargeId the charge id
     * @return the list of {@link BatteryStat} objects
     */
    public List<BatteryStat> getBatteryStatsByDate(long chargeId) {
        String selectQuery = "SELECT * FROM " + TBL_BATTERY_STATS + " WHERE " + BTY_TS + " >= " + chargeId + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        List<BatteryStat> results = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                ChargeMode cm = ChargeMode.getInstance(c.getInt(3));
                results.add(new BatteryStat(c.getLong(1), c.getInt(2) == 1, cm, c.getInt(4) == 1,
                        c.getInt(5), (float) c.getInt(6) / FLOATER, (float) c.getInt(7) / FLOATER,
                        c.getInt(8) == 1, c.getInt(9), c.getInt(10) == 1, c.getInt(11) == 1,
                        c.getInt(12) == 1, c.getInt(13) == 1, c.getInt(14) == 1, c.getInt(15) == 1,
                        c.getInt(16) == 1, c.getInt(17)));
            } while (c.moveToNext());
        }

        c.close();
        return results;
    }

    public List<NetworkHistory> getNetworkHistory(long ts) {
        String selectQuery = "SELECT * FROM " + TBL_NETWORK_STATS + " WHERE " + NETWORK_TS + " >= " + ts + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        List<NetworkHistory> results = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                results.add(new NetworkHistory(c.getLong(1), c.getLong(2), c.getLong(3), c.getLong(4), c.getLong(5), c.getString(6)));
            } while (c.moveToNext());
        }

        c.close();
        return results;
    }

    /**
     * Add battery stat.
     *
     * @param batteryStat the battery stat
     * @return the timestamp of the {@link BatteryStat}
     */
    public long addBatteryStat(BatteryStat batteryStat) {
        String row = "INSERT OR REPLACE INTO " + TBL_BATTERY_STATS + " (" + ID + ", " + BTY_TS + ", " + BTY_IS_CHARGING + ", " +
                BTY_CHARGE_MODE + ", " + BTY_IS_CHARGED + ", " + BTY_LEVEL + ", " + BTY_TEMPERATURE + ", " + BTY_VOLTAGE + ", " +
                BTY_IS_SCREEN_ON + ", " + BTY_SCREEN_BRIGHTNESS + ", " + BTY_IS_WIFI_ENABLED + ", " + BTY_IS_WIFI_CONNECTED + ", " +
                BTY_IS_GPS_ENABLED + ", " + BTY_IS_GPS_NETWORK_ENABLED + ", " + BTY_IS_GPS_PASSIVE_ENABLED + ", " +
                BTY_IS_SYNC_ENABLED + ", " + BTY_IS_AIRPLANE_MODE_ENABLED + ", " + BTY_CURRENT_MAH +
                ") VALUES (" + null + ", " + batteryStat.getStamp() + ", " +
                (batteryStat.isCharging() ? 1 : 0) + ", " + batteryStat.getChargeMode().ordinal() + ", " +
                (batteryStat.isCharged() ? 1 : 0) + ", " + batteryStat.getLevel() + ", " +
                (int) batteryStat.getTemperature() * FLOATER + ", " + (int) batteryStat.getVoltage() * FLOATER + ", " +
                (batteryStat.isScreenOn() ? 1 : 0) + ", " + batteryStat.getScreenBrightness() + ", " +
                (batteryStat.isWifiEnabled() ? 1 : 0) + ", " + (batteryStat.isWifiConnected() ? 1 : 0) + ", " +
                (batteryStat.isGpsEnabled() ? 1 : 0) + ", " + (batteryStat.isGpsNetworkEnabled() ? 1 : 0) + ", " +
                (batteryStat.isGpsPassiveEnabled() ? 1 : 0) + ", " + (batteryStat.isSyncEnabled() ? 1 : 0) + ", " +
                (batteryStat.isAirPlaneModeEnabled() ? 1 : 0) + ", " +
                (batteryStat.getRemainingCapacity_mAh()) + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);

        Log.d(TAG, "addBatteryStat");

        return batteryStat.getStamp();
    }

    public void cleanBatteryStats() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_NETWORK_STATS + " WHERE " + NETWORK_TS + " < " +
                (System.currentTimeMillis() - (Const.DELETE_NWK_STATS_OLDER_THAN_IN_HOURS * 60 * 60 * 1000)) + ";");
    }

    /**
     * Add charge state long.
     *
     * @param tsChargeState the ts charge state
     * @param tsBatteryStat the ts battery stat
     * @param isCharging    the is charging
     * @return the timestamp of the charge-state
     */
    public long addChargeState(long tsChargeState, long tsBatteryStat, boolean isCharging) {
        String row = "INSERT OR REPLACE INTO " + TBL_CHARGE_STATES + " (" + ID + ", " + CHARGE_TS + ", " + CHG_FK_BTY_TS + ", " +
                CHG_CHARGING + ") VALUES (" + null + ", " + tsChargeState + ", " + tsBatteryStat + ", " + (isCharging ? 1 : 0) + ");";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);

        Log.d(TAG, "addChargeState");

        return tsChargeState;
    }

    /**
     * Add screen state long.
     *
     * @param tsScreenState the ts screen state
     * @param tsBatteryStat the ts battery stat
     * @param isScreenOn    the is screen on
     * @return the timestamp of the screen-state
     */
    public long addScreenState(long tsScreenState, long tsBatteryStat, boolean isScreenOn) {
        String row = "INSERT OR REPLACE INTO " + TBL_SCREEN_STATES + " (" + ID + ", " + SCREEN_TS + ", " + SCN_FK_BTY_TS + ", " +
                SCN_SCREEN_ON + ") VALUES (" + null + ", " + tsScreenState + ", " + tsBatteryStat + ", " + (isScreenOn ? 1 : 0) + ");";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);

        Log.d(TAG, "addScreenState");

        return tsScreenState;
    }

    /**
     * Add network stat long.
     *
     * @param tsNetworkStat the ts network stat
     * @param rx            the rx
     * @param tx            the tx
     * @param mobileRx      the mobile rx
     * @param mobileTx      the mobile tx
     * @param pkgName       the pkg name
     * @return the timestamp of the network-stat
     */
    public long addNetworkStat(long tsNetworkStat, long rx, long tx, long mobileRx, long mobileTx,
                               String pkgName) {

        String row = "INSERT OR REPLACE INTO " + TBL_NETWORK_STATS + " (" + ID + ", " + NETWORK_TS + ", " + NWK_RX + ", " + NWK_TX + ", " +
                NWK_MOBILE_RX + ", " + NWK_MOBILE_TX + ", " + NWK_PKG_NAME + ") VALUES (" + null + ", " + tsNetworkStat + ", " +
                rx + ", " + tx + ", " + mobileRx + ", " + mobileTx + ", " + "'" + pkgName + "');";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);

        Log.d(TAG, "addNetworkStat");

        return tsNetworkStat;
    }

    public void addClipData(int type, String text, long genericIv) {
        String textToStore;

        if (AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, "").equals("")) {
            textToStore = text;
        } else {
            textToStore = Crypt.encrypt(AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, ""), text, genericIv);
        }

        String row = "INSERT OR REPLACE INTO " + TBL_CLIP_DATA + " (" + ID + ", " + CD_TS + ", " + CD_CONTENT + ", " + CD_TEXT + ", " +
                CD_IV + " ) VALUES ( " + null + ", " + System.currentTimeMillis() + ", " + type + ", '" + textToStore + "', " + genericIv + ");";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(row);

        Log.d(TAG, "added clip-data");
    }

    public List<ClipDataAdvanced> getClipData(int i) {
        String selectQuery = "SELECT * FROM " + TBL_CLIP_DATA + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int ctr = 0;

        if (AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, "").equals("")
                || AesPrefs.getLongRes(R.string.LOGOUT_TIME, 0) < System.currentTimeMillis()) {

            List<ClipDataAdvanced> results = new ArrayList<>();

            if (c.moveToLast()) {
                do {
                    results.add(new ClipDataAdvanced(c.getLong(1), c.getInt(2), c.getString(3), c.getLong(4)));
                } while (c.moveToPrevious() && ((ctr++) < i));
            }
            c.close();
            return results;

        } else {
            List<ClipDataAdvanced> results = new ArrayList<>();
            if (c.moveToLast()) {
                do {
                    try {
                        results.add(new ClipDataAdvanced(c.getLong(1), c.getInt(2), Crypt
                                .decrypt(AesPrefs.getRes(R.string.ENCRYPTION_PASSWORD, ""), c.getString(3), c.getLong(4)), c.getLong(4)));
                    } catch (Exception e) {
                        Log.e(TAG, "getClipData error while decrypting...");
                    }
                } while (c.moveToPrevious() && ((ctr++) < i));
            }
            c.close();
            return results;
        }
    }

    public int getClipDataCount() {
        String selectQuery = "SELECT * FROM " + TBL_CLIP_DATA + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int ctr = 0;
        if (c.moveToLast()) {
            do {
                if (c.getString(3) != null && !c.getString(3).isEmpty()) {
                    ctr++;
                }
            } while (c.moveToPrevious());
        }
        c.close();
        return ctr;
    }

    public void deleteClipData(String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_CLIP_DATA + " WHERE " + CD_TEXT + " = '" + content + "';");
    }

    public void deleteAllClips() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_CLIP_DATA + ";");
    }

    public void encryptClipboard(String s) {
        List<ClipDataAdvanced> data = getClipData(Integer.MAX_VALUE);
        for (ClipDataAdvanced cda : data) {
            encryptClipDataEntry(cda, s);
        }
    }

    public void decryptClipboard(String s) {
        List<ClipDataAdvanced> data = getClipData(Integer.MAX_VALUE);
        for (ClipDataAdvanced cda : data) {
            decryptClipDataEntry(cda, s);
        }
    }

    private void encryptClipDataEntry(ClipDataAdvanced clipDataAdvanced, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        long iv = System.currentTimeMillis();
        String encrypted = Crypt.encrypt(password, clipDataAdvanced.getClipText(), iv);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CD_TEXT, encrypted);
        contentValues.put(CD_IV, iv);
        db.update(TBL_CLIP_DATA, contentValues, CD_TS + " = " + clipDataAdvanced.getTimestamp(), null);
    }

    private void decryptClipDataEntry(ClipDataAdvanced clipDataAdvanced, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String decrypted = Crypt.decrypt(password, clipDataAdvanced.getClipText(), clipDataAdvanced.getIv());
        ContentValues contentValues = new ContentValues();
        contentValues.put(CD_TEXT, decrypted);
        contentValues.put(CD_IV, clipDataAdvanced.getIv());
        try {
            db.update(TBL_CLIP_DATA, contentValues, CD_TS + "= " + clipDataAdvanced.getTimestamp(), null);
        } catch (Exception e) {
            Log.e(TAG, "decryptClipDataEntry: ", e);
        }
    }

    /**
     * Delete all.
     */
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TBL_BATTERY_STATS + ";");
        db.execSQL("DELETE FROM " + TBL_CHARGE_STATES + ";");
        db.execSQL("DELETE FROM " + TBL_SCREEN_STATES + ";");
        db.execSQL("DELETE FROM " + TBL_NETWORK_STATS + ";");
        db.execSQL("DELETE FROM " + TBL_CLIP_DATA + ";");
    }

    /**
     * Update battery stat.
     *
     * @param batteryStat the battery stat
     * @param newValue    the new value
     */
    public void updateBatteryStat(BatteryStat batteryStat, int newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", newValue);
        db.update(TBL_CHARGE_STATES, contentValues, CHARGE_TS + " = '???'", null);
    }

}

