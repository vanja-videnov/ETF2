package com.example.vanjavidenov.etf2.resources;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vanjavidenov.etf2.MenuContract;

/**
 * Created by vanjavidenov on 6/15/16.
 */
public class Item {
    private String name;
    private String price;
    private String description;

    public Item(String un, String pass, String em){
        name = un;
        price = pass;
        description = em;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Item getItem(SQLiteDatabase db, long id) {
        String[] projection = {
                MenuContract.ItemEntry._ID,
                MenuContract.ItemEntry.COLUMN_NAME_NAME,
                MenuContract.ItemEntry.COLUMN_NAME_PRICE,
                MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION,
        };

        String sortOrder =
                MenuContract.ItemEntry.COLUMN_NAME_NAME + " DESC";
        Cursor c = db.query(
                MenuContract.ItemEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MenuContract.ItemEntry._ID + "=?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(id)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        Item item = new Item(c.getString(1), c.getString(2), c.getString(3));
        return item;
    }

    public static Item getItemByName(SQLiteDatabase db,String name) {
        String[] projection = {
                MenuContract.ItemEntry._ID,
                MenuContract.ItemEntry.COLUMN_NAME_NAME,
                MenuContract.ItemEntry.COLUMN_NAME_PRICE,
                MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION,
        };
        String sortOrder =
                MenuContract.ItemEntry.COLUMN_NAME_NAME + " DESC";
        Cursor c = db.query(
                MenuContract.ItemEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                MenuContract.ItemEntry.COLUMN_NAME_NAME + "=?",                                // The columns for the WHERE clause
                new String[]{name},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        Item item = new Item(c.getString(1), c.getString(2), c.getString(3));
        return item;
    }

    public static Cursor getAllItemsForSubcategory(SQLiteDatabase db, String subCategoryName) {
        String[] projection = {
                MenuContract.ItemEntry._ID,
                MenuContract.ItemEntry.COLUMN_NAME_NAME,
                MenuContract.ItemEntry.COLUMN_NAME_PRICE,
                MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION
        };
        Cursor cursor = db.query(
                MenuContract.ItemEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                MenuContract.ItemEntry.COLUMN_NAME_SUBCATEGORY + " = ?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(subCategoryName)},
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return cursor;
    }
}
