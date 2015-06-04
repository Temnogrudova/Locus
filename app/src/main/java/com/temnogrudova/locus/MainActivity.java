package com.temnogrudova.locus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity implements ScreenMap.onSomeEventListener {
    Drawer.Result drawerResult;
    Menu mainMenu;

    ScreenMap screenMap;
    ScreenList screenList;
    ScreenActive screenActive;
    ScreenSettings screenSettings;
    DialogAddNotification dialogAddNotification;

    boolean isExistsScreenList;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager =  getSupportFragmentManager();
        screenMap = new ScreenMap();
        screenList = new ScreenList();
        screenActive = new ScreenActive();
        screenSettings = new ScreenSettings();
        dialogAddNotification = new DialogAddNotification();

        isExistsScreenList = false;

        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_list).withIcon(FontAwesome.Icon.faw_list).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_active).withIcon(FontAwesome.Icon.faw_check).withBadge("0").withIdentifier(2),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                }).build();

        this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
        drawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                if (iDrawerItem != null) {
                    if (isExistsScreenList){
                        screenList.showToolbar();
                    }
                    if (iDrawerItem.getIdentifier() == 0) {
                        fragmentManager.beginTransaction().replace(R.id.content_frame, screenMap).addToBackStack("").commit();
                        setMainMenuItems(true);
                    } else if (iDrawerItem.getIdentifier() == 1) {
                        setMainMenuItems(false);
                        if (isExistsScreenList == false){
                            isExistsScreenList = true;
                        }
                        fragmentManager.beginTransaction().replace(R.id.content_frame, screenList).addToBackStack("").commit();
                    } else if (iDrawerItem.getIdentifier() == 2) {
                        setMainMenuItems(false);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, screenActive).addToBackStack("").commit();
                    } else if (iDrawerItem.getIdentifier() == 3) {
                        setMainMenuItems(false);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, screenSettings).addToBackStack("").commit();
                    }
                }
            }
        });


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


    }

    public void setMainMenuItems(boolean isVisibleCrosshairGPS) {
        mainMenu.findItem(R.id.action_crosshairs_gps).setVisible(isVisibleCrosshairGPS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mainMenu = menu;
        setMainMenuItems(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_crosshairs_gps) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void someEvent() {
            fragmentManager.beginTransaction().replace(R.id.content_frame, dialogAddNotification).addToBackStack("").commit();
    }
}
