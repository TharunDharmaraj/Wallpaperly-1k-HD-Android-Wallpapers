package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CacheClearReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if it's time to clear the cache
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long lastClearTime = preferences.getLong("last_clear_time", 0);
        long currentTime = System.currentTimeMillis();
        long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000; // 1 week in milliseconds

        if (currentTime - lastClearTime >= oneWeekInMillis) {
            CacheUtil.deleteCache(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("last_clear_time", currentTime);
            editor.apply();
        }
    }
}
