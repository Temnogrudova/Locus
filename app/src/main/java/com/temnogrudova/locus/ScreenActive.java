package com.temnogrudova.locus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.RecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by 123 on 12.05.2015.
 */
public class ScreenActive extends Fragment {
    private int mScrollOffset = 4;
    private FloatingActionButton mFab;
    public ScreenActive(){}

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
      //  recyclerView.setOnTouchListener( new ShowHideOnScroll(mFab, R.anim.show,R.anim.hide));


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
        recyclerView.setAdapter(recyclerAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "YES!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    private ArrayList<CardViewItem> createItemList() {
        ArrayList<CardViewItem> cardViewItemArrayList = new ArrayList<CardViewItem>();
        int num = 100;
        for (int i = 1; i <= num; i++) {
            CardViewItem cardViewItem = new CardViewItem();
            cardViewItem.setItemIcon(R.drawable.ic_map_marker);
            cardViewItem.setItemText("Active"+ " "+ i);
            cardViewItem.setItemSubText("subActive"+ " "+ i);
            cardViewItemArrayList.add(cardViewItem);
        }
        return cardViewItemArrayList;
    }
}
