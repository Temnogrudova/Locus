package com.temnogrudova.locus;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.andexert.library.RippleView;


/**
 * Created by 123 on 12.05.2015.
 */
public class DialogAddNotification extends Fragment {
    private Activity activity;
    private FragmentManager fragmentManager;

    public DialogAddNotification(){}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_add_notification, container, false);
      //  fragmentManager = activity.getFragmentManager();
       // final RippleBackground rippleBackground=(RippleBackground)rootView.findViewById(R.id.content);
        final RippleView rippleView = (RippleView)rootView.findViewById(R.id.more);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getFragmentManager().popBackStack();
            }
        });
       ImageView imageView=(ImageView)rootView.findViewById(R.id.ivClose);
       imageView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               rippleView.animateRipple(event);
               return true;
           }
       });

        final RippleView rippleView1 = (RippleView)rootView.findViewById(R.id.mor);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getFragmentManager().popBackStack();
            }
        });
        TextView textView = (TextView)rootView.findViewById(R.id.tvDone);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rippleView1.animateRipple(event);
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }


}
