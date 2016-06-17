package com.example.vanjavidenov.etf2;

import android.provider.BaseColumns;

/**
 * Created by vanjavidenov on 5/29/16.
 */
public final class MenuContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MenuContract() {}

    /* Inner class that defines the table contents */
    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_NAME = "name";
    }
    public static abstract class SubcategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "subcategories";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
    }
    public static abstract class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SUBCATEGORY = "subcategory";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRICE = "price";
    }
}
