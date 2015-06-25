package com.temnogrudova.locus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.temnogrudova.locus.Utils.DrawerUtil;
import com.temnogrudova.locus.database.dbManager;

public class MainActivity extends AppCompatActivity implements ScreenMap.onFabClickListener,
        ScreenList.onFabClickListener,
        ScreenActive.onFabClickListener,
        CategoryRecyclerAdapter.onCategoryItemClickListener,
        NotificationRecyclerAdapter.onNotificationItemClickListener,
        DialogViewCategoryItem.onCategoryItemEditClickListener,
        DialogViewNotificationItem.onNotificationItemEditClickListener,
        DialogAddCategory.onCategoryItemClickListener,
        DialogAddNotification.onNotificationItemClickListener,
        ScreenSettings.onDisableCheckedListener
{
    public static final String ID_FRAGMENT = "ID_FRAGMENT";
    AccountHeader.Result  headerResult;
    Drawer.Result drawerResult;

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
    static dbManager dbM;

    boolean currentlyTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbM = new dbManager(this);

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

        headerResult = DrawerUtil.getAccountHeader(MainActivity.this, savedInstanceState);
        drawerResult = DrawerUtil.createCommonDrawer(MainActivity.this, toolbar, headerResult);
        drawerResult.setSelectionByIdentifier(mCurrentSelectedPosition);
        drawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
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
    public void onAddNotification(String parent) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        bundle.putString("title", "");
        bundle.putInt("reminder", 1);
        bundle.putInt("current_pos", 1);
        bundle.putString("location", "");
        bundle.putString("category", "No category");
        bundle.putString("note","");
        bundle.putString("parent",parent);
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


    public void onEditNotification(NotificationItem notificationItem, String parent) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("title", notificationItem.getItemTitle());
        bundle.putInt("reminder", notificationItem.getItemReminder());
        bundle.putInt("current_pos", 0);
        bundle.putString("location", notificationItem.getItemLocation());
        bundle.putString("category", notificationItem.getItemCategory());
        bundle.putString("note", notificationItem.getItemNote());
        bundle.putString("parent",parent);
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
    public void onViewNotificationItem(NotificationItem notificationItem) {
        Bundle bundle = new Bundle();
        bundle.putString("title", notificationItem.getItemTitle());
        bundle.putInt("reminder", notificationItem.getItemReminder());
        bundle.putString("location", notificationItem.getItemLocation());
        bundle.putString("note", notificationItem.getItemNote());
        bundle.putString("category", notificationItem.getItemCategory());
        dialogViewNotificationItem = new DialogViewNotificationItem();
        dialogViewNotificationItem.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, dialogViewNotificationItem).addToBackStack("").commit();

    }

    @Override
    public void onDoneAddNotificationItem(NotificationItem notificationItem, String parent) {
        if (parent.equals("map")){
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
        }
        else if (parent.equals("list")){
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenList).addToBackStack("").commit();
        }
        else if (parent.equals("active")){
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenActive).addToBackStack("").commit();
        }
        else if (parent.equals("view_notification")){
            onViewNotificationItem(notificationItem);
        }
    }

    @Override
    public void onBackViewNotificationItem() {
            fragmentManager.beginTransaction().replace(R.id.content_frame, screenList).addToBackStack("").commit();
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
        SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        final boolean disableMode = settings.getBoolean("disableMode", false);
        onDisableIsChecked(disableMode);


        final boolean start_from_notification = settings.getBoolean("start_from_notification", false);
        if (start_from_notification){ Toast.makeText(this,"123456", Toast.LENGTH_SHORT).show();}

    }

    @Override
    protected void onPause() {
        super.onPause();
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
        dbM.close();
        SharedPreferences settings = getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("start_from_notification", false);
        // Commit the edits!
        editor.commit();
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
                startLockIntent.putExtra("not_notify", true);
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


}
/*
                drawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null) {

                            if (drawerItem.getIdentifier() == 1) {
                                MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenOne()).commit();
                            } else if (drawerItem.getIdentifier() == 2) {
                                MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenTwo()).commit();
                            } else if (drawerItem.getIdentifier() == 3) {
                                MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenThree()).commit();
                            } else if (drawerItem.getIdentifier() == 70) {
                                // Rate App
                                try {
                                    Intent int_rate = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MainActivity.this.getApplicationContext().getPackageName()));
                                    int_rate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MainActivity.this.getApplicationContext().startActivity(int_rate);
                                } catch (Exception e) {
                                    //
                                }
                            }

                        }
                       /*
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }

                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {
                                    Log.d("test", "Не нажимайте на бейдж, содержащий плюс! :)");
                                }
                            }
                        }
                    }
                });

        */