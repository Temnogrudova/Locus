package com.temnogrudova.locus.database;

import android.content.Context;
import android.database.Cursor;

import com.temnogrudova.locus.CategoryItem;
import com.temnogrudova.locus.NotificationItem;

import java.util.ArrayList;

/**
 * Created by Дом on 21.01.2015.
 */
public class dbManager {
    DB db;
    Cursor cursor;
    Context context;
    public dbManager(Context c){
        // подключаемся к БД
        this.context = c;
        db = new DB(this.context);
        db.open();
    }

    public ArrayList<CategoryItem> getCategoryItems(){
        ArrayList<CategoryItem> categories = new ArrayList<CategoryItem>();
        try {
            cursor = db.getCategoryData();
            categories = db.convertMassiveCursorToCategoryItems(cursor, DB.CATEGORY_COLUMN_TITLE, DB.CATEGORY_COLUMN_REMINDER, DB.CATEGORY_COLUMN_SHOW_ON_MAP);
        }
        catch (Exception e){
            categories = null;
        }

        return categories;
    }

    public ArrayList<NotificationItem> getCategoryNotificationsItems(String selCategory){
        ArrayList<NotificationItem> s = new ArrayList<NotificationItem>();
        try {
            cursor = db.getCategoryId(selCategory);
            int id = db.convertCursorToInt(cursor);
            cursor = db.getNotificationData(id);
            s = db.convertMassiveCursorToNotificationItems(cursor, DB.NOTIFICATION_COLUMN_TITLE, DB.NOTIFICATION_COLUMN_REMINDER,
                    DB.NOTIFICATION_COLUMN_LOCATION, DB.NOTIFICATION_COLUMN_NOTE,DB.NOTIFICATION_COLUMN_CATEGORY );
        }
        catch (Exception e){
            s = null;
        }

        return s;
    }

    public Integer getCategoryId(String selCategory){
        int id = 0;
        cursor = db.getCategoryId(selCategory);
        id = db.convertCursorToInt(cursor);
        return id;
    }
public void addCategory(CategoryItem categoryItem){
        db.addCategory(categoryItem.getItemTitle(), categoryItem.getItemReminder(), categoryItem.getItemShowOnMap());
    }

    public void delSelectedCategory(String selectedCategory){
        db.delCategory(selectedCategory);
    }

    public   void updSelectedCategory(String selectedCategory,  CategoryItem categoryItem){
        db.updCategory(selectedCategory, categoryItem.getItemTitle(), categoryItem.getItemReminder(), categoryItem.getItemShowOnMap());
    }

 public void addNotification(NotificationItem notificationItem) {
        db.addNotification(notificationItem.getItemTitle(),
                notificationItem.getItemReminder(),
                notificationItem.getItemLocation(),
                notificationItem.getItemNote(),
                notificationItem.getItemCategory());

    }

    public ArrayList<NotificationItem> getNotificationItems(){
        ArrayList<NotificationItem> notifications = new ArrayList<NotificationItem>();
        try {
            cursor = db.getNotificationData();
            notifications = db.convertMassiveCursorToNotificationItems(cursor, DB.NOTIFICATION_COLUMN_TITLE, DB.NOTIFICATION_COLUMN_REMINDER,
                    DB.NOTIFICATION_COLUMN_LOCATION, DB.NOTIFICATION_COLUMN_NOTE, DB.NOTIFICATION_COLUMN_CATEGORY);
        }
        catch (Exception e){
            notifications = null;
        }

        return notifications;
    }

   public void updSelectedNotification( String selNotification, NotificationItem notificationItem){
      db.updNotification(selNotification, notificationItem.getItemTitle(), notificationItem.getItemReminder(),
              notificationItem.getItemLocation(), notificationItem.getItemNote(), notificationItem.getItemCategory());
    }


    public void delSelectedNotification(String selNotification){
        db.delNotification(selNotification);
    }

    public void close() {
            db.close();
        }
}
