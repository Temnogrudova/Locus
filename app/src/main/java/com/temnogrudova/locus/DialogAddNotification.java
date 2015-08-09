package com.temnogrudova.locus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.temnogrudova.locus.Utils.KeyboardUtil;
import com.temnogrudova.locus.common.logger.Log;
import com.temnogrudova.locus.common.activities.SampleActivityBase;
import com.temnogrudova.locus.database.dbManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by 123 on 12.05.2015.
 */
public class DialogAddNotification extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    public static final String IS_ENABLE_SWITCH_REMINDER = "POSITION SWITCH REMINDER";
    public static final String IS_ENABLE_SWITCH_CURRENT_POSITION = "POSITION SWITCH CURRENT POSITION";
    public static final String LOCATION = "LOCATION";
    public static final String CATEGORY = "CATEGORY";
    public static final String NOTE = "NOTE";


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(SampleActivityBase.TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    public interface onNotificationItemClickListener {
        public void onDoneAddNotificationItem(NotificationItem notificationItem, String parent, String sub);
    }
    onNotificationItemClickListener notificationItemClickListener;

    private Activity activity;
    dbManager dbM;
    private String title = null;

    View rootView;
    EditText etNotificationTitle;
    View line;
    TextView tvError;
    SwitchCompat switchReminder;
    SwitchCompat switchCurrentGPS;
    private AutoCompleteTextView mAutocompleteView;
    Spinner spCategory;
    EditText etNote;
    boolean isBack = false;
    String currentLocation = null;
    Bundle bundle;

    LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    boolean isLocationService;

    KeyboardUtil keyboardUtil = null;


    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public DialogAddNotification() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            notificationItemClickListener = (onNotificationItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity.findViewById(R.id.shadow).setVisibility(View.INVISIBLE);
        rootView = inflater.inflate(R.layout.dialog_add_notification, container, false);
        dbM = new dbManager(getActivity());
        bundle = this.getArguments();
        setToolbarTitle();
        isLocationService = false;
        etNotificationTitle = (EditText) rootView.findViewById(R.id.etNotificationTitle);
        line = rootView.findViewById(R.id.line);
        tvError= (TextView)rootView.findViewById(R.id.tvError);
        switchReminder = (SwitchCompat) rootView.findViewById(R.id.switchReminder);
        switchCurrentGPS = (SwitchCompat) rootView.findViewById(R.id.switchCurrentGPS);
        mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.etReminderAddress);
        spCategory = (Spinner) rootView.findViewById(R.id.spCategory);
        etNote = (EditText) rootView.findViewById(R.id.etNote);



        if(bundle.getInt("type") ==1) {
            etNotificationTitle.setText(bundle.getString("title"));
        if (bundle.getInt("reminder") == 1) {
            switchReminder.setChecked(true);
        }
        else{
            switchReminder.setChecked(false);
        }
        if (bundle.getInt("current_pos") == 1) {
            switchCurrentGPS.setChecked(true);
        }
        else{
            switchCurrentGPS.setChecked(false);
            final LinearLayout llReminderAddress = (LinearLayout) rootView.findViewById(R.id.llReminderAddress);
            llReminderAddress.setVisibility(View.VISIBLE);

        }
            mAutocompleteView.setText(bundle.getString("location"));

            ArrayList<CategoryItem> categoryItems = new ArrayList<CategoryItem>();
            categoryItems = dbM.getCategoryItems();
            ArrayList<String> categoryTitles = new ArrayList<String>();
            for(CategoryItem categoryItem:categoryItems){
                categoryTitles.add(categoryItem.getItemTitle());
            }
            etNote.setText(bundle.getString("note"));
        }
        else {
            clearAllFields();
        }

        enableKeyboardUtil();

        addRippleAnimationToCloseBtn();
        addRippleAnimationToDoneBtn();
        addAnimationToEtNotification();
        addAnimationToSwitchCurrentGPS();
        addItemsOnAutoCompleteTextView();
        addItemsOnSpinner();

        setCurrentLocation();

         return rootView;
    }

    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }
    private void setToolbarTitle() {
        View l = rootView.findViewById(R.id.dialog_toolbar);
        ((TextView)l.findViewById(R.id.dialog_toolbar_title)).setText(getTitle());
    }


    private void addItemsOnAutoCompleteTextView() {
        if (isNetworkAvailable()) {
            createGoogleAPiConnection();
            // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
            // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
            // the entire world.
            mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                    mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
            mAutocompleteView.setAdapter(mAdapter);
        }
    }

    private void createGoogleAPiConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    private void enableKeyboardUtil() {
        if (keyboardUtil == null) {
            keyboardUtil = new KeyboardUtil(getActivity(), rootView);
            keyboardUtil.disable();
            keyboardUtil.enable();
        }
    }

    private void addAnimationToSwitchCurrentGPS() {
        switchCurrentGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Animation animSlideDown = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.slide_down);
                final Animation animSlideUp = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.slide_up);

                final LinearLayout llReminderAddress = (LinearLayout) rootView.findViewById(R.id.llReminderAddress);
                final LinearLayout llContainer = (LinearLayout) rootView.findViewById(R.id.llContainer);

                if (isChecked) {
                    setCurrentLocation();
                    animSlideUp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            int height = etNotificationTitle.getHeight();
                            Animation an = new TranslateAnimation(0, 0, 0, -height);
                            an.setFillEnabled(true);
                            an.setDuration(300);
                            llContainer.startAnimation(an);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            llReminderAddress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    llReminderAddress.startAnimation(animSlideUp);

                } else {
                    isLocationService = false;
                    currentLocation = null;
                    int height = etNotificationTitle.getHeight();
                    Animation animation = new TranslateAnimation(0, 0, 0, height);
                    animation.setFillEnabled(true);
                    animation.setDuration(200);
                    animation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationEnd(Animation animatiofillAftern) {
                            // TODO Auto-generated method stub
                            llReminderAddress.setVisibility(View.VISIBLE);
                            llReminderAddress.startAnimation(animSlideDown);
                        }
                    });
                    llContainer.startAnimation(animation);
                }
            }
        });
    }

    private void addAnimationToEtNotification() {
        final Animation animScaleUp = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.show_up);
        final Animation animScaleDown = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.hide_down);
        final TextView tvNotificationTitle = (TextView) rootView.findViewById(R.id.tvNotificationTitle);
        etNotificationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (etNotificationTitle.getText().toString().equals("")) {
                    if (!isBack) {
                        tvNotificationTitle.startAnimation(animScaleUp);
                    }
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvNotificationTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etNotificationTitle.getText().toString().equals("")) {
                    tvNotificationTitle.setVisibility(View.INVISIBLE);
                    if (!isBack) {
                        tvNotificationTitle.startAnimation(animScaleDown);
                    } else {
                        isBack = false;
                    }
                }
            }
        });
    }

    private void addRippleAnimationToDoneBtn() {
        final RippleView rvDone = (RippleView) rootView.findViewById(R.id.rvDone);
        rvDone.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (etNotificationTitle.getText().toString().equals("")) {
                    line.setVisibility(View.VISIBLE);
                    tvError.setVisibility(View.VISIBLE);
                    new AsyncTask<Void, Void, Void>()
                    {

                        protected Void doInBackground(Void... params)
                        {
                            try {
                                Thread.sleep(3000);                   // sleep 5 seconds
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        protected void onPostExecute (Void result)
                        {
                            // fade out view nicely
                            AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.0f);
                            alphaAnim.setDuration(400);
                            alphaAnim.setAnimationListener(new Animation.AnimationListener()
                            {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                public void onAnimationEnd(Animation animation)
                                {
                                    // make invisible when animation completes, you could also remove the view from the layout
                                    line.setVisibility(View.INVISIBLE);
                                    tvError.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            line.startAnimation(alphaAnim);
                            tvError.startAnimation(alphaAnim);
                        }
                    }.execute();
                }
                else {
                NotificationItem notificationItem = new NotificationItem();
                int reminder = 0;
                if (switchReminder.isChecked()) {
                    reminder = 1;
                } else {
                    reminder = 0;
                }

                boolean b = switchCurrentGPS.isChecked();
                if (b) {
                    notificationItem.setItemLocation(currentLocation);
                } else {
                    notificationItem.setItemLocation(mAutocompleteView.getText().toString());
                }
                notificationItem.setItemTitle(etNotificationTitle.getText().toString());
                notificationItem.setItemReminder(reminder);
                notificationItem.setItemNote(etNote.getText().toString());
                notificationItem.setItemActive(0);

                String categoryTitle = null;
                if (spCategory.getSelectedItemPosition() != 0) {
                    categoryTitle = spCategory.getSelectedItem().toString();
                }
                notificationItem.setItemCategory(categoryTitle);

                if (bundle.getInt("type") == 0) {
                    dbM.addNotification(notificationItem);
                    getFragmentManager().popBackStack();
                } else {
                    String oldCategory = bundle.getString("category");
                    if (((notificationItem.getItemCategory() != null) &&
                            (oldCategory != null) &&
                            (!oldCategory.equals(notificationItem.getItemCategory()))) ||
                            (((notificationItem.getItemCategory() == null) && (oldCategory != null)) ||
                                    ((notificationItem.getItemCategory() != null) && (oldCategory == null)))) {
                        ArrayList<CategoryItem> categoryItems = new ArrayList<CategoryItem>();
                        categoryItems = dbM.getCategoryItems();
                        for (CategoryItem categoryItem : categoryItems) {
                            if (categoryItem.getItemTitle().equals(notificationItem.getItemCategory())) {
                                notificationItem.setItemReminder(categoryItem.getItemReminder());
                            }
                        }
                    }
                }
                dbM.updSelectedNotification(bundle.getString("title"), notificationItem);
                String parent = bundle.getString("parent");
                String sub = bundle.getString("sub");
                notificationItemClickListener.onDoneAddNotificationItem(notificationItem, parent, sub);
            }

        }

        });


        TextView textView = (TextView) rootView.findViewById(R.id.tvDone);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rvDone.animateRipple(event);
            return true;
            }
        });
    }

    private void
    addRippleAnimationToCloseBtn() {
        final RippleView rvClose = (RippleView) rootView.findViewById(R.id.rvBack);
        rvClose.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if ((!etNotificationTitle.getText().toString().equals("")) ||
                        (!mAutocompleteView.getText().toString().equals("")) ||
                        (!switchReminder.isChecked()) ||
                        (!switchCurrentGPS.isChecked()) ||
                        (spCategory.getSelectedItemPosition() != 0) ||
                        (!etNote.getText().toString().equals(""))) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Are you sure?")
                            .setMessage("You have unsaved changes.\nDo you want to quit without saving?")
                            .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    //clearAllFields();
                                    getFragmentManager().popBackStack();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                } else {
                    getFragmentManager().popBackStack();
                }

            }
        });

        ImageView ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
        ivClose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rvClose.animateRipple(event);
                return true;
            }
        });

    }

    private void clearAllFields() {
        etNotificationTitle.getText().clear();
        switchReminder.setChecked(true);
        switchCurrentGPS.setChecked(true);
        mAutocompleteView.getText().clear();
        spCategory.setAdapter(null);
        etNote.getText().clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                lat = intent.getDoubleExtra("lat", 0);
                longit = intent.getDoubleExtra("longit", 0);
                setAddress(lat, longit);
            }
        };
        IntentFilter intFilt = new IntentFilter(Constants.ACTION.SET_CURRENT_LOCATION);
        // регистрируем (включаем) BroadcastReceiver
        activity.registerReceiver(br, intFilt);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setAddress(double lat, double longit) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        List<Address> addresses = null;
        String errorMessage = "";
        try {
            addresses = geocoder.getFromLocation(
                    lat,
                    longit,
                    // In this sample, get just a single address.
                    1);
            while (addresses.size()==0) {
                addresses = geocoder.getFromLocation(
                        lat,
                        longit,
                        // In this sample, get just a single address.
                        1);
            }
        }
        catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service not avaliable";

        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid lang long used";
        }
        String s = "";
        if (addresses!=null) {
            Address address = addresses.get(0);
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                s = s + address.getAddressLine(i) + ", ";
            }
            s = s + address.getCountryName();

        }
        currentLocation = s;
    }


    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        activity.findViewById(R.id.shadow).setVisibility(View.VISIBLE);
        isBack = true;
        keyboardUtil = null;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }


    public void addItemsOnSpinner() {
        ArrayList<CategoryItem> CategoryItems = dbM.getCategoryItems();
        List<String> list = new ArrayList<String>();
        list.add("No category");
        for(CategoryItem categoryItem:CategoryItems){
            list.add(categoryItem.getItemTitle());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(dataAdapter);

        if(bundle.getInt("type") == 1) {
            String category = bundle.getString("category");
            int index = 0;
            int count = spCategory.getCount();
            for (int i = 0; i < count; i++) {
                String s = spCategory.getItemAtPosition(i).toString();
                if (s.equals(category)) {
                    index = i;
                    break;
                }
            }
            spCategory.setSelection(index);
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(SampleActivityBase.TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
          /*  PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            */
          //  LatLng LatLngReminderAddres = getLocationFromAddress((String) item.description);
            //latitude = LatLngReminderAddres.latitude;
           // longitude = LatLngReminderAddres.longitude;
           // Log.i(SampleActivityBase.TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    /*
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(SampleActivityBase.TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
           /* mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }
            */
/*
            Log.i(SampleActivityBase.TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    */
/*
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));


    }
*/

    BroadcastReceiver br;
    double lat= 0;
    double longit = 0;

    private void setCurrentLocation() {
        Intent serviceLauncher = new Intent(activity, GpsTrackerService.class);
        serviceLauncher.setAction(Constants.ACTION.GET_CURRENT_LOCATION);
        activity.startService(serviceLauncher);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etNotificationTitle!=null) {
            outState.putString(NOTIFICATION_TITLE, etNotificationTitle.getText().toString());
        }
        if (switchReminder!=null) {
            outState.putBoolean(IS_ENABLE_SWITCH_REMINDER, switchReminder.isChecked());
        }
        if (switchCurrentGPS!=null) {
            outState.putBoolean(IS_ENABLE_SWITCH_CURRENT_POSITION, switchCurrentGPS.isChecked());
        }
        if (mAutocompleteView!=null) {
            outState.putString(LOCATION, mAutocompleteView.getText().toString());
        }
        if (spCategory!=null) {
            outState.putInt(CATEGORY, spCategory.getSelectedItemPosition());
        }
        if (etNote!=null) {
            outState.putString(NOTE, etNote.getText().toString());
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unregisterReceiver(br);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbM!=null) {
            dbM.close();
        }
    }
}
