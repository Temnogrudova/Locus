package com.temnogrudova.locus.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.temnogrudova.locus.R;
import com.temnogrudova.locus.ScreenSettings;

import java.util.Arrays;
/**
 * Created by 123 on 17.06.2015.
 */
public class DrawerUtil {

    public static final int ACCOUNTS_LOGOUT_ID = 110;

    public interface onUpadteListener {
        public void onNavigatorDrawUpadate(String userName, Drawable drawable);
    }

    static onUpadteListener NavigatorDrawUpadateListener;
    public static AccountHeader.Result getAccountHeader(final AppCompatActivity activity, final Bundle savedInstanceState, final String userName, final Drawable drawable, final Toolbar toolbar) {
        NavigatorDrawUpadateListener = (onUpadteListener) activity;
        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
          final IProfile profileMain = new ProfileDrawerItem().withName(userName).withIcon(drawable)
                .withName(userName);

        // Create the AccountHeader
         final AccountHeader.Result acountHeader = new AccountHeader()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .withProfileImagesClickable(false)
                .withProfileImagesVisible(true)
                .addProfiles(
                        profileMain,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Sign in").withIcon(activity.getResources().getDrawable(R.drawable.ic_facebook_box)).withIdentifier(109),
                        new ProfileSettingDrawerItem().withName("Exit").withIcon(new IconicsDrawable(activity, GoogleMaterial.Icon.gmd_exit_to_app).actionBarSize().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(ACCOUNTS_LOGOUT_ID)
                        //new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile.getIdentifier() == 109) {
                            LoginManager.getInstance().logOut();
                            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("user_friends"));

                        }
                        if (profile.getIdentifier() == 110) {
                            if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
                                LoginManager.getInstance().logOut();
                            }
                            //    LoginManager.getInstance().logOut();
                            String userNameDefault = "Guest";
                            Drawable drawableDefault = activity.getResources().getDrawable(R.drawable.avatar_man);
                            SharedPreferences settings = activity.getSharedPreferences(ScreenSettings.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("user_name", userNameDefault);
                            editor.putString("bm", "");
                            editor.commit();
                            NavigatorDrawUpadateListener.onNavigatorDrawUpadate(userName, drawableDefault);
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        return acountHeader;
    }


    public static Drawer.Result createCommonDrawer(final AppCompatActivity activity, Toolbar toolbar, AccountHeader.Result headerResult) {

        Drawer.Result drawerResult = new Drawer()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_list).withIcon(FontAwesome.Icon.faw_list).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_active).withIcon(FontAwesome.Icon.faw_check).withIdentifier(2),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                }).build();

        return drawerResult;
    }

    }
