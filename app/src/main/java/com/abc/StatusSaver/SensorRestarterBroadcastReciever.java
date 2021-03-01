package com.abc.StatusSaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SensorRestarterBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(SensorRestarterBroadcastReciever.class.getSimpleName(), "Service Stopped!");
        //context.startService(new Intent(context, Isevice.class)); // Restart your service here
    }

}

