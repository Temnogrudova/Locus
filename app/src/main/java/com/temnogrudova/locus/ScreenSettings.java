package com.temnogrudova.locus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenSettings extends Fragment {
    public ScreenSettings(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        View rootView = inflater.inflate(R.layout.screen_settings, container, false);
        return rootView;
    }
}
