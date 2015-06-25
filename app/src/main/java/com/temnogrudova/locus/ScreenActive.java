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

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenActive extends Fragment {
    public interface onFabClickListener {
        public void onAddNotification(String parent);
    }
    onFabClickListener FabClickListener;

    private int mScrollOffset = 4;
    Activity activity;
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
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.scroll);
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
      //  recyclerView.setOnTouchListener( new ShowHideOnScroll(mFab, R.anim.show_up,R.anim.hide_down));


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        NotificationRecyclerAdapter notificationRecyclerAdapter = new NotificationRecyclerAdapter(createItemList());
        recyclerView.setAdapter(notificationRecyclerAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabClickListener.onAddNotification("active");
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
                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new ScreenMap()).addToBackStack("").commit();
                    return true;
                }
                return false;
            }
        });
    }

    private ArrayList<NotificationItem> createItemList() {
        ArrayList<NotificationItem> recyclerViewItemArrayList = new ArrayList<NotificationItem>();
        int num = 50;
        for (int i = 1; i <= num; i++) {
            NotificationItem notificationItem = new NotificationItem();
            //  cardViewItem.setItemIcon(R.drawable.ic_map_circle);
            notificationItem.setItemTitle("Active"+ " "+ i);
            notificationItem.setItemReminder(1);
            notificationItem.setItemLocation("");
            notificationItem.setItemNote("");
            notificationItem.setItemCategory("");
            recyclerViewItemArrayList.add(notificationItem);
        }
        return recyclerViewItemArrayList;
    }
}
