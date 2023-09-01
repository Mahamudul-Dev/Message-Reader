package com.uniquepaybd.uniquepay;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LoginManager {
    private static final String PREF_NAME = "LoginPrefs";
    private static final String EMAIL = "email";
    private static final String UID = "uid";

    public static void saveLoginInfo(Context context, String email, String uid) {
        Log.d("saveLoginEmail", email);
        Log.d("saveLoginUid",uid);
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UID, uid);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(EMAIL, "");
    }

    public static String getSavedUid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(UID, "");
    }
}
