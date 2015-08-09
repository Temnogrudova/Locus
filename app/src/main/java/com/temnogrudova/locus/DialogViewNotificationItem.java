package com.temnogrudova.locus;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.temnogrudova.locus.database.dbManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by 123 on 14.06.2015.
 */
public class DialogViewNotificationItem extends Fragment {

    public interface onNotificationItemEditClickListener {
        public void onBackViewNotificationItem(String parent);
        public void onEditNotification(NotificationItem notificationItem, String parent, String sub);
    }
    onNotificationItemEditClickListener notificationItemEditClickListener;

    private Activity activity;
    dbManager dbM;
    Bundle bundle;
    private Integer isCheckedSwitchReminder = null;
    private View rootView;
    NotificationItem notificationItem = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            notificationItemEditClickListener = (onNotificationItemEditClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_view_notification_item, container, false);
        dbM = new dbManager(getActivity());
        bundle = this.getArguments();
        notificationItem = new NotificationItem();
        notificationItem.setItemTitle(bundle.getString("title"));
        notificationItem.setItemReminder(bundle.getInt("reminder"));
        notificationItem.setItemLocation(bundle.getString("location"));
        notificationItem.setItemNote(bundle.getString("note"));
        notificationItem.setItemCategory(bundle.getString("category"));

        setToolbarTitle();
        setSwitchReminder();
        setLocation();
        setCategory();
        setNote();

        addUpDownAnimOptions();

        addRippleAnimationToBackBtn();
        addRippleAnimationToDeleteBtn();
        addRippleAnimationToEditBtn();

        return rootView;
    }

    private void addRippleAnimationToBackBtn() {
        final RippleView rvBack = (RippleView) rootView.findViewById(R.id.rvBack);
        rvBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                notificationItemEditClickListener.onBackViewNotificationItem(bundle.getString("parent"));

            }
        });

        ImageView ivBack = (ImageView) rootView.findViewById(R.id.ivClose);
        ivBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rvBack.animateRipple(event);
                return true;
            }
        });
    }

    private void addRippleAnimationToDeleteBtn() {
        final RippleView rvDelete = (RippleView) rootView.findViewById(R.id.rvDelete);
        rvDelete.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dbM.delSelectedNotification(notificationItem.getItemTitle());
                getFragmentManager().popBackStack();
            }
        });

        ImageView ivDelete = (ImageView) rootView.findViewById(R.id.ivDelete);
        ivDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rvDelete.animateRipple(event);
                return true;
            }
        });
    }

    private void addRippleAnimationToEditBtn() {
        final RippleView rvEdit = (RippleView) rootView.findViewById(R.id.rvEdit);
        rvEdit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String parent = bundle.getString("parent");
                notificationItemEditClickListener.onEditNotification(notificationItem, parent, "view_notification");
               // getFragmentManager().popBackStack();
            }
        });

        ImageView ivEdit = (ImageView) rootView.findViewById(R.id.ivEdit);
        ivEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rvEdit.animateRipple(event);
                return true;
            }
        });
    }

    private void setToolbarTitle() {
        View l = rootView.findViewById(R.id.dialog_toolbar);
        ((TextView)l.findViewById(R.id.dialog_toolbar_title)).setText(notificationItem.getItemTitle());
    }

    private void addUpDownAnimOptions() {
        final ImageButton ibIsShowOptions = (ImageButton) rootView.findViewById(R.id.ibIsShowOptions);
        final LinearLayout llOptions = (LinearLayout) rootView.findViewById(R.id.llOptions);


        ibIsShowOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llOptions.getVisibility() == View.VISIBLE){
                    llOptions.setVisibility(View.GONE);
                    ibIsShowOptions.setImageResource(R.drawable.ic_chevron_up_grey600_24dp);
                }
                else {
                    llOptions.setVisibility(View.VISIBLE);
                    ibIsShowOptions.setImageResource(R.drawable.ic_chevron_down_grey600_24dp);
                }
            }
        });
    }

    public Integer getIsCheckedSwitchReminder() {
        return isCheckedSwitchReminder;
    }

    public void setIsCheckedSwitchReminder(Integer isCheckedSwitchReminder) {
        this.isCheckedSwitchReminder = isCheckedSwitchReminder;
    }

    public void setSwitchReminder() {
        SwitchCompat switchReminder = (SwitchCompat)rootView.findViewById(R.id.switchReminder);
        setIsCheckedSwitchReminder(notificationItem.getItemReminder());
        if (getIsCheckedSwitchReminder() ==0) {
            switchReminder.setChecked(false);
        }
        if (getIsCheckedSwitchReminder() ==1) {
            switchReminder.setChecked(true);
        }
    }

    private void setLocation() {
        TextView tvLocation = (TextView) rootView.findViewById(R.id.tvLocation);
        tvLocation.setText(notificationItem.getItemLocation());
    }

    private void setCategory() {
        TextView tvCategory = (TextView) rootView.findViewById(R.id.tvCategory);
        if(notificationItem.getItemCategory() != null){
            tvCategory.setText(notificationItem.getItemCategory());
        }
        else {
            tvCategory.setText("No category");
        }
    }

    private void setNote() {
        TextView tvNote = (TextView) rootView.findViewById(R.id.tvNote);
        if(notificationItem.getItemNote() != null){
            tvNote.setText(notificationItem.getItemNote());
        }
        else {
            tvNote.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    notificationItemEditClickListener.onBackViewNotificationItem(bundle.getString("parent"));
                    return true;
                }
                return false;
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbM!=null) {
            dbM.close();
        }
    }

}
