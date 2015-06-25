package com.temnogrudova.locus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.temnogrudova.locus.database.dbManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ekaterina Temnogrudova on 28.04.2015.
 */

public class GpsTrackerService extends Service  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Notification notif;
    NotificationManager nm;
    Integer num = 0;

    private boolean isNotify = true;

    dbManager dbM;
    @Override
    public void onCreate() {
        super.onCreate();
        dbM = new dbManager(this);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        /*if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            setCurrentLocation();
        }
        */

    }

    private void setCurrentLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String contentTitle;
        String contentText;
            if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {
            contentTitle = "Locus is disabled";
            contentText = "Touch to enable reminder";
            createNotification(contentTitle, contentText);
                if (mGoogleApiClient != null) {
                    if (mGoogleApiClient.isConnected()) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(
                                mGoogleApiClient, this);
                        mGoogleApiClient.disconnect();
                    }
                    mGoogleApiClient = null;
                }

            }
            else if (intent.getAction().equals(Constants.ACTION.START_ACTION)){
            contentTitle = "Locus is listening";
            contentText = "You'll be reminded";
            setIsNotify(intent.getBooleanExtra("not_notify",true));
            createNotification(contentTitle, contentText);
            setCurrentLocation();
            }

            if (intent.getAction().equals("ACTION_IS_NOTIFIED")){
                setIsNotify(false);
            }




            return START_STICKY;

    }

    private void createNotification(String contentTitle, String contentText){
        Intent intent = new Intent(this, MainActivity.class);
        SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        final boolean disableMode = settings.getBoolean("disableMode", false);
        if  (disableMode) {
        intent.setAction(Constants.ACTION.STOP_ACTION);
        }
        else
        {
            intent.setAction(Constants.ACTION.START_ACTION);
        }
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(Notification.PRIORITY_MIN)
                .setOngoing(true)
                .setContentIntent(pIntent)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(this, MainActivity.class);
        SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        final boolean disableMode = settings.getBoolean("disableMode", false);
        if  (disableMode) {
            intent.setAction(Constants.ACTION.STOP_ACTION);
        }
        else
        {
            intent.setAction(Constants.ACTION.START_ACTION);
        }
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        //PendingIntent for sliding notification
        Intent dismissIntent = new Intent(this, NotificationDismissedReceiver.class);
        PendingIntent pDismissIntent = PendingIntent.getBroadcast(this, 0, dismissIntent, 0);

        ArrayList<NotificationItem> notificationDataArrayList = new ArrayList<NotificationItem>();
        notificationDataArrayList = dbM.getNotificationItems();
        //Convert Location to LatLng
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if ((notificationDataArrayList!=null &&(newLatLng!=null))) {
            for (int i = 0; i < notificationDataArrayList.size(); i++) {
                LatLng fromDB = getLocationFromAddress(notificationDataArrayList.get(i).getItemLocation());
                double deltaLatitude = (fromDB.latitude - newLatLng.latitude);
                double deltaLongitude = (fromDB.longitude - newLatLng.longitude);


                    if ((Math.abs(deltaLatitude) < 0.007) && (Math.abs(deltaLongitude) < 0.007)) {
                        if (isNotify) {
                            notif = new NotificationCompat.Builder(this)
                                    .setContentTitle(notificationDataArrayList.get(i).getItemTitle())
                                    .setContentText(notificationDataArrayList.get(i).getItemLocation())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentIntent(pIntent)
                                    .setDeleteIntent(pDismissIntent)
                                    .setNumber(num++)
                                    .build();
                            nm.notify(0, notif);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("start_from_notification", true);
                            // Commit the edits!
                            editor.commit();
                        }
                    }
                    else
                    {
                        setIsNotify(true);
                    }



            }
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbM.close();
    }

    public boolean getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(boolean isNotify) {
        this.isNotify = isNotify;
    }
}
