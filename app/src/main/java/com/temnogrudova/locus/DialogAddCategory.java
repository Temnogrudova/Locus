package com.temnogrudova.locus;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.temnogrudova.locus.Utils.KeyboardUtil;

import java.util.ArrayList;


/**
 * Created by 123 on 12.05.2015.
 */
public class DialogAddCategory extends Fragment {
    public static final String CATEGORY_TITLE = "CATEGORY_TITLE";
    public static final String IS_ENABLE_SWITCH_REMINDER = "POSITION SWITCH REMINDER";
    public static final String IS_ENABLE_SWITCH_SHOW_ON_MAP = "POSITION SWITCH SHOW ON MAP";

    public interface onCategoryItemClickListener {
        public void onViewCategoryItem(CategoryItem categoryItem);
    }
    onCategoryItemClickListener categoryItemClickListener;


    private String title = null;

    private Activity activity;
    View rootView;
    EditText etCategoryTitle;
    SwitchCompat switchReminder;
    SwitchCompat switchShowOnMap;
    boolean isBack = false;
    Bundle bundle;
    KeyboardUtil keyboardUtil = null;

    public DialogAddCategory() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            categoryItemClickListener = (onCategoryItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_add_category, container, false);
        bundle = this.getArguments();
        setToolbarTitle();

        etCategoryTitle = (EditText) rootView.findViewById(R.id.etCategoryTitle);
        switchReminder = (SwitchCompat) rootView.findViewById(R.id.switchReminder);
        switchShowOnMap = (SwitchCompat) rootView.findViewById(R.id.switchShowOnMap);

        if(bundle.getInt("type") ==1) {
            etCategoryTitle.setText(bundle.getString("title"));
            if (bundle.getInt("reminder") == 1) {
                switchReminder.setChecked(true);
            }
            else{
                switchReminder.setChecked(false);
            }
            if (bundle.getInt("show_on_map") == 1) {
                switchShowOnMap.setChecked(true);
            }
            else{
                switchShowOnMap.setChecked(false);
            }
        }
        else {
            clearAllFields();
        }
        enableKeyboardUtil();

        addRippleAnimationToCloseBtn();
        addRippleAnimationToDoneBtn();
        addAnimationToEtCategory();

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

    private void enableKeyboardUtil() {
        if (keyboardUtil == null) {
            keyboardUtil = new KeyboardUtil(getActivity(), rootView);
            keyboardUtil.disable();
            keyboardUtil.enable();
        }
    }

    private void addAnimationToEtCategory() {
        final Animation animScaleUp = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.show_up);
        final Animation animScaleDown = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.hide_down);
        final TextView tvCategoryTitle = (TextView) rootView.findViewById(R.id.tvCategoryTitle);
        etCategoryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (etCategoryTitle.getText().toString().equals("")) {
                    if (!isBack) {
                        tvCategoryTitle.startAnimation(animScaleUp);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCategoryTitle.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etCategoryTitle.getText().toString().equals("")) {
                    tvCategoryTitle.setVisibility(View.INVISIBLE);
                    if (!isBack) {
                        tvCategoryTitle.startAnimation(animScaleDown);
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
                int reminder = 0;
                if (switchReminder.isChecked()){
                    reminder = 1;
                }
                else {
                    reminder = 0;
                }
                int showOnMap = 0;
                if (switchShowOnMap.isChecked()){
                    showOnMap = 1;
                }
                else {
                    showOnMap = 0;
                }
                CategoryItem categoryItem = new CategoryItem();
                categoryItem.setItemTitle(etCategoryTitle.getText().toString());
                categoryItem.setItemReminder(reminder);
                categoryItem.setItemShowOnMap(showOnMap);

                if(bundle.getInt("type") ==0) {
                    MainActivity.dbM.addCategory(categoryItem);
                    //clearAllFields();
                    getFragmentManager().popBackStack();
                }
                else{
                        MainActivity.dbM.updSelectedCategory(bundle.getString("title"), categoryItem );

                        ArrayList<NotificationItem> oldCategoryNotificationsDataArrayList = new ArrayList<NotificationItem>();
                        ArrayList<NotificationItem> categoryNotificationsDataArrayList = new ArrayList<NotificationItem>();
                        oldCategoryNotificationsDataArrayList = MainActivity.dbM.getCategoryNotificationsItems(categoryItem.getItemTitle());

                       categoryNotificationsDataArrayList = oldCategoryNotificationsDataArrayList;
                        for (int i = 0; i < categoryNotificationsDataArrayList.size(); i++) {
                            categoryNotificationsDataArrayList.get(i).setItemReminder(categoryItem.getItemReminder());
                            categoryNotificationsDataArrayList.get(i).setItemCategory(categoryItem.getItemTitle());
                        }
                        for (int i = 0; i < oldCategoryNotificationsDataArrayList.size(); i++) {
                            MainActivity.dbM.updSelectedNotification(oldCategoryNotificationsDataArrayList.get(i).getItemTitle(),categoryNotificationsDataArrayList.get(i) );
                        }


                    //clearAllFields();
                    getFragmentManager().popBackStack();
                    getFragmentManager().popBackStack();
                    categoryItemClickListener.onViewCategoryItem(categoryItem);
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

    private void addRippleAnimationToCloseBtn() {
        final RippleView rvClose = (RippleView) rootView.findViewById(R.id.rvBack);
        rvClose.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if ((!etCategoryTitle.getText().toString().equals("")) ||
                        (!switchReminder.isChecked()) ||
                        (!switchShowOnMap.isChecked())) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Are you sure?")
                            .setMessage("You have unsaved changes.\nDo you want to quit without saving?")
                            .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                   // clearAllFields();
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
        etCategoryTitle.getText().clear();
        switchReminder.setChecked(true);
        switchShowOnMap.setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        isBack = true;
        keyboardUtil = null;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etCategoryTitle!=null) {
            outState.putString(CATEGORY_TITLE, etCategoryTitle.getText().toString());
        }
        if (switchReminder!=null) {
            outState.putBoolean(IS_ENABLE_SWITCH_REMINDER, switchReminder.isChecked());
        }
        if (switchShowOnMap!=null) {
            outState.putBoolean(IS_ENABLE_SWITCH_SHOW_ON_MAP, switchShowOnMap.isChecked());
        }

    }
}
