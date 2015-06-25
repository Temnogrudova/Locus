package com.temnogrudova.locus;


import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenMap extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to View
                .zoom(10)                   // Sets the zoom=
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.animateCamera(cameraUpdate);

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

    }

    public interface onFabClickListener {
        public void onAddCategory();
        public void onAddNotification(String parent);
    }

    onFabClickListener FabClickListener;

    FloatingActionMenu menu;
    Activity activity;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    GoogleMap mGoogleMap;

   // ArrayList<MarkerOptions> markerOptions;


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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Map");

        // inflate and return the layout
        View v = inflater.inflate(R.layout.screen_map, container,
                false);

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
                FabClickListener.onAddNotification("map");
            }
        });


        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext()) == ConnectionResult.SUCCESS) {
            initMap();
        }
        return v;

    }


    private void initMap() {
        // add fragment
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mapView, mMapFragment);

        fragmentTransaction.addToBackStack("").commit();
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                setCurrentLocation();
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
        notificationDataArrayList = MainActivity.dbM.getNotificationItems();
        LatLng LatLngReminderAddress = null;
        for (NotificationItem notificationItem: notificationDataArrayList){
            if (notificationItem.getItemCategory() == null) {
                MarkerOptions markerOption = setMarkerOptionsOnMap(LatLngReminderAddress, notificationItem);
                markerOptions.add(markerOption);
            }
        }

        ArrayList<NotificationItem> showOnMapNotificationArrayList = new ArrayList<NotificationItem>();
        ArrayList<CategoryItem> categoryItems = new ArrayList<CategoryItem>();
        categoryItems = MainActivity.dbM.getCategoryItems();
        for(CategoryItem categoryItem:categoryItems){
            if (categoryItem.getItemShowOnMap() == 1){
                ArrayList<NotificationItem> categoryNotificationsItems = new ArrayList<NotificationItem>();
                categoryNotificationsItems = MainActivity.dbM.getCategoryNotificationsItems(categoryItem.getItemTitle());
                for(NotificationItem notificationItem:categoryNotificationsItems){
                    showOnMapNotificationArrayList.add(notificationItem);
                }
            };
        }

        LatLngReminderAddress = null;
            for (NotificationItem notificationItem: showOnMapNotificationArrayList){
                MarkerOptions markerOption = setMarkerOptionsOnMap(LatLngReminderAddress,notificationItem);
                markerOptions.add(markerOption);
        }
        for(MarkerOptions markerOptionsItem: markerOptions){
            mGoogleMap.addMarker(markerOptionsItem);
        }
    }




    private MarkerOptions setMarkerOptionsOnMap(LatLng LatLngReminderAddress, NotificationItem notificationItem) {
        MarkerOptions markerOptions = null;
        try {
            LatLngReminderAddress = getLocationFromAddress(notificationItem.getItemLocation());
        }
        catch (NullPointerException e){
            Toast.makeText(activity.getApplicationContext(),"Not correct adress!", Toast.LENGTH_SHORT).show();
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

        Geocoder coder = new Geocoder(getActivity());
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
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
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
                setCurrentLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
    private void setCurrentLocation() {

         mGoogleMap.setMyLocationEnabled(true);

         mLocationRequest = new LocationRequest();
         mLocationRequest.setInterval(10000);
         mLocationRequest.setFastestInterval(5000);
         mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
         mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
}
