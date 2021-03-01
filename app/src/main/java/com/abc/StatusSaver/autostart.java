package com.abc.StatusSaver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.abc.StatusSaver.Services.Isevice;

import static com.abc.StatusSaver.Utils.Constants.MyPREFERENCES;

public class autostart extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, Isevice.class);
        SharedPreferences sharedpreferenceboot = context.getSharedPreferences
                (MyPREFERENCES, Context.MODE_PRIVATE);
        String boot = sharedpreferenceboot.getString("boot", "null");
        if (boot.equals("checked")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            Log.i("Autostart", "started");
        } else if (boot.equals("unchecked")) {
            Log.i("Autostart", "not starting");

        } else if (boot.equals("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            Log.i("Autostart", "started");
        }

    }
}
