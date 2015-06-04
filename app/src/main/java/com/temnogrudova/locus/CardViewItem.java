package com.temnogrudova.locus;

import java.util.ArrayList;

/**
 * Created by 123 on 23.05.2015.
 */
public class CardViewItem {
    private int itemIcon;
    private String itemText;
    private String itemSubText;

    public CardViewItem() {
    }
    public int getIconResId(){
        return itemIcon;
    }
    public String getItemText(){
        return this.itemText;
    }
    public  String getItemSubText(){
        return this.itemSubText;
    }
    public void setItemIcon(int ResId){
        this.itemIcon = ResId;
    }
    public void setItemText(String text){
        this.itemText = text;
    }
    public void setItemSubText(String text){
        this.itemSubText = text;
    }
}
