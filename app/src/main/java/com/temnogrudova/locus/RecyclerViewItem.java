package com.temnogrudova.locus;

import java.util.ArrayList;

/**
 * Created by 123 on 23.05.2015.
 */
public class RecyclerViewItem {
    private String itemText;
    private String itemSubText;

    public RecyclerViewItem() {
    }
    public String getItemText(){
        return this.itemText;
    }
    public  String getItemSubText(){
        return this.itemSubText;
    }

    public void setItemText(String text){
        this.itemText = text;
    }
    public void setItemSubText(String text){
        this.itemSubText = text;
    }
}
