package com.temnogrudova.locus.scrollingslidingtabtoolbar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.temnogrudova.locus.CardViewItem;
import com.temnogrudova.locus.MainActivity;
import com.temnogrudova.locus.R;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<CardViewItem> mCardViewList;
    View view;
    Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    public RecyclerAdapter(ArrayList<CardViewItem> cardViewItemArrayList) {
        mCardViewList = cardViewItemArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        int itemIcon = mCardViewList.get(position).getIconResId();
        String itemText = mCardViewList.get(position).getItemText();
        String itemSubText= mCardViewList.get(position).getItemSubText();
        holder.setItemText(itemIcon, itemText, itemSubText);
/*
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "CardView " + String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });
*/
        ImageButton ibChangeCardViewItem = (ImageButton) viewHolder.itemView.findViewById(R.id.ibChangeCardViewItem);
        ibChangeCardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Change CardView " + String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCardViewList == null ? 0 : mCardViewList.size();
    }

}
