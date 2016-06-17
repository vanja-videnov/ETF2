package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.vanjavidenov.etf2.MenuContract.*;

/**
 * Created by vanjavidenov on 5/29/16.
 */
public class MenuReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MenuReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                    CategoryEntry.COLUMN_NAME_NAME + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_SUBCATEGORIES =
            "CREATE TABLE " + SubcategoryEntry.TABLE_NAME + " (" +
                    SubcategoryEntry._ID + " INTEGER PRIMARY KEY," +
                    SubcategoryEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    SubcategoryEntry.COLUMN_NAME_CATEGORY + " INTEGER" +
                    " )";
    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                    ItemEntry._ID + " INTEGER PRIMARY KEY," +
                    ItemEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    ItemEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    ItemEntry.COLUMN_NAME_PRICE + " INTEGER" + COMMA_SEP +
                    ItemEntry.COLUMN_NAME_SUBCATEGORY + " INTEGER" +
                    " )";

    private static final String SQL_DELETE_CATEGORIES =
            "DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME;
    private static final String SQL_DELETE_SUBCATEGORIES =
            "DROP TABLE IF EXISTS " + SubcategoryEntry.TABLE_NAME;
    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;

    public MenuReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_SUBCATEGORIES);
        db.execSQL(SQL_CREATE_ITEMS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_CATEGORIES);
        db.execSQL(SQL_DELETE_SUBCATEGORIES);
        db.execSQL(SQL_DELETE_ITEMS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
