package com.example.vanjavidenov.etf2;

import android.provider.BaseColumns;

/**
 * Created by vanjavidenov on 5/29/16.
 */
public final class OrderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public OrderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_NAME_ITEM = "item";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_TABLE = "tableNumber";
        public static final String COLUMN_NAME_PAYMENT = "payment";
        public static final String COLUMN_NAME_SUM = "sum";
    }
}
