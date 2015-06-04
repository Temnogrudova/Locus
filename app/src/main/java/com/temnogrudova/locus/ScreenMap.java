package com.temnogrudova.locus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenMap extends Fragment
{

    public interface onSomeEventListener {
        public void someEvent();
    }

    onSomeEventListener someEventListener;

    FloatingActionMenu menu;
    Activity activity;

    ShowcaseView sv;
    GoogleMap mGoogleMap;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity= activity;

        try {
            someEventListener = (onSomeEventListener) activity;
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

        menu = (FloatingActionMenu) v.findViewById(R.id.floatingActionMenu);


        FloatingActionButton mFabAddCategory =(FloatingActionButton) v.findViewById(R.id.menu_item_add_category);
        mFabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity,"add Category", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton mFabAddNotification =(FloatingActionButton) v.findViewById(R.id.menu_item_add_notification);
        mFabAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someEventListener.someEvent();
            }
        });


        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext()) == ConnectionResult.SUCCESS) {
            initMap();
          /*  if (locations != null) {
                addMarkers();
            }
            moveMapToCuracao();
            */
        }
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun)
        {
        // Code to run once
        ViewTarget target = new ViewTarget(R.id.Space, activity);

        sv = new ShowcaseView.Builder(activity, true)
                .setTarget(target)
                .setContentTitle("Click to add your notification").setStyle(R.style.CustomTitle2)
                .setStyle(R.style.CustomShowcaseTheme2)
                .hideOnTouchOutside()
                .build();
        sv.hideButton();
        //
        SharedPreferences.Editor editor = wmbPreference.edit();
        editor.putBoolean("FIRSTRUN", false);
        editor.commit();

        }
    }

    private void initMap() {

        // add fragment
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mapView, mMapFragment);

        fragmentTransaction.addToBackStack("").commit();
        /*SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);*/
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                // latitude and longitude
                double latitude = 17.385044;
                double longitude = 78.486671;

                // create marker
               MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title("Hello Maps");
               mGoogleMap.addMarker(marker);

               mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        menu.close(true);

                    }
                });

            }
        });

    }

    /*
    private GoogleMap map;
    MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.screen_map, container, false);


        mMapView = (MapView) v.findViewById(R.id.mapView);
       // mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map = mMapView.getMap();
        double latitude = 17.385044;
        double longitude = 78.486671;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");
        // mMapView.onCreate(savedInstanceState);
       // map = ((MapView)(v.findViewById(R.id.mapView))).getMap();
/*
        Marker hamburg = googleMap.addMarker(new MarkerOptions().position(HAMBURG)
                .title("Hamburg"));
        /*
        Marker kiel = googleMap.addMarker(new MarkerOptions()
                .position(KIEL)
                .title("Kiel")
                .snippet("Kiel is cool")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));
*/
        // Move the camera instantly to hamburg with a zoom of 15.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

        // Zoom in, animating the camera.
 /*       googleMap
                .animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

return v;
}
*/

}
