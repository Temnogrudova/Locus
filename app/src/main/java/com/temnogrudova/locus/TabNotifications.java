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

import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.temnogrudova.locus.scrollingslidingtabtoolbar.observablescrollview.ObservableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TabNotifications extends Fragment {
    private int mScrollOffset = 4;
    private FloatingActionButton mFab;
    ObservableRecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

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
        updateNotificationRecyclerView();

        return view;
    }

    public void updateNotificationRecyclerView() {
        ArrayList<NotificationItem> notificationDataArrayList = new ArrayList<NotificationItem>();
        notificationDataArrayList = MainActivity.dbM.getNotificationItems();

        List<Integer> list = new ArrayList<Integer>();
        ArrayList<CategoryItem> categoryItems = MainActivity.dbM.getCategoryItems();
        for (CategoryItem categoryItem:categoryItems){
            int i =MainActivity.dbM.getCategoryId(categoryItem.getItemTitle());
            list.add(i);
        }
        for(int i = 0; i<notificationDataArrayList.size();i++){
            if(notificationDataArrayList.get(i).getItemCategory()!=null) {
                int id = Integer.parseInt(notificationDataArrayList.get(i).getItemCategory());
                for(int y = 0; y<list.size();y++){
                    if (list.get(y) == id){
                        notificationDataArrayList.get(i).setItemCategory(categoryItems.get(y).getItemTitle());
                    }
                }
            }
        }
        if (!(notificationDataArrayList==null)) {
            NotificationRecyclerAdapter notificationRecyclerAdapter = new NotificationRecyclerAdapter(notificationDataArrayList);
            recyclerView.setAdapter(notificationRecyclerAdapter);
        }
    }
}
