package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.Order;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        con = this ;
        mDbHelper = new OrderReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();
        day = getIntent().getIntExtra("day", 2);
        month = getIntent().getIntExtra("month", 2);
        TextView tv = (TextView) findViewById(R.id.report_date);
        tv.setText(day+ ". " + month+ ".");

        date = Calendar.getInstance().getTime();
        date.setDate(day);
        date.setMonth(month);
        final Cursor c = getOrder();

        lvItems = (ListView) findViewById(R.id.list_reports);
        itemsAdapter = new OrderCursorAdapter(con, c, 3);
        lvItems.setAdapter(itemsAdapter);
    }
    public Cursor getOrder() {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM,
                OrderContract.OrderEntry.COLUMN_NAME_QUANTITY
                //"SUM("+OrderContract.OrderEntry.COLUMN_NAME_QUANTITY+")"
        };

        String sortOrder =
                OrderContract.OrderEntry.COLUMN_NAME_TABLE + " DESC";
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                OrderContract.OrderEntry.COLUMN_NAME_TIME + " like ?",                                // The columns for the WHERE clause
                new String[]{"%"+"Jun 27"+"%"},                            // The values for the WHERE clause
                null,//OrderContract.OrderEntry.COLUMN_NAME_ITEM,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.getCount();
        return c;
    }
}
