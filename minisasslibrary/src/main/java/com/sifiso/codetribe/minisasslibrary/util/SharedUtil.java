package com.sifiso.codetribe.minisasslibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationDTO;
import com.sifiso.codetribe.minisasslibrary.dto.EvaluationImageDTO;
import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;

import java.util.Date;

/**
 * Created by Chris on 2015-02-20.
 */
public class SharedUtil {

    static final Gson gson = new Gson();

    public static final String
            TEAM_MEMBER_JSON = "teamMember",
            EMAIL_JSON = "email",
            EVALUATION_IMAGE_ID = "evaluationImageID",
            EVALUATION_JSON = ".evaluation",
            EVALUATION_ID = "evaluationID",
            GCM_REGISTRATION_ID = "gcm",
            IMAGE_LOCATION = "imageLocation",
            LOG = "SharedUtil",
            SESSION_ID = "sessionID",
            REMINDER_TIME = "reminderTime",
            APP_VERSION = "appVersion",
            ALL_RIVERS_LOADED = "river_loaded";

    public static ImageLocation getImageLocation(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String s = sp.getString(IMAGE_LOCATION, null);
        if (s == null) {
            return null;
        }
        return gson.fromJson(s, ImageLocation.class);
    }

    public static void setRiverLoadedFlag(Context ctx, int flag){

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();

        ed.putInt(ALL_RIVERS_LOADED, flag);
        ed.commit();
        Log.e(LOG, "SharedUtil, RIVER LOADED flag: " + flag+ " SAVED IN SharedPreferences");
    }
    public static int getRiverLoadedFlag(Context ctx){
        // if flag is 1 means load all rivers if 0 mean don't load
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        int flag = sp.getInt(ALL_RIVERS_LOADED, 1);;
        return flag;
    }
    public static void clearTeam(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        sp.edit().remove(TEAM_MEMBER_JSON).commit();
    }

    public static EvaluationDTO getEvaluation(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String st = sp.getString(EVALUATION_JSON, null);
        EvaluationDTO e = null;
        if (st != null) {
            e = gson.fromJson(st, EvaluationDTO.class);
        }
        return e;
    }

    public static TeamMemberDTO getTeamMember(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String st = sp.getString(TEAM_MEMBER_JSON, null);
        TeamMemberDTO tm = null;
        if (st != null) {
            tm = gson.fromJson(st, TeamMemberDTO.class);
        }
        return tm;
    }

    public static void saveRiverRefreshTime(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();

        ed.putLong("time", new Date().getTime());
        ed.commit();
        Log.e(LOG, "Time  SAVED IN SharedPreferences");

    }
    public static Date getRiverRefreshTime(Context ctx) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Long st = sp.getLong("time", 0);
        if (st == 0) {
            return null;
        }
        Date date = new Date(st);
        return date;
    }
    static final long TIME = 1000 * 60 * 1;
    public static boolean isRiverListNeedsRefresh(Context ctx) {
        if (getRiverRefreshTime(ctx) == null) {
            return true;
        }
        Date lastRefresh = getRiverRefreshTime(ctx);
        Date now = new Date();
        if (now.getTime() - lastRefresh.getTime() > TIME) {
            return true;
        }


        return false;
    }

    public static void saveTeamMember(Context ctx, TeamMemberDTO evi) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();

        ed.putString(TEAM_MEMBER_JSON, gson.toJson(evi));
        ed.commit();
        Log.e(LOG, "SharedUtil, USER INFO: " + new Gson().toJson(evi) + " SAVED IN SharedPreferences");

    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GCM_REGISTRATION_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
        Log.e(LOG, "GCM registrationId saved in prefs! Yebo!!!");
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getEmail(Context context) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String email = prefs.getString(EMAIL_JSON, null);
        if (email == null) {
            Log.i(LOG, "GCM Registration ID not found on device.");
            return null;
        }

        return email;
    }

    public static void storeEmail(Context context, String email) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Log.i(LOG, "Saving regId on app version " + email);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(EMAIL_JSON, email);
        editor.commit();
        Log.e(LOG, "GCM registrationId saved in prefs! Yebo!!!");
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(GCM_REGISTRATION_ID, null);
        if (registrationId == null) {
            Log.i(LOG, "GCM Registration ID not found on device.");
            return null;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = SharedUtil.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG, "App version changed.");
            return null;
        }
        return registrationId;
    }

    public static void saveSessionID(Context ctx, String sessionID) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SESSION_ID, sessionID);
        ed.commit();
        Log.e("SharedUtil", "%%%%% SessionID: " + sessionID + " saved in SharedPreferences");
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getSessionID(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return sp.getString(SESSION_ID, null);
    }

    public static void saveLastEvaluationImageID(Context ctx, Integer evaluationImageID) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(EVALUATION_IMAGE_ID, evaluationImageID);
        ed.commit();
        Log.e("SharedUtil", "evaluationImageID:" + evaluationImageID + "save in SharedPreferences");
    }

    public static Integer getLastEvaluationImageID(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int id = sp.getInt(EVALUATION_IMAGE_ID, 0);
        return id;

    }

    public static void saveImageLocation(Context ctx, EvaluationImageDTO evi, Location loc) {
        ImageLocation il = new ImageLocation();
        il.setLongitude(loc.getLongitude());
        il.setAccuracy(loc.getAccuracy());
        il.setLatitude(loc.getLatitude());
        il.setDateTaken(new Date());

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(IMAGE_LOCATION, gson.toJson(il));
        ed.commit();
        Log.e(LOG, "SharedUtil, LOCATION IMAGE: " + evi.getFileName() + " SAVED IN SharedPreferences");

    }

    public static void saveImageUri(Context ctx, Uri uri) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Constants.IMAGE_URI, uri.toString());
        ed.commit();
    }

    public static void saveThumbUri(Context ctx, Uri uri) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Constants.THUMB_URI, uri.toString());
        ed.commit();
    }
    public static String getImageUri(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String s = sp.getString(Constants.IMAGE_URI, null);

        return s;
    }

    public static String getThumbUri(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String s = sp.getString(Constants.THUMB_URI, null);

        return s;
    }
}
