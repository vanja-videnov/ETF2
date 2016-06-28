package com.example.vanjavidenov.etf2.resources;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.vanjavidenov.etf2.MenuContract;

/**
 * Created by vanjavidenov on 6/28/16.
 */
public class Menu {
    public static Cursor getAllSubcategoriesForCategory(SQLiteDatabase db, long catId) {
        String[] projection = {
                MenuContract.SubcategoryEntry._ID,
                MenuContract.SubcategoryEntry.COLUMN_NAME_NAME
        };
        Cursor c = db.query(
                MenuContract.SubcategoryEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                MenuContract.SubcategoryEntry.COLUMN_NAME_CATEGORY + " = ?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(getCategoryName(db, catId))},
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }

    public static String getCategoryName(SQLiteDatabase db, long categoryId) {
        String[] projection = {
                MenuContract.CategoryEntry._ID,
                MenuContract.CategoryEntry.COLUMN_NAME_NAME
        };

        Cursor c = db.query(
                MenuContract.CategoryEntry.TABLE_NAME,
                projection,
                MenuContract.CategoryEntry._ID + "=?",
                new String[]{String.valueOf(categoryId)},
                null,
                null,
                null
        );
        if (c.moveToFirst()) {
            return c.getString(1);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return null;
    }

    public static Cursor getAllCategories(SQLiteDatabase db) {
        String[] projection = {
                MenuContract.CategoryEntry._ID,
                MenuContract.CategoryEntry.COLUMN_NAME_NAME
        };

        String sortOrder =
                MenuContract.CategoryEntry.COLUMN_NAME_NAME + " DESC";
        Cursor c = db.query(
                MenuContract.CategoryEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    public static String getSubcategoryName(SQLiteDatabase db, long categoryId) {
        String[] projection = {
                MenuContract.SubcategoryEntry._ID,
                MenuContract.SubcategoryEntry.COLUMN_NAME_NAME
        };

        Cursor c = db.query(
                MenuContract.SubcategoryEntry.TABLE_NAME,
                projection,
                MenuContract.SubcategoryEntry._ID + "=?",
                new String[]{String.valueOf(categoryId)},
                null,
                null,
                null
        );
        if (c.moveToFirst()) {
            return c.getString(1);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return null;
    }
}
