package com.temnogrudova.locus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.temnogrudova.locus.Utils.DrawerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements
        ScreenMap.onFabClickListener,
        ScreenList.onFabClickListener,
        ScreenActive.onFabClickListener,
        CategoryRecyclerAdapter.onCategoryItemClickListener,
        NotificationRecyclerAdapter.onNotificationItemClickListener,
        DialogViewCategoryItem.onCategoryItemEditClickListener,
        DialogViewNotificationItem.onNotificationItemEditClickListener,
        DialogAddCategory.onCategoryItemClickListener,
        DialogAddNotification.onNotificationItemClickListener,
        ScreenSettings.onDisableCheckedListener,
        SettingRecyclerAdapter.onSettingItemClickListener,
        DrawerUtil.onUpadteListener

{
    public static final String ID_FRAGMENT = "ID_FRAGMENT";
    AccountHeader.Result  headerResult;
    Drawer.Result drawerResult;
    Toolbar toolbar;
    ScreenMap screenMap;
    ScreenList screenList;
    ScreenActive screenActive;
    ScreenSettings screenSettings= null;
    DialogAddNotification dialogAddNotification;
    DialogAddCategory dialogAddCategory;
    DialogViewCategoryItem dialogViewCategoryItem;
    DialogViewNotificationItem dialogViewNotificationItem;

    boolean isExistsScreenList;
    FragmentManager fragmentManager;
    int mCurrentSelectedPosition;
    private boolean mRotated;
    Bundle savedInstanceState = null;

    boolean currentlyTracking;
    CallbackManager callbackManager;
    String saveThis;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences(ScreenSettings.PREFS_NAME, Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", true);

        this.savedInstanceState = savedInstanceState;
        Boolean nonConfigState =
                (Boolean)getLastCustomNonConfigurationInstance();
        if (nonConfigState == null) {
            mRotated = false;
            mCurrentSelectedPosition = 0;
           }
        else {
            mRotated = nonConfigState.booleanValue();
            mCurrentSelectedPosition = savedInstanceState.getInt(ID_FRAGMENT);
        }

        setContentView(R.layout.main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager =  getSupportFragmentManager();
        screenMap = new ScreenMap();
        screenList = new ScreenList();
        screenActive = new ScreenActive();
        screenSettings = new ScreenSettings();
        dialogAddNotification = new DialogAddNotification();
        dialogAddCategory = new DialogAddCategory();
        dialogViewCategoryItem = new DialogViewCategoryItem();
        dialogViewNotificationItem = new DialogViewNotificationItem();

        isExistsScreenList = false;

        SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        String userName = settings.getString("user_name", "Guest");
        String bm = settings.getString("bm", "");
        Drawable drawable = null;
        if (bm.equals("")){
            drawable = getResources().getDrawable(R.drawable.avatar_man);
        }
        else{
            byte[] b = Base64.decode(bm, Base64.DEFAULT);
            Bitmap icon = BitmapFactory.decodeByteArray(b, 0, b.length);

            drawable = new BitmapDrawable(getResources(), icon);
        }
      //  headerResult = DrawerUtil.getAccountHeader(MainActivity.this, savedInstanceState, userName, drawable, toolbar);
        onNavigatorDrawUpadate(userName, drawable);
     /*   drawerResult = DrawerUtil.createCommonDrawer(MainActivity.this, toolbar, headerResult);
        drawerResult.setSelectionByIdentifier(mCurrentSelectedPosition);
        drawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
               NavigatorDrawClickItem(iDrawerItem);
            }
        });

*/
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
              //  onNavigatorDrawUpadate();
              //  Toast.makeText(getApplicationContext(), "in LoginResult on success", Toast.LENGTH_LONG).show();
                final String userId = loginResult.getAccessToken().getUserId();

                AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>(){
                    protected Bitmap doInBackground(Void... p) {
                        Bitmap bm = null;
                        try {
                            URL aURL = new URL("https://graph.facebook.com/"+userId+"/picture?type=large");
                            URLConnection conn = aURL.openConnection();
                            conn.setUseCaches(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BufferedInputStream bis = new BufferedInputStream(is);
                            bm = BitmapFactory.decodeStream(bis);
                            bis.close();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return bm;
                    }

                    protected void onPostExecute(final Bitmap bm){
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        try {
                                            String fName = object.getString("first_name");
                                            String lName = object.getString("last_name");
                                            String userName = fName+" "+ lName;
                                            SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("user_name", userName);
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                                            byte[] b = baos.toByteArray();
                                            saveThis = Base64.encodeToString(b, Base64.DEFAULT);
                                            editor.putString("bm",saveThis );
                                            // Commit the edits!
                                            editor.commit();
                                            final Drawable drawable = new BitmapDrawable(getResources(), bm);
                                            headerResult = DrawerUtil.getAccountHeader(MainActivity.this, savedInstanceState, userName, drawable, toolbar);
                                            onNavigatorDrawUpadate(userName, drawable);
                                         //   drawerResult = DrawerUtil.createCommonDrawer(MainActivity.this, toolbar, headerResult);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                request.executeAsync();

                    }
                };
                t.execute();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "in LoginResult on cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "in LoginResult on error", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddCategory() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString("title", "");
        bundle.putInt("reminder", 1);
        bundle.putInt("show_on_map", 1);
        dialogAddCategory = new DialogAddCategory();
        dialogAddCategory.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogAddCategory).addToBackStack("").commit();
        dialogAddCategory.setTitle(getResources().getString(R.string.tv_add_category));
        Boolean nonConfigState =
                (Boolean)getLastCustomNonConfigurationInstance();
        if (nonConfigState != null) {
            ((EditText) dialogAddCategory.getView().findViewById(R.id.etCategoryTitle)).setText(savedInstanceState.getCharSequence(DialogAddCategory.CATEGORY_TITLE));
            ((SwitchCompat) dialogAddCategory.getView().findViewById(R.id.switchReminder)).setChecked(savedInstanceState.getBoolean(DialogAddCategory.IS_ENABLE_SWITCH_REMINDER));
            ((SwitchCompat) dialogAddCategory.getView().findViewById(R.id.switchShowOnMap)).setChecked(savedInstanceState.getBoolean(DialogAddCategory.IS_ENABLE_SWITCH_SHOW_ON_MAP));
        }
    }

    @Override
    public void onAddNotification(String parent, String sub) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString("title", "");
        bundle.putInt("reminder", 1);
        bundle.putInt("current_pos", 1);
        bundle.putString("location", "");
        bundle.putString("category", "No category");
        bundle.putString("note","");
        bundle.putString("parent",parent);
        bundle.putString("sub", sub);
        dialogAddNotification = new DialogAddNotification();
        dialogAddNotification.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogAddNotification).addToBackStack("").commit();
        dialogAddNotification.setTitle(getResources().getString(R.string.tv_add_notification));
        Boolean nonConfigState =
                (Boolean)getLastCustomNonConfigurationInstance();
        if (nonConfigState != null) {
            ((EditText) dialogAddNotification.getView().findViewById(R.id.etNotificationTitle)).setText(savedInstanceState.getCharSequence(DialogAddNotification.NOTIFICATION_TITLE));
            ((AutoCompleteTextView) dialogAddNotification.getView().findViewById(R.id.etReminderAddress)).setText(savedInstanceState.getCharSequence(DialogAddNotification.LOCATION));
            ((EditText) dialogAddNotification.getView().findViewById(R.id.etNote)).setText(savedInstanceState.getCharSequence(DialogAddNotification.NOTE));
            ((SwitchCompat) dialogAddNotification.getView().findViewById(R.id.switchReminder)).setChecked(savedInstanceState.getBoolean(DialogAddNotification.IS_ENABLE_SWITCH_REMINDER));
            ((SwitchCompat) dialogAddNotification.getView().findViewById(R.id.switchCurrentGPS)).setChecked(savedInstanceState.getBoolean(DialogAddNotification.IS_ENABLE_SWITCH_CURRENT_POSITION));
            ((Spinner) dialogAddNotification.getView().findViewById(R.id.spCategory)).setSelection(savedInstanceState.getInt(DialogAddNotification.CATEGORY));
        }
    }

    @Override
    public void onEditCategory(CategoryItem categoryItem) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("title", categoryItem.getItemTitle());
        bundle.putInt("reminder", categoryItem.getItemReminder());
        bundle.putInt("show_on_map", categoryItem.getItemShowOnMap());
        dialogAddCategory = new DialogAddCategory();
        dialogAddCategory.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogAddCategory).addToBackStack("").commit();
        dialogAddCategory.setTitle(getResources().getString(R.string.tv_edit_category));
    }


    public void onEditNotification(NotificationItem notificationItem, String parent, String sub) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("title", notificationItem.getItemTitle());
        bundle.putInt("reminder", notificationItem.getItemReminder());
        bundle.putInt("current_pos", 0);
        bundle.putString("location", notificationItem.getItemLocation());
        bundle.putString("category", notificationItem.getItemCategory());
        bundle.putString("note", notificationItem.getItemNote());
        bundle.putString("parent",parent);
        bundle.putString("sub", sub);
        dialogAddNotification = new DialogAddNotification();
        dialogAddNotification.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogAddNotification).addToBackStack("").commit();
        dialogAddNotification.setTitle(getResources().getString(R.string.tv_edit_notification));
    }

    @Override
    public void onViewCategoryItem(CategoryItem categoryItem) {
        Bundle bundle = new Bundle();
        bundle.putString("title", categoryItem.getItemTitle());
        bundle.putInt("reminder", categoryItem.getItemReminder());
        bundle.putInt("show_on_map", categoryItem.getItemShowOnMap());
        dialogViewCategoryItem = new DialogViewCategoryItem();
        dialogViewCategoryItem.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogViewCategoryItem).addToBackStack("").commit();
    }

    @Override
    public void onViewNotificationItem(NotificationItem notificationItem, String parent) {
        Bundle bundle = new Bundle();
        bundle.putString("title", notificationItem.getItemTitle());
        bundle.putInt("reminder", notificationItem.getItemReminder());
        bundle.putString("location", notificationItem.getItemLocation());
        bundle.putString("note", notificationItem.getItemNote());
        bundle.putString("category", notificationItem.getItemCategory());
        bundle.putString("parent",parent);
        dialogViewNotificationItem = new DialogViewNotificationItem();
        dialogViewNotificationItem.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogViewNotificationItem).addToBackStack("").commit();

    }

    @Override
    public void onDoneAddNotificationItem(NotificationItem notificationItem, String parent, String sub) {
       if (sub!=null) {
           if (sub.equals("view_notification")) {
               onViewNotificationItem(notificationItem, parent);
           }
       }
        else {
            if (parent.equals("map")) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
            } else if (parent.equals("list")) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenList).addToBackStack("").commit();
            } else if (parent.equals("active")) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenActive).addToBackStack("").commit();
            }
        }
    }

    @Override
    public void onBackViewNotificationItem(String parent) {
        if (parent.equals("map")){
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
            drawerResult.setSelection(0);
        }
        else if (parent.equals("list")) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenList).addToBackStack("").commit();
        }
        else if (parent.equals("active")){
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenActive).addToBackStack("").commit();
        }
     }



    @Override
    public void onStart(){
        super.onStart();
        if(!mRotated){
            this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.

        AppEventsLogger.activateApp(this);


        SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        final boolean disableMode = settings.getBoolean("disableMode", false);
        onDisableIsChecked(disableMode);

        final boolean start_from_notification = settings.getBoolean("start_from_notification", false);
        if (start_from_notification){

            Snackbar snackbar =  Snackbar.with(getApplicationContext());
            snackbar.text("You have a new place reminder"); // text to display
            snackbar.duration(5000);
            snackbar.actionLabel("→");
            snackbar.actionListener(new ActionClickListener() {
                @Override
                public void onActionClicked(Snackbar snackbar) {
                     drawerResult.setSelection(2);
                   // fragmentManager.beginTransaction().replace(R.id.content_frame, screenActive).addToBackStack("").commit();
                }
            });
            snackbar.setPadding(8, 0, 0, 0);
            snackbar.show(this);

            // activity where it is displayed
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("start_from_notification", false);
            // Commit the edits!
            editor.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        mRotated = false;
        if (isChangingConfigurations()) {
            int changingConfig = getChangingConfigurations();
            if ((changingConfig & ActivityInfo.CONFIG_ORIENTATION) == ActivityInfo.CONFIG_ORIENTATION) {
                mRotated = true;
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mRotated ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ID_FRAGMENT, mCurrentSelectedPosition);

    }

    @Override
    public void onDisableIsChecked(boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences(ScreenSettings.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if  (isChecked) {
            Intent startLockIntent = new Intent(MainActivity.this, GpsTrackerService.class);
            startLockIntent.setAction(Constants.ACTION.STOP_ACTION);
            startService(startLockIntent);
        }
        else{
            if (currentlyTracking ==true) {
                if (!isFirst()) {
                    Intent startLockIntent = new Intent(MainActivity.this, GpsTrackerService.class);
                    startLockIntent.setAction(Constants.ACTION.STOP_ACTION);
                    startService(startLockIntent);
                    currentlyTracking = false;
                    editor.putBoolean("currentlyTracking", false);
                }
            }
                Intent startLockIntent = new Intent(MainActivity.this, GpsTrackerService.class);
                startLockIntent.setAction(Constants.ACTION.START_ACTION);
                startService(startLockIntent);
                currentlyTracking = true;
                editor.putBoolean("currentlyTracking", true);
        }
    }

    public boolean isFirst(){
        final SharedPreferences reader = getSharedPreferences(ScreenSettings.PREFS_NAME, Context.MODE_PRIVATE);
        final boolean first = reader.getBoolean("is_first", true);
        if(first){
            final SharedPreferences.Editor editor = reader.edit();
            editor.putBoolean("is_first", false);
            editor.commit();
        }
        return first;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                String ringTonePath = uri.toString();
                Intent soundIntent = new Intent(MainActivity.this, GpsTrackerService.class);
                soundIntent.setAction(Constants.ACTION.SET_NOTOFICATION_SOUND);
                soundIntent.putExtra("notification_sound",ringTonePath);
                startService(soundIntent);
            }
        }
    }

    @Override
    public void onClearRemindersItem() {
        screenSettings.onClearRemindersItem();
    }

    @Override
    public void onSoundReminder(){
       screenSettings.onSoundRemindersItem();
    }

    @Override
    public void onNavigatorDrawUpadate(String userName, Drawable drawable) {
        headerResult = DrawerUtil.getAccountHeader(MainActivity.this, savedInstanceState, userName, drawable, toolbar);
        drawerResult = DrawerUtil.createCommonDrawer(MainActivity.this, toolbar, headerResult);
        drawerResult.setSelectionByIdentifier(mCurrentSelectedPosition);
        drawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                NavigatorDrawClickItem(iDrawerItem);
            }
        });
    }

    public void NavigatorDrawClickItem(IDrawerItem iDrawerItem){
        mCurrentSelectedPosition = iDrawerItem.getIdentifier();
        if (iDrawerItem != null) {
            if (isExistsScreenList){
                screenList.showToolbar();
            }
            if (iDrawerItem.getIdentifier() == 0) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
            } else if (iDrawerItem.getIdentifier() == 1) {
                if (isExistsScreenList == false){
                    isExistsScreenList = true;
                }
                screenList = new ScreenList();
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenList).addToBackStack("").commit();
            } else if (iDrawerItem.getIdentifier() == 2) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenActive).addToBackStack("").commit();
            } else if (iDrawerItem.getIdentifier() == 3) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, screenSettings).addToBackStack("").commit();
            }
        }
    }
}