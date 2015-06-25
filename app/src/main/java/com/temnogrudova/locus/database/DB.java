package com.temnogrudova.locus.database;

/**
 * Created by Дом on 18.12.2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.temnogrudova.locus.CategoryItem;
import com.temnogrudova.locus.NotificationItem;

import java.util.ArrayList;

public class DB {

    private static final String DB_NAME = "dbLocus";
    private static final int DB_VERSION = 1;

    // имя таблицы плейлистов, поля и запрос создания
    private static final String CATEGORY_TABLE = "category";
    public static final String CATEGORY_COLUMN_ID = "_id";
    public static final String CATEGORY_COLUMN_TITLE = "title";
    public static final String CATEGORY_COLUMN_REMINDER = "reminder";
    public static final String CATEGORY_COLUMN_SHOW_ON_MAP = "show_on_map";
    private static final String CATEGORY_TABLE_CREATE = "create table "
            + CATEGORY_TABLE + "("
            + CATEGORY_COLUMN_ID + " integer primary key, "
            + CATEGORY_COLUMN_TITLE + " text, "
            + CATEGORY_COLUMN_SHOW_ON_MAP + " integer, "
            + CATEGORY_COLUMN_REMINDER + " integer"+ ");";

    // имя таблицы песен, поля и запрос создания
    private static final String NOTIFICATION_TABLE = "notification";
    public static final String NOTIFICATION_COLUMN_ID = "_id";
    public static final String NOTIFICATION_COLUMN_TITLE = "title";
    public static final String NOTIFICATION_COLUMN_REMINDER = "reminder";
    public static final String NOTIFICATION_COLUMN_LOCATION = "location";
    public static final String NOTIFICATION_COLUMN_NOTE = "note";
    public static final String NOTIFICATION_COLUMN_CATEGORY = "category_id";
    private static final String NOTIFICATION_TABLE_CREATE = "create table "
            + NOTIFICATION_TABLE + "("
            + NOTIFICATION_COLUMN_ID + " integer primary key autoincrement, "
            + NOTIFICATION_COLUMN_TITLE + " text, "
            + NOTIFICATION_COLUMN_REMINDER + " integer, "
            + NOTIFICATION_COLUMN_LOCATION + " text, "
            + NOTIFICATION_COLUMN_NOTE + " text, "
            + NOTIFICATION_COLUMN_CATEGORY + " integer" + ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открываем подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрываем подключение
    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    public long addCategory(String title, Integer reminder, Integer showOnMap){
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("reminder", reminder);
        cv.put("show_on_map",showOnMap);
        // вставляем запись и получаем ее ID
        long rowID = mDB.insert(CATEGORY_TABLE, null, cv);
        return rowID;
    }

    // данные по категориям
    public Cursor getCategoryData() {
        return mDB.query(CATEGORY_TABLE, null, null, null, null, null, null);
    }

    public ArrayList<CategoryItem> convertMassiveCursorToCategoryItems(Cursor c, String ColumnName, String ColumnReminder, String ColumnShowOnMap){
        ArrayList<CategoryItem> str = new ArrayList<CategoryItem>();
        str.clear();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    CategoryItem categoryItem = new CategoryItem();
                    for (String cn : c.getColumnNames()) {
                        if  (cn.equals(ColumnName))
                        {
                            categoryItem.setItemTitle(c.getString(c.getColumnIndex(cn)));
                        }
                        if (cn.equals(ColumnReminder))
                        {
                            categoryItem.setItemReminder(c.getInt(c.getColumnIndex(cn)));
                        }
                        if (cn.equals(ColumnShowOnMap))
                        {
                            categoryItem.setItemShowOnMap(c.getInt(c.getColumnIndex(cn)));
                        }
                    }
                    str.add(categoryItem);
                } while (c.moveToNext());
            }
        } else
            Log.d("myLog", "Cursor is null");
            return str;
    }

    //del Category

    public boolean delCategory(String selectedCategory){
        // удаляем по названию
       Cursor c = getCategoryId(selectedCategory);
       int categoryId = convertCursorToInt(c);

     //  mDB.delete(NOTIFICATION_TABLE,  NOTIFICATION_COLUMN_CATEGORY + " = " + categoryId, null);
       return  mDB.delete(CATEGORY_TABLE,  CATEGORY_COLUMN_TITLE + " = '" + selectedCategory + "'", null)>0;
    }

    public Cursor getCategoryId(String categoryName) {
        String table = null;
        String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        table = CATEGORY_TABLE;
        columns = new String[] {"category._id as CategoryID"};
        selection = "category.title = ?";
        selectionArgs = new String[]{categoryName} ;
        return mDB.query(table, columns, selection, selectionArgs, null, null, null);
    }

    public int convertCursorToInt(Cursor c) {
        // удаляем по названию
        String str = "";
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = c.getString(c.getColumnIndex(cn));
                    }
                    Log.d("myLog", str);
                } while (c.moveToNext());
            }
        } else
            Log.d("myLog", "Cursor is null");

        int plid = Integer.parseInt(str);
        return plid;
    }
    //------

    public boolean updCategory(String selectedCategory, String title, Integer reminder, Integer showOnMap) {
        // подготовим значения для обновления
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("reminder", reminder);
        cv.put("show_on_map",showOnMap);
        return mDB.update(CATEGORY_TABLE, cv, CATEGORY_COLUMN_TITLE + " = '" + selectedCategory + "'", null)>0;
    }

    public long addNotification( String title, Integer reminder, String location, String note, String categoryTitle){
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        Integer categoryId = null;
        if (categoryTitle!=null){
            Cursor c = getCategoryId(categoryTitle);
            categoryId = convertCursorToInt(c);
        }

        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("reminder", reminder);
        cv.put("location", location);
        cv.put("note", note);
        cv.put("category_id", categoryId);
        // вставляем запись и получаем ее ID
        long rowID = mDB.insert(NOTIFICATION_TABLE, null, cv);
        return rowID;
    }
    // данные по категориям
    public Cursor getNotificationData() {
        return mDB.query(NOTIFICATION_TABLE, null, null, null, null, null, null);
    }

    public ArrayList<NotificationItem> convertMassiveCursorToNotificationItems(Cursor c, String ColumnName,
                                                                           String ColumnReminder, String ColumnLocation,
                                                                           String ColumnNote,
                                                                           String ColumnCategory){
        ArrayList<NotificationItem> str = new ArrayList<NotificationItem>();
        str.clear();
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    NotificationItem notificationItem = new NotificationItem();
                    for (String cn : c.getColumnNames()) {
                        if  (cn.equals(ColumnName))
                        {
                            notificationItem.setItemTitle(c.getString(c.getColumnIndex(cn)));
                        }
                        if (cn.equals(ColumnReminder))
                        {
                            notificationItem.setItemReminder(c.getInt(c.getColumnIndex(cn)));
                        }
                        if (cn.equals(ColumnLocation))
                        {
                            notificationItem.setItemLocation(c.getString(c.getColumnIndex(cn)));
                        }

                        if (cn.equals(ColumnNote))
                        {
                            notificationItem.setItemNote(c.getString(c.getColumnIndex(cn)));
                        }
                        if (cn.equals(ColumnCategory))
                        {
                            notificationItem.setItemCategory(c.getString(c.getColumnIndex(cn)));
                        }
                    }
                    str.add(notificationItem);
                } while (c.moveToNext());
            }
        } else
            Log.d("myLog", "Cursor is null");
        return str;
    }

    // данные по песням конкретной группы
    public Cursor getNotificationData(long categoryID) {
        return mDB.query(NOTIFICATION_TABLE, null, NOTIFICATION_COLUMN_CATEGORY + " = "
                + categoryID, null, null, null, null);
    }


    public boolean delNotification(String selNotification){
        // удаляем по названию
        return  mDB.delete(NOTIFICATION_TABLE,  NOTIFICATION_COLUMN_TITLE + " = '" + selNotification + "'", null)>0;
    }

    public boolean updNotification(String selNotification,  String title,Integer reminder,String location,String note, String categoryTitle) {
        // подготовим значения для обновления
        Integer categoryId = null;
        if (categoryTitle!=null){
            Cursor c = getCategoryId(categoryTitle);
            categoryId = convertCursorToInt(c);
        }
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("title", title);
        cv.put("reminder", reminder);
        cv.put("location", location);
        cv.put("note", note);
        cv.put("category_id", categoryId);

        return mDB.update(NOTIFICATION_TABLE, cv, NOTIFICATION_COLUMN_TITLE + " = '" + selNotification + "'", null)>0;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // создаем таблицу плейлистов
            db.execSQL(CATEGORY_TABLE_CREATE);
            // создаем таблицу песен
            db.execSQL(NOTIFICATION_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onConfigure(SQLiteDatabase db){
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

}