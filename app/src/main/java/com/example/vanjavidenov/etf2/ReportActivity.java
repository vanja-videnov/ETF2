package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.Order;

public class ReportActivity extends AppCompatActivity {

    public int day;
    public int month;
    public OrderCursorAdapter itemsAdapter;
    Context con;
    OrderReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    Order order;
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
                c = Order.getOrdersPerDay(db,months[month-1],day);
                break;
            case 2:
                tableNumber = getIntent().getIntExtra("tableNumber", 3);
                tv.setText("Item count for table "+tableNumber);
                c = Order.getOrdersPerTable(db,tableNumber);
                break;
        }

        lvItems = (ListView) findViewById(R.id.list_reports);
        itemsAdapter = new OrderCursorAdapter(con, c, 3);
        lvItems.setAdapter(itemsAdapter);
    }
}

