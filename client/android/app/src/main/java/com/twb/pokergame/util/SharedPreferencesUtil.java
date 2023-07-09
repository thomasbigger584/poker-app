package com.twb.pokergame.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static final String KEY_PREVIOUS_ID = "ua.naiksoftware.stompclientexample.previousId";

    private SharedPreferencesUtil() {

    }

    public static void saveId(Activity activity, int id) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_PREVIOUS_ID, id);
        editor.apply();
    }

    public static int getPreviousId(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(KEY_PREVIOUS_ID, 0);
    }
}
