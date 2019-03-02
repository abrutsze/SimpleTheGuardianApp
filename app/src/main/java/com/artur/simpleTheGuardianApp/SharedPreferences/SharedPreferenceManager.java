package com.artur.simpleTheGuardianApp.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.artur.simpleTheGuardianApp.Enums.ViewType;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by artur on 02-Mar-19.
 */

public class SharedPreferenceManager {

    private static SharedPreferenceManager sharedPreferenceManager;
    private final String MY_PREFS_NAME = "SimpleTheGuardianApp";
    // preference keys
    private final String KEY_VIEW_TYPE = "com.artur.simpleTheGuardianApp.SharedPreferences.KEY_VIEW_TYPE";
    private SharedPreferences prefs;

    private SharedPreferenceManager(Context context) {
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    }

    public static void initSharedPreferenceManager(Context context) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(context);
        }
    }

    public static SharedPreferenceManager getSharedPreferenceManager() {
        return sharedPreferenceManager;
    }

    public int getViewType() {
        return prefs.getInt(KEY_VIEW_TYPE, 1);
    }

    public void setViewType(ViewType viewType) {
        prefs.edit().putInt(KEY_VIEW_TYPE, viewType.type()).apply();
    }

}
