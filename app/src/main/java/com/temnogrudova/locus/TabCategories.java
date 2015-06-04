/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.temnogrudova.locus;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.RecyclerAdapter;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ObservableRecyclerView;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ObservableScrollView;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ObservableScrollViewCallbacks;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ScrollState;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ScrollUtils;

import java.util.ArrayList;

public class TabCategories extends Fragment {
    private int mScrollOffset = 4;
    private FloatingActionButton mFab;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        final ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        Fragment parentFragment = getParentFragment();
        mFab =(FloatingActionButton) parentFragment.getView().findViewById(R.id.fab);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
        recyclerView.setAdapter(recyclerAdapter);
        /*
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "YES!", Toast.LENGTH_SHORT).show();
            }
        });
        */
        return view;
    }

    private ArrayList<CardViewItem> createItemList() {
        ArrayList<CardViewItem> cardViewItemArrayList = new ArrayList<CardViewItem>();
        int num = 100;
        for (int i = 1; i <= num; i++) {
            CardViewItem cardViewItem = new CardViewItem();
            cardViewItem.setItemIcon(R.drawable.ic_folder);
            cardViewItem.setItemText("Categories"+ " "+ i);
            cardViewItem.setItemSubText("subCategories"+ " "+ i);
            cardViewItemArrayList.add(cardViewItem);
        }
        return cardViewItemArrayList;
    }

}
