package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.Order;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {

    public int day;
    public int month;
    public OrderCursorAdapter itemsAdapter;
    Context con;
    OrderReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    Order order;
    Date date;
    int selected;
    int tableNumber;
    String[] months;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        con = this ;
        mDbHelper = new OrderReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();
        selected = getIntent().getIntExtra("selected", 1);
        TextView tv = (TextView) findViewById(R.id.report_date);
        months = new String [] {"Januar", "Februar", "Mart","Maj", "Jun", "Jul", "Avgust", "Septembar", "Oktobar", "Novembar", "Decembar"};
        switch (selected){
            case 1:
                day = getIntent().getIntExtra("day", 2);
                month = getIntent().getIntExtra("month", 2);
                tv.setText("Item count for date: " +day+ ". " + months[month-1]);
                c = getOrdersPerDay();
                break;
            case 2:
                tableNumber = getIntent().getIntExtra("tableNumber", 3);
                tv.setText("Item count for table "+tableNumber);
                c = getOrdersPerTable();
                break;
        }



        date = Calendar.getInstance().getTime();
        date.setDate(day);
        date.setMonth(month);


        lvItems = (ListView) findViewById(R.id.list_reports);
        itemsAdapter = new OrderCursorAdapter(con, c, 3);
        lvItems.setAdapter(itemsAdapter);
    }

    public Cursor getOrdersPerDay() {
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
                new String[]{"%"+months[month-1]+" "+day+"%"},                            // The values for the WHERE clause
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.getCount();
        return c;
    }

    public Cursor getOrdersPerTable() {
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
}

