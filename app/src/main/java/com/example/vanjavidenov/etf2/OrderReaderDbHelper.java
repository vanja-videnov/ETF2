package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vanjavidenov.etf2.MenuContract.CategoryEntry;
import com.example.vanjavidenov.etf2.MenuContract.ItemEntry;
import com.example.vanjavidenov.etf2.MenuContract.SubcategoryEntry;

/**
 * Created by vanjavidenov on 5/29/16.
 */
public class OrderReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OrderReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ORDERS =
            "CREATE TABLE " + OrderContract.OrderEntry.TABLE_NAME + " (" +
                    OrderContract.OrderEntry._ID + " INTEGER PRIMARY KEY," +
                    OrderContract.OrderEntry.COLUMN_NAME_ITEM + TEXT_TYPE + COMMA_SEP +
                    OrderContract.OrderEntry.COLUMN_NAME_TABLE + TEXT_TYPE + COMMA_SEP +
                    OrderContract.OrderEntry.COLUMN_NAME_QUANTITY + TEXT_TYPE + COMMA_SEP +
                    OrderContract.OrderEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    OrderContract.OrderEntry.COLUMN_NAME_PAYMENT + TEXT_TYPE + COMMA_SEP +
                    OrderContract.OrderEntry.COLUMN_NAME_SUM + TEXT_TYPE +
                    " )";


    private static final String SQL_DELETE_ORDERS =
            "DROP TABLE IF EXISTS " + OrderContract.OrderEntry.TABLE_NAME;

    public OrderReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ORDERS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ORDERS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
