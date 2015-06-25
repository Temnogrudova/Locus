package com.temnogrudova.locus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenSettings extends Fragment {
    private Activity activity;
    CheckBox chbDisable;
    public static final String PREFS_NAME = "SETTINGS";

    public interface onDisableCheckedListener {
        public void onDisableIsChecked(boolean isChecked);

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
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenMap()).addToBackStack("").commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
