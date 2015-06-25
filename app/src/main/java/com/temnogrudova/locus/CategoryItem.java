package com.temnogrudova.locus;

/**
 * Created by 123 on 23.05.2015.
 */
public class CategoryItem {
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

    public Integer getItemShowOnMap() {
        return itemShowOnMap;
    }

    public void setItemShowOnMap(Integer itemShowOnMap) {
        this.itemShowOnMap = itemShowOnMap;
    }

    private String itemTitle;
    private Integer itemReminder;
    private Integer itemShowOnMap;

    public CategoryItem() {
    }

}
