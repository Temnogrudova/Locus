package com.temnogrudova.locus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;

import com.temnogrudova.locus.database.dbManager;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.Scrollable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenSettings extends Fragment  {
    private Activity activity;
    CheckBox chbDisable;
    RecyclerView RemindersSetting;
    dbManager dbM;
    public static final String PREFS_NAME = "SETTINGS";

    public interface onDisableCheckedListener {
        public void onDisableIsChecked(boolean isChecked);
        public void onBackViewNotificationItem(String parent);

    }
    onDisableCheckedListener DisableCheckedListener;
    public ScreenSettings(){}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            DisableCheckedListener = (onDisableCheckedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        View rootView = inflater.inflate(R.layout.screen_settings, container, false);
        dbM = new dbManager(getActivity());
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        final boolean disableMode = settings.getBoolean("disableMode", false);
        chbDisable = (CheckBox)rootView.findViewById(R.id.chbDisable);
        chbDisable.setChecked(disableMode);
        chbDisable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("disableMode", isChecked);
                // Commit the edits!
                editor.commit();
                DisableCheckedListener.onDisableIsChecked(isChecked);
            }


        });

        RemindersSetting = (RecyclerView)rootView.findViewById(R.id.rvRemindersSetting);
        RemindersSetting.setLayoutManager(new LinearLayoutManager(getActivity()));
        RemindersSetting.setHasFixedSize(false);
        ArrayList<SettingItem> items = new ArrayList<SettingItem>();
        items.add(new SettingItem(getString(R.string.clear_reminders),getString(R.string.clear_reminders_prompt)));
        items.add(new SettingItem(getString(R.string.sound_reminders),getString(R.string.sound_reminders_prompt)));
        SettingRecyclerAdapter recyclerAdapter = new SettingRecyclerAdapter(items);
        RemindersSetting.setAdapter(recyclerAdapter);

        return rootView;
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
                    DisableCheckedListener.onBackViewNotificationItem("map");// getFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenMap()).addToBackStack("").commit();
                    return true;
                }
                return false;
            }
        });
    }

    public void onClearRemindersItem() {
        ArrayList<NotificationItem> notificationDataArrayList = dbM.getNotificationItems();
        for(int i = 0; i<notificationDataArrayList.size();i++){
            if (notificationDataArrayList.get(i).getItemTitle()!=null){
                dbM.delSelectedNotification(notificationDataArrayList.get(i).getItemTitle());
            }
        }
    }
    public void onSoundRemindersItem() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Choose ringtone");
        Uri uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 1l);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbM!=null) {
            dbM.close();
        }
    }



}
