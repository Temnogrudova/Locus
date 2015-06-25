package com.temnogrudova.locus;

/**
 * Created by 123 on 23.05.2015.
 */
public class NotificationItem {

    private String itemTitle;
    private Integer itemReminder;
    private String itemLocation;
    private String itemNote;
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

}
