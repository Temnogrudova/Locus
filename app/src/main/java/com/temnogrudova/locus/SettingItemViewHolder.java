package com.temnogrudova.locus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SettingItemViewHolder extends RecyclerView.ViewHolder {
    private TextView txtText;
    private TextView txtPrompt;

    public SettingItemViewHolder(final View parent, TextView txtText, TextView txtPrompt) {
        super(parent);
        this.txtText = txtText;
        this.txtPrompt = txtPrompt;
    }

    public static SettingItemViewHolder newInstance(View parent) {
        TextView txtText = (TextView)parent.findViewById(R.id.tvText);
        TextView txtPrompt = (TextView)parent.findViewById(R.id.tvPrompt);
        return new SettingItemViewHolder(parent, txtText, txtPrompt);
    }

    public void setSettingItemText(String text, String prompt) {
        txtText.setText(text);
        txtPrompt.setText(prompt);
    }
}

