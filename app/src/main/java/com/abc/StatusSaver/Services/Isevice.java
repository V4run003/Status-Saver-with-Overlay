package com.abc.StatusSaver.Services;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.abc.StatusSaver.Utils.Constants.MyPREFERENCES;

public class Isevice extends Service {
    final String[] currentApp = {"NULL"};

    public Isevice() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        onTaskRemoved(intent);
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        assert usm != null;
        AsyncTask.execute(() -> {
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000 * 10000, time);
            if (appList != null && appList.size() > 1) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp[0] = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                }
            }
            if (currentApp[0].compareTo("com.whatsapp") == 0) {
                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                String aa = sharedpreferences.getString("Overlay", "null");
                if (aa.equals("checked")) {
                    final Intent intent2 = new Intent(Isevice.this, FloatingService.class);
                    ContextCompat.startForegroundService(Isevice.this, intent2);
                } else if (aa.equals("null")) {
                    final Intent intent2 = new Intent(Isevice.this, FloatingService.class);
                    ContextCompat.startForegroundService(Isevice.this, intent2);
                }
            }


        });


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String aa = sharedpreferences.getString("Overlay", "null");
        if (aa.equals("checked")) {
            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());
            startService(restartServiceIntent);
            super.onTaskRemoved(rootIntent);
        } else if (aa.equals("unchecked")) {
            stopSelf();
            Log.d("stopped by user", aa);
        }
    }


    @Override
    public void onDestroy() {

      /*  Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        super.onDestroy(); */
    }
}
