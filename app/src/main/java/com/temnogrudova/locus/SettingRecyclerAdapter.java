package com.temnogrudova.locus;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.temnogrudova.locus.database.dbManager;

import java.util.ArrayList;


public class SettingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SettingItem> mDataArrayList;
    View view;
    Context context;
    public interface onSettingItemClickListener {
        public void onClearRemindersItem();
        public void onSoundReminder();
    }
    onSettingItemClickListener settingItemClickListener;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
        try {
            settingItemClickListener = (onSettingItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }

    }

    public SettingRecyclerAdapter(ArrayList<SettingItem> DataArrayList) {
        mDataArrayList = DataArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.setting_pattern, parent, false);
        return SettingItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        SettingItemViewHolder holder = (SettingItemViewHolder) viewHolder;
        String text= mDataArrayList.get(position).text;
        String prompt= mDataArrayList.get(position).prompt;
        holder.setSettingItemText(text,prompt);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0){
                    new AlertDialog.Builder(context)
                            .setTitle("Are you sure?")
                            .setMessage("Do you want to delete all reminders?")
                            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    //clearAllFields();
                                    settingItemClickListener.onClearRemindersItem();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();

                }
                if (position == 1){
                   settingItemClickListener.onSoundReminder();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataArrayList == null ? 0 : mDataArrayList.size();
    }



}
