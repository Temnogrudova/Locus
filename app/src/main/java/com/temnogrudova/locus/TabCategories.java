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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.temnogrudova.locus.database.dbManager;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ObservableRecyclerView;

import java.util.ArrayList;

public class TabCategories extends Fragment {
    dbManager dbM;
    private int mScrollOffset = 4;
    private FloatingActionButton mFab;
    ObservableRecyclerView recyclerView;
    Activity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    public TabCategories() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        dbM = new dbManager(getActivity());
        recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
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
        try {
            updateCategoryRecyclerView();
        }
        catch (NullPointerException e){

        }

        return view;
    }

    private void updateCategoryRecyclerView() {
        ArrayList<CategoryItem> categoryDataArrayList = new ArrayList<CategoryItem>();
        categoryDataArrayList = dbM.getCategoryItems();
        ArrayList<NotificationItem> categoryNotificationsDataArrayList = new ArrayList<NotificationItem>();

        ArrayList<RecyclerViewItem> categoryArrayList = new ArrayList<RecyclerViewItem>();
        for(CategoryItem categoryItem:categoryDataArrayList){
            RecyclerViewItem recyclerViewItem = new RecyclerViewItem();
            recyclerViewItem.setItemText(categoryItem.getItemTitle());
            categoryNotificationsDataArrayList = dbM.getCategoryNotificationsItems(categoryItem.getItemTitle());
            recyclerViewItem.setItemSubText(String.valueOf(categoryNotificationsDataArrayList.size()) +" notifications");
            categoryArrayList.add(recyclerViewItem);
        }

        if (!(categoryDataArrayList==null)) {
            CategoryRecyclerAdapter categoryRecyclerAdapter = new CategoryRecyclerAdapter(categoryArrayList, categoryDataArrayList);
            recyclerView.setAdapter(categoryRecyclerAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbM!=null) {
            dbM.close();
        }
    }
}
