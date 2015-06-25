package com.temnogrudova.locus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class GpsTrackerBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceLauncher = new Intent(context, GpsTrackerService.class);
        SharedPreferences settings = context.getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        final boolean disableMode = settings.getBoolean("disableMode", false);
        if  (disableMode) {
            serviceLauncher.setAction(Constants.ACTION.STOP_ACTION);
        }
        else
        {
            serviceLauncher.setAction(Constants.ACTION.START_ACTION);
        }
        context.startService(serviceLauncher);
    }
}
