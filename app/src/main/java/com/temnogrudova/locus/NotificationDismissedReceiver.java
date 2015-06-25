package com.temnogrudova.locus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import com.temnogrudova.locus.GpsTrackerService;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceDismissNotification = new Intent(context, GpsTrackerService.class);
        serviceDismissNotification.setAction("ACTION_IS_NOTIFIED");
        context.startService(serviceDismissNotification);
    }
}
