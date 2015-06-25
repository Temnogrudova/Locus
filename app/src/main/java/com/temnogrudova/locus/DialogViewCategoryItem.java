package com.temnogrudova.locus;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
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

import java.util.ArrayList;

/**
 * Created by 123 on 14.06.2015.
 */
public class DialogViewCategoryItem extends Fragment {

    public interface onCategoryItemEditClickListener {
        public void onEditCategory(CategoryItem categoryItem);
    }
    onCategoryItemEditClickListener categoryItemEditClickListener;

    private Activity activity;
   // private String title = null;
    private Integer isCheckedSwitchReminder = null;
    private Integer isCheckedSwitchShowOnMap = null;
    private View rootView;
    RecyclerView recyclerView;
    CategoryItem categoryItem =null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            categoryItemEditClickListener = (onCategoryItemEditClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_view_category_item, container, false);

        Bundle bundle = this.getArguments();
        categoryItem = new CategoryItem();
        categoryItem.setItemTitle(bundle.getString("title"));
        categoryItem.setItemReminder(bundle.getInt("reminder"));
        categoryItem.setItemShowOnMap(bundle.getInt("show_on_map"));

        setToolbarTitle();
      //  setIsCheckedSwitchReminder(categoryItem.getItemReminder());
      //  setIsCheckedSwitchShowOnMap(categoryItem.getItemShowOnMap());


        setSwitchReminder();
        setSwitchShowOnMap();
        setRecyclerViewCategoryNotifications();

        addUpDownAnimOptions();
        addUpDownAnimNotifications();

        addRippleAnimaBackBtn();
        addRippleAnimDeleteBtn();
        addRippleAnimEditBtn();

        return rootView;
    }

    private void setRecyclerViewCategoryNotifications() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        updateCategoryNotificationsRecyclerView();
    }

    private void updateCategoryNotificationsRecyclerView() {
        ArrayList<NotificationItem> categoryNotificationsDataArrayList = new ArrayList<NotificationItem>();
        categoryNotificationsDataArrayList = MainActivity.dbM.getCategoryNotificationsItems(categoryItem.getItemTitle());

        for(int i = 0; i<categoryNotificationsDataArrayList.size();i++){
            categoryNotificationsDataArrayList.get(i).setItemCategory(categoryItem.getItemTitle());
        }

        if (!(categoryNotificationsDataArrayList==null)) {
            NotificationRecyclerAdapter categoryNotificationsRecyclerAdapter = new NotificationRecyclerAdapter(categoryNotificationsDataArrayList);
            recyclerView.setAdapter(categoryNotificationsRecyclerAdapter);
        }
    }

    private void addRippleAnimaBackBtn() {
        final RippleView rvBack = (RippleView) rootView.findViewById(R.id.rvBack);
        rvBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getFragmentManager().popBackStack();
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


    private void addRippleAnimDeleteBtn() {
        final RippleView rvDelete = (RippleView) rootView.findViewById(R.id.rvDelete);
        rvDelete.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                ArrayList<NotificationItem> categoryNotificationsItems = new ArrayList<NotificationItem>();
                categoryNotificationsItems = MainActivity.dbM.getCategoryNotificationsItems(categoryItem.getItemTitle());
                for(int i =0; i<categoryNotificationsItems.size();i++){
                    categoryNotificationsItems.get(i).setItemCategory(null);
                    MainActivity.dbM.updSelectedNotification(categoryNotificationsItems.get(i).getItemTitle(),categoryNotificationsItems.get(i));
                }
                MainActivity.dbM.delSelectedCategory(categoryItem.getItemTitle());
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

    private void addRippleAnimEditBtn() {
        final RippleView rvEdit = (RippleView) rootView.findViewById(R.id.rvEdit);
        rvEdit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                categoryItemEditClickListener.onEditCategory(categoryItem);
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



/*    public String getTitle() {
        return this.title;
    }
    */
    public Integer getIsCheckedSwitchReminder() {
        return this.isCheckedSwitchReminder;
    }
    public Integer getIsCheckedSwitchShowOnMap() {
        return this.isCheckedSwitchShowOnMap;
    }
   /* public void setTitle(String title) {
        this.title = title;
    }
*/
    public void setIsCheckedSwitchReminder(Integer isCheckedSwitchReminder) {
        this.isCheckedSwitchReminder = isCheckedSwitchReminder;
    }

    public void setIsCheckedSwitchShowOnMap(Integer isCheckedSwitchShowOnMap) {
        this.isCheckedSwitchShowOnMap = isCheckedSwitchShowOnMap;
    }

    public void setSwitchReminder() {
        SwitchCompat switchReminder = (SwitchCompat)rootView.findViewById(R.id.switchReminder);
        setIsCheckedSwitchReminder(categoryItem.getItemReminder());
        if (getIsCheckedSwitchReminder() ==0) {
            switchReminder.setChecked(false);
        }
        if (getIsCheckedSwitchReminder() ==1) {
            switchReminder.setChecked(true);
        }
    }

    public void setSwitchShowOnMap() {
        SwitchCompat switchShowOnMap = (SwitchCompat)rootView.findViewById(R.id.switchShowOnMap);
        setIsCheckedSwitchShowOnMap(categoryItem.getItemShowOnMap());
        if (getIsCheckedSwitchShowOnMap() ==0) {
            switchShowOnMap.setChecked(false);
        }
        if (getIsCheckedSwitchShowOnMap() ==1) {
            switchShowOnMap.setChecked(true);
        }

    }

    private void setToolbarTitle() {
        View l = rootView.findViewById(R.id.dialog_toolbar);
        ((TextView)l.findViewById(R.id.dialog_toolbar_title)).setText(categoryItem.getItemTitle());
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

    private void addUpDownAnimNotifications() {
        final ImageButton ibIsShowNotifications = (ImageButton) rootView.findViewById(R.id.ibShowNotifications);
        final LinearLayout llNotifications = (LinearLayout) rootView.findViewById(R.id.llNotifications);
        ibIsShowNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llNotifications.getVisibility() == View.VISIBLE){
                    llNotifications.setVisibility(View.GONE);
                    ibIsShowNotifications.setImageResource(R.drawable.ic_chevron_up_grey600_24dp);
                }
                else {
                    llNotifications.setVisibility(View.VISIBLE);
                    ibIsShowNotifications.setImageResource(R.drawable.ic_chevron_down_grey600_24dp);
                }
            }
        });
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

}
