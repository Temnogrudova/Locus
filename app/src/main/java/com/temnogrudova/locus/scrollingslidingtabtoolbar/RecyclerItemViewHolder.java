package com.temnogrudova.locus.scrollingslidingtabtoolbar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.temnogrudova.locus.R;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView itemIcon;
    private final TextView itemTextView;
    private final TextView itemSubTextView;

    public RecyclerItemViewHolder(final View parent, ImageView itemIcon, TextView itemTextView, TextView itemSubTextView) {
        super(parent);
        this.itemIcon = itemIcon;
        this.itemTextView = itemTextView;
        this.itemSubTextView = itemSubTextView;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {

        ImageView itemIcon = (ImageView) parent.findViewById(R.id.item_icon);
        TextView itemTextView = (TextView) parent.findViewById(R.id.item_text);
        TextView itemSubTextView = (TextView) parent.findViewById(R.id.item_subtext);
        return new RecyclerItemViewHolder(parent,itemIcon, itemTextView, itemSubTextView);
    }

    public void setItemText(int iconResId, CharSequence text, CharSequence subText ) {
        itemIcon.setImageResource(iconResId);
        itemTextView.setText(text);
        itemSubTextView.setText(subText);
    }
}

