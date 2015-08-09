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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Ekaterina Temnogrudova on 28.04.2015.
 */

public class GpsTrackerService extends Service  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Notification notif;
    String notificationSound= null;
    NotificationManager nm;
    Integer num = 0;
    boolean isGetCurLoc =false;
    dbManager dbM;
    @Override
    public void onCreate() {
        super.onCreate();
        dbM = new dbManager(this);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (isNetworkAvailable()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(2500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String contentTitle;
        String contentText;
        if (isNetworkAvailable()) {
            if (intent != null) {
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

                } else if (intent.getAction().equals(Constants.ACTION.START_ACTION)) {
                    contentTitle = "Locus is listening";
                    contentText = "You'll be reminded";
                    createNotification(contentTitle, contentText);
                    setCurrentLocation();
                }

                if (intent.getAction().equals("ACTION_IS_NOTIFIED")) {
                    SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("start_from_notification", false);
                    // Commit the edits!
                    editor.commit();
                }
                if (intent.getAction().equals(Constants.ACTION.GET_CURRENT_LOCATION)) {
                    isGetCurLoc = true;
                }
                if (intent.getAction().equals(Constants.ACTION.SET_NOTOFICATION_SOUND)) {
                    notificationSound = intent.getStringExtra("notification_sound");
                    SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("notification_sound", notificationSound);
                    // Commit the edits!
                    editor.commit();

                }
            }

        }

        return START_STICKY;

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
       // notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (isNetworkAvailable()) {
           //curLocation= location;

            Intent intent = new Intent(this, MainActivity.class);
            SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
            final boolean disableMode = settings.getBoolean("disableMode", false);
            if (disableMode) {
                intent.setAction(Constants.ACTION.STOP_ACTION);
            } else {
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

            if ((notificationDataArrayList != null && (newLatLng.latitude != 0)&&(newLatLng.longitude != 0))) {
                for (int i = 0; i < notificationDataArrayList.size(); i++) {
                    if (notificationDataArrayList.get(i).getItemReminder()!=0) {
                        if (!notificationDataArrayList.get(i).getItemLocation().equals("")) {

                            LatLng fromDB = getLocationFromAddress(notificationDataArrayList.get(i).getItemLocation());

                            double deltaLatitude = (fromDB.latitude - newLatLng.latitude);
                            double deltaLongitude = (fromDB.longitude - newLatLng.longitude);

                            if ((Math.abs(deltaLatitude) < 0.007) && (Math.abs(deltaLongitude) < 0.007)) {

                                notificationDataArrayList.get(i).setItemActive(1);
                                notificationDataArrayList.get(i).setItemReminder(0);
                                if (notificationDataArrayList.get(i).getItemCategory()!=null){
                                    ArrayList<CategoryItem> categoryItemsArrayList = new ArrayList<CategoryItem>();
                                    categoryItemsArrayList = dbM.getCategoryItems();
                                    for(CategoryItem categoryItem: categoryItemsArrayList){
                                        Integer num = dbM.getCategoryId(categoryItem.getItemTitle());
                                        if (num==Integer.parseInt(notificationDataArrayList.get(i).getItemCategory())){
                                            notificationDataArrayList.get(i).setItemCategory(categoryItem.getItemTitle());
                                        }
                                    }
                                }
                                dbM.updSelectedNotification(notificationDataArrayList.get(i).getItemTitle(), notificationDataArrayList.get(i));

                                SharedPreferences sharedPreferences = getSharedPreferences(ScreenSettings.PREFS_NAME, Context.MODE_PRIVATE);
                                String sound = sharedPreferences.getString("notification_sound", "");
                                Uri uriSound = Uri.parse(sound);
                                notif = new NotificationCompat.Builder(this)
                                        .setContentTitle(notificationDataArrayList.get(i).getItemTitle())
                                        .setContentText(notificationDataArrayList.get(i).getItemLocation())
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentIntent(pIntent)
                                        .setDeleteIntent(pDismissIntent)
                                        .setAutoCancel(true)
                                        .setPriority(Notification.PRIORITY_HIGH)
                                        .setNumber(num++)
                                        .setSound(uriSound)
                                        .build();
                                notif.defaults |= Notification.DEFAULT_VIBRATE;
                                nm.notify(0, notif);

                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("start_from_notification", true);
                                // Commit the edits!
                                editor.commit();

                            }
                        }
                    }
                }
            }
        }
        if (isGetCurLoc){
            isGetCurLoc = false;
            Intent retIntent = new Intent(Constants.ACTION.SET_CURRENT_LOCATION);
            double  curLocLat= location.getLatitude();
            double  curLocLong= location.getLongitude();
            retIntent.putExtra("lat", curLocLat);
            retIntent.putExtra("longit", curLocLong);
            sendBroadcast(retIntent);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
