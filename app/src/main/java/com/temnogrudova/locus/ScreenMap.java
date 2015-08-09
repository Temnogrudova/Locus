package com.temnogrudova.locus;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temnogrudova.locus.database.dbManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenMap extends Fragment{
    public interface onFabClickListener {
        public void onAddCategory();
        public void onAddNotification(String parent, String sub);

    }

    onFabClickListener FabClickListener;

    FloatingActionMenu menu;
    Activity activity;
    dbManager dbM;

    GoogleMap mGoogleMap;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity= activity;

        try {
            FabClickListener = (onFabClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Map");
        // inflate and return the layout
        View v = inflater.inflate(R.layout.screen_map, container,
                false);
        dbM = new dbManager(getActivity());

        setHasOptionsMenu(true);
        menu = (FloatingActionMenu) v.findViewById(R.id.floatingActionMenu);

        FloatingActionButton mFabAddCategory =(FloatingActionButton) v.findViewById(R.id.menu_item_add_category);
        mFabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabClickListener.onAddCategory();
            }
        });

        FloatingActionButton mFabAddNotification =(FloatingActionButton) v.findViewById(R.id.menu_item_add_notification);
        mFabAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabClickListener.onAddNotification("map",null);
            }
        });


        if (isNetworkAvailable()) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext()) == ConnectionResult.SUCCESS) {
                initMap();
            }
        }

        return v;

    }


    private void initMap() {
        // add fragment
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        if (getActivity()!=null) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.mapView, mMapFragment);
            fragmentTransaction.addToBackStack("").commit();
        }
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

             //   setCurrentLocation();
                setNotificationMarkers();
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        menu.close(true);

                    }
                });
            }
        });

    }

    private void setNotificationMarkers() {
        ArrayList<MarkerOptions> markerOptions = new ArrayList<>();

        ArrayList<NotificationItem> notificationDataArrayList = new ArrayList<NotificationItem>();
        notificationDataArrayList = dbM.getNotificationItems();
        for (NotificationItem notificationItem: notificationDataArrayList){
            if (notificationItem.getItemCategory() == null) {
                MarkerOptions markerOption = setMarkerOptionsOnMap(notificationItem);
                markerOptions.add(markerOption);
            }
        }

        ArrayList<NotificationItem> showOnMapNotificationArrayList = new ArrayList<NotificationItem>();
        ArrayList<CategoryItem> categoryItems = new ArrayList<CategoryItem>();
        categoryItems = dbM.getCategoryItems();
        for(CategoryItem categoryItem:categoryItems){
            if (categoryItem.getItemShowOnMap() == 1){
                ArrayList<NotificationItem> categoryNotificationsItems = new ArrayList<NotificationItem>();
                categoryNotificationsItems = dbM.getCategoryNotificationsItems(categoryItem.getItemTitle());
                for(NotificationItem notificationItem:categoryNotificationsItems){
                    showOnMapNotificationArrayList.add(notificationItem);
                }
            };
        }


            for (NotificationItem notificationItem: showOnMapNotificationArrayList){
                MarkerOptions markerOption = setMarkerOptionsOnMap(notificationItem);
                markerOptions.add(markerOption);
        }
        for(MarkerOptions markerOptionsItem: markerOptions){
            if (markerOptionsItem!=null) {
                mGoogleMap.addMarker(markerOptionsItem);
            }
        }
    }




    private MarkerOptions setMarkerOptionsOnMap(NotificationItem notificationItem) {
        MarkerOptions markerOptions = null;
        LatLng LatLngReminderAddress = null;
        try {
            LatLngReminderAddress =  getLocationFromAddress(notificationItem.getItemLocation());
        }
        catch (NullPointerException e){
            Toast.makeText(activity.getApplicationContext(),"Not correct address!", Toast.LENGTH_SHORT).show();
        }
        if (LatLngReminderAddress!=null) {

            if (notificationItem.getItemReminder() == 1) {
                markerOptions = new MarkerOptions().position(LatLngReminderAddress).
                        title(notificationItem.getItemTitle())
                        .snippet(notificationItem.getItemLocation())
                        .icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_map_marker_teal600_48dp)));
            } else {
                markerOptions = new MarkerOptions().position(LatLngReminderAddress).
                        title(notificationItem.getItemTitle())
                        .snippet(notificationItem.getItemLocation())
                        .icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_map_marker_grey600_48dp)));
            }
        }
        return markerOptions;
    }

    public LatLng getLocationFromAddress(String strAddress) {
        LatLng p1 = null;
/*
        ParseTask pt = new ParseTask();
        pt.execute(strAddress);


        if (pt == null) return null;
        String strJson = "";
        try {
            strJson = pt.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        JSONObject dataJsonObj = null;
        try {
            dataJsonObj = new JSONObject(strJson);
            double lng = ((JSONArray)dataJsonObj.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray)dataJsonObj.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

             p1 = new LatLng(lat, lng);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity,"!!!",Toast.LENGTH_SHORT).show();
        }
*/


        Geocoder coder = new Geocoder(activity);
        List<Address> address;


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
    public void onStop() {
        super.onStop();
    }

    public void setCameraLocation(double lat, double longit) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, longit))      // Sets the center of the map to View
                .zoom(10)                   // Sets the zoom=
                .build();
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        if (mGoogleMap!=null) {
            mGoogleMap.animateCamera(cameraUpdate);
            mGoogleMap.setMyLocationEnabled(true);
        }
        else{
         initMap();
         }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        //  mainMenu = menu;
        // setMainMenuItems(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_crosshairs_gps:
                mGoogleMap.setMyLocationEnabled(true);
                setCurrentLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    BroadcastReceiver br;
    double lat= 0;
    double longit = 0;

    @Override
    public void onResume() {
        super.onResume();
        setCurrentLocation();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                lat = intent.getDoubleExtra("lat",0);
                longit = intent.getDoubleExtra("longit",0);
                //  screenMap = new ScreenMap();
                setCameraLocation(lat, longit);



            }
        };
        IntentFilter intFilt = new IntentFilter(Constants.ACTION.SET_CURRENT_LOCATION);
        // регистрируем (включаем) BroadcastReceiver
        activity.registerReceiver(br, intFilt);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    activity.finish();
                    return true;
                }
                return false;
            }
        });



    }
    public void setCurrentLocation() {
         enableLocation();

        Intent serviceLauncher = new Intent(activity, GpsTrackerService.class);
        serviceLauncher.setAction(Constants.ACTION.GET_CURRENT_LOCATION);
        activity.startService(serviceLauncher);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void enableLocation() {
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle(activity.getResources().getString(R.string.enable_location));
            dialog.setMessage(activity.getResources().getString(R.string.enable_location_message));
            dialog.setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbM!=null) {
            dbM.close();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unregisterReceiver(br);
    }
}

/*
    private class ParseTask extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(
                        "http://maps.google.com/maps/api/geocode/json?address="
                                + URLEncoder.encode(params[0], "UTF-8") + "&ka&sensor=false");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);


        }
    }
    */