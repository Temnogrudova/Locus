package com.temnogrudova.locus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 123 on 23.05.2015.
 */
public class NotificationItem implements Parcelable {

    private String itemTitle;
    private Integer itemReminder;
    private String itemLocation;
    private String itemNote;
    private Integer itemActive;
    private String itemCategory;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public Integer getItemReminder() {
        return itemReminder;
    }

    public void setItemReminder(Integer itemReminder) {
        this.itemReminder = itemReminder;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getItemNote() {
        return itemNote;
    }

    public Integer getItemActive() {
        return itemActive;
    }

    public void setItemActive(Integer itemActive) {
        this.itemActive = itemActive;
    }

    public void setItemNote(String itemNote) {
        this.itemNote = itemNote;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public NotificationItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
