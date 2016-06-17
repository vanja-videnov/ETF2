package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity {

    public int day;
    public int month;
    public CategoryCursorAdapter categoryAdapter;
    Context con;
    MenuReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        con = this ;
        mDbHelper = new MenuReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();
        day = getIntent().getIntExtra("day", 2);
        month = getIntent().getIntExtra("month", 2);
        TextView tv = (TextView) findViewById(R.id.report_date);
        tv.setText(day+ ". " + month+ ".");

        final Cursor c = dbWork();

        lvItems = (ListView) findViewById(R.id.list_reports);
        categoryAdapter = new CategoryCursorAdapter(con, c, 3);
        lvItems.setAdapter(categoryAdapter);
    }
    private Cursor dbWork() {
        String[] projection = {
                MenuContract.ItemEntry._ID,
                MenuContract.ItemEntry.COLUMN_NAME_NAME,
        };
        Cursor c = db.query(
                MenuContract.ItemEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                null,                                // The columns for the WHERE clause
                null,
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }
}
