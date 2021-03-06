package com.temnogrudova.locus;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryRecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemTextView;
    private final TextView itemSubTextView;


    public CategoryRecyclerItemViewHolder(final View parent, TextView itemTextView, TextView itemSubTextView) {
        super(parent);
        this.itemTextView = itemTextView;
        this.itemSubTextView = itemSubTextView;
    }

    public static CategoryRecyclerItemViewHolder newInstance(View parent) {

        TextView itemTextView = (TextView) parent.findViewById(R.id.item_text);
        TextView itemSubTextView = (TextView) parent.findViewById(R.id.item_subtext);
        return new CategoryRecyclerItemViewHolder(parent, itemTextView, itemSubTextView);
    }

    public void setItemText(CharSequence text, CharSequence subText ) {
        itemTextView.setText(text);
        itemSubTextView.setText(subText);
    }
}

