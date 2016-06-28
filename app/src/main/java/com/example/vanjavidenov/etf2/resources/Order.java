package com.example.vanjavidenov.etf2.resources;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.SweepGradient;

import com.example.vanjavidenov.etf2.OrderContract;

import java.util.Calendar;

/**
 * Created by vanjavidenov on 6/20/16.
 */
public class Order {
    private int id;
    private String item;
    private String quantity;
    private String time;
    private String table;
    private String payment;
    private String sum;

    public Order(String it, String qu, String ti, String ta, String pa, String su, int i){
        item = it;
        quantity = qu;
        time = ti;
        table = ta;
        payment = pa;
        sum = su;
        id = i;
    }

    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public static  Cursor getTableItems(SQLiteDatabase db, int table) {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,
                OrderContract.OrderEntry.COLUMN_NAME_QUANTITY
        };
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry.COLUMN_NAME_TABLE + " = ?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(table)},
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }

    public static String getTableSum(SQLiteDatabase db, int table) {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                "sum("+OrderContract.OrderEntry.COLUMN_NAME_SUM+") AS "+ OrderContract.OrderEntry.COLUMN_NAME_SUM
        };
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry.COLUMN_NAME_TABLE + " = ?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(table)},
                OrderContract.OrderEntry.COLUMN_NAME_TABLE,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
       if (c.moveToFirst())
           return c.getString(1);
        else
           return "0";
    }

    public static Cursor getOrdersPerTable(SQLiteDatabase db,int tableNumber) {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,
                "sum("+OrderContract.OrderEntry.COLUMN_NAME_QUANTITY+") AS "+OrderContract.OrderEntry.COLUMN_NAME_QUANTITY
        };


        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry.COLUMN_NAME_TABLE+ "=?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(tableNumber)},                            // The values for the WHERE clause
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,                                 // The sort order
                null
        );
        c.getCount();
        return c;
    }

    public static Cursor getOrdersPerDay(SQLiteDatabase db, String month, int day) {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,
                "sum("+OrderContract.OrderEntry.COLUMN_NAME_QUANTITY+") AS "+OrderContract.OrderEntry.COLUMN_NAME_QUANTITY
        };

        String sortOrder =
                OrderContract.OrderEntry.COLUMN_NAME_TABLE + " DESC";
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry.COLUMN_NAME_TIME + " like ?",                                // The columns for the WHERE clause
                new String[]{"%"+month+" "+day+"%"},                            // The values for the WHERE clause
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.getCount();
        return c;
    }

    public static Order getOrder(SQLiteDatabase db,long id) {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,
                OrderContract.OrderEntry.COLUMN_NAME_SUM,
                OrderContract.OrderEntry.COLUMN_NAME_TABLE,
                OrderContract.OrderEntry.COLUMN_NAME_QUANTITY,
                OrderContract.OrderEntry.COLUMN_NAME_PAYMENT,
                OrderContract.OrderEntry.COLUMN_NAME_TIME,
        };

        String sortOrder =
                OrderContract.OrderEntry.COLUMN_NAME_TABLE + " DESC";
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry._ID + "=?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(id)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        Order order = new Order(c.getString(1), c.getString(4), c.getString(6), c.getString(3), c.getString(5), c.getString(2), c.getInt(0));
        return order;
    }

    public static Order checkExistingOrder(String item, String table, SQLiteDatabase db) {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,
                OrderContract.OrderEntry.COLUMN_NAME_SUM,
                OrderContract.OrderEntry.COLUMN_NAME_TABLE,
                OrderContract.OrderEntry.COLUMN_NAME_QUANTITY,
                OrderContract.OrderEntry.COLUMN_NAME_PAYMENT,
                OrderContract.OrderEntry.COLUMN_NAME_TIME,
        };

        String sortOrder =
                OrderContract.OrderEntry.COLUMN_NAME_TABLE + " DESC";
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry.COLUMN_NAME_ITEM + "=? AND " + OrderContract.OrderEntry.COLUMN_NAME_TABLE + "=?",                                // The columns for the WHERE clause
                new String[]{item, table},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (c.moveToFirst()) {
            Order order = new Order(c.getString(1), c.getString(4), c.getString(6), c.getString(3), c.getString(5), c.getString(2), c.getInt(0));
            return order;
        } else return null;
    }
    public static void insertOrder(Item item, int table, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(OrderContract.OrderEntry.COLUMN_NAME_ITEM, item.getName());
        values.put(OrderContract.OrderEntry.COLUMN_NAME_QUANTITY, String.valueOf(1));
        values.put(OrderContract.OrderEntry.COLUMN_NAME_SUM, item.getPrice());
        values.put(OrderContract.OrderEntry.COLUMN_NAME_TABLE, String.valueOf(table));
        values.put(OrderContract.OrderEntry.COLUMN_NAME_TIME, String.valueOf(Calendar.getInstance().getTime()));
        long newRowId;
        newRowId = db.insert(
                OrderContract.OrderEntry.TABLE_NAME,
                null,
                values);
    }

    public static void deleteOrder(SQLiteDatabase db, String string) {
        db.execSQL("DELETE FROM " + OrderContract.OrderEntry.TABLE_NAME + " WHERE _id = '" + string + "'");
    }

    public static void updateOrderPlus(Item item, int table, SQLiteDatabase db){
        Order o = Order.checkExistingOrder(item.getName(), String.valueOf(table), db);
        int qu = Integer.valueOf(o.getQuantity());
        qu += 1;
        int sum = qu * (Integer.valueOf(item.getPrice()));
        ContentValues values = new ContentValues();
        values.put(OrderContract.OrderEntry.COLUMN_NAME_QUANTITY, qu);
        values.put(OrderContract.OrderEntry.COLUMN_NAME_SUM, sum);
        long newRowId;
        newRowId = db.update(
                OrderContract.OrderEntry.TABLE_NAME,
                values,
                "_id=" + o.getId(),
                null);
    }
    public static void updateOrderMinus(Order order, Item item, SQLiteDatabase db){
        Order o = Order.checkExistingOrder(order.getItem(), order.getTable(), db);
        int qu = Integer.valueOf(o.getQuantity());
        qu -= 1;
        int sum = qu * Integer.valueOf(item.getPrice());
        ContentValues values = new ContentValues();
        values.put(OrderContract.OrderEntry.COLUMN_NAME_QUANTITY, qu);
        values.put(OrderContract.OrderEntry.COLUMN_NAME_SUM, sum);
        long newRowId;
        newRowId = db.update(
                OrderContract.OrderEntry.TABLE_NAME,
                values,
                "_id=" + o.getId(),
                null);
    }
    public static void deleteOrderForTable(SQLiteDatabase db, String string){
        db.execSQL("DELETE FROM " + OrderContract.OrderEntry.TABLE_NAME + " WHERE tableNumber = '" + string + "'");
    }
}
