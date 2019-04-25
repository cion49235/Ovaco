package kr.co.inno.autocash.cms;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;

import java.util.HashMap;
import java.util.Map;

public class PreferencesHelper {
    private static PreferencesHelper current;

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static PreferencesHelper getInstance(Context context) {
        if (current == null) {
            current = new PreferencesHelper();
        }

        current.setContext(context);
        return current;
    }

    private PreferencesHelper() {
    }

    public void setContext(Context context) {
        if (this.context != context) {
            this.context = context;

            if (preferences != null) {
                preferences = null;
            }

            preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
            editor = preferences.edit();
        }
    }

    public SharedPreferences getPreferences() {
        return this.preferences;
    }

    public SharedPreferences.Editor getEditor() {
        return this.editor;
    }

    public void commit() {
        SharedPreferencesCompat.EditorCompat.getInstance().apply(this.editor);
    }

    public static void setLiveActivity(Context context, String activityName, String uniqueKey) {
        SharedPreferences.Editor editor = PreferencesHelper.getInstance(context).getEditor();
        editor.putString("LIVE_ACTIVITY_NAME", activityName);
        editor.putString("LIVE_ACTIVITY_KEY", uniqueKey);
        PreferencesHelper.getInstance(context).commit();
    }

    public static Map<String, String> getLiveActivity(Context context) {
        SharedPreferences preferences = PreferencesHelper.getInstance(context).getPreferences();
        String activityName = preferences.getString("LIVE_ACTIVITY_NAME", "");
        String uniqueKey = preferences.getString("LIVE_ACTIVITY_KEY", "");

        Map<String, String> liveMap = new HashMap<String, String>();
        liveMap.put("activityName", activityName);
        liveMap.put("uniqueKey", uniqueKey);
        return liveMap;
    }
}
