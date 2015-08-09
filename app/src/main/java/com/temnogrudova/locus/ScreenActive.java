package com.temnogrudova.locus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.temnogrudova.locus.database.dbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenActive extends Fragment {
    public interface onFabClickListener {
        public void onAddNotification(String parent, String sub);
        public void onBackViewNotificationItem(String parent);
    }

    onFabClickListener FabClickListener;

    RecyclerView recyclerView;
    private int mScrollOffset = 4;
    Activity activity;
    dbManager dbM;

    private FloatingActionButton mFab;
    public ScreenActive(){}

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity= activity;

        try {
            FabClickListener = (onFabClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Active");
        View view = inflater.inflate(R.layout.screen_active, container, false);
        dbM = new dbManager(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.scroll);
        mFab = (FloatingActionButton)view.findViewById(R.id.fab);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > mScrollOffset) {
                    if (dy > 0) {
                        mFab.hide(true);
                    } else {
                        mFab.show(true);
                    }
                }
            }
        });

        ArrayList<NotificationItem> activeNotificationArrayList = new ArrayList<NotificationItem>();
        ArrayList<NotificationItem> notificationDataArrayList = new ArrayList<NotificationItem>();
        notificationDataArrayList = dbM.getNotificationItems();
        for(int i = 0; i<notificationDataArrayList.size();i++){
            if (notificationDataArrayList.get(i).getItemCategory()!=null){
                ArrayList<CategoryItem> categoryItemsArrayList = new ArrayList<CategoryItem>();
                categoryItemsArrayList = dbM.getCategoryItems();
                for(CategoryItem categoryItem: categoryItemsArrayList){
                    Integer num = dbM.getCategoryId(categoryItem.getItemTitle());
                    if (num==Integer.parseInt(notificationDataArrayList.get(i).getItemCategory())){
                        notificationDataArrayList.get(i).setItemCategory(categoryItem.getItemTitle());
                    }
                }
            }

            if(notificationDataArrayList.get(i).getItemActive() == 1) {
                activeNotificationArrayList.add(notificationDataArrayList.get(i));
            }
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        NotificationRecyclerAdapter notificationRecyclerAdapter = new NotificationRecyclerAdapter(activeNotificationArrayList, "active");
        recyclerView.setAdapter(notificationRecyclerAdapter);

        /*
        int size =0;
        if (activeNotificationArrayList!=null){
            size = activeNotificationArrayList.size();
        }
        FabClickListener.onChangeBadge(size);
        */
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabClickListener.onAddNotification("active", null);
            }
        });
        return view;
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
                    FabClickListener.onBackViewNotificationItem("map");
                  // getFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenMap()).addToBackStack("").commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbM!=null) {
            dbM.close();
        }
    }
}
