package com.example.vanjavidenov.etf2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.Item;

import java.sql.Time;
import java.util.Calendar;

public class TableActivity extends AppCompatActivity {

    FloatingActionButton fab;
    public OrderCursorAdapter orderAdapter;
    public CategoryCursorAdapter categoryAdapter;
    Context con;
    OrderReaderDbHelper oDbHelper;
    MenuReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    public Cursor subcat;
    String categoryName;
    String subCategoryName;
    Item item;
    long categoryId;
    int choose;
    int table;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        con = this;
        oDbHelper = new OrderReaderDbHelper(con);
        db = oDbHelper.getReadableDatabase();
        table = getIntent().getIntExtra("table", 1);
        TextView tn = (TextView) findViewById(R.id.tableNumber);
        tn.setText("table "+String.valueOf(table));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        choose = 1;
        c = itemsPerTable();
        lvItems = (ListView) findViewById(R.id.lvItems);
        orderAdapter = new OrderCursorAdapter(con, c, 3);
        lvItems.setAdapter(orderAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.fragment_menu);

                mDbHelper = new MenuReaderDbHelper(con);
                db = mDbHelper.getReadableDatabase();
                c = dbWork();

                getWindow().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                lvItems = (ListView) findViewById(R.id.lvItems);
                categoryAdapter = new CategoryCursorAdapter(con, c, 3);
                lvItems.setAdapter(categoryAdapter);
                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (choose ==1){
                            categoryId = l;
                            categoryName = getCategory();
                            choose = 2;
                            subcat = dbWorkSub();
                            categoryAdapter.changeCursor(subcat);
                            categoryAdapter.notifyDataSetChanged();}
                        else if(choose ==2){
                            categoryId = l;
                            subCategoryName = getSubcategory();
                            choose = 3;
                            subcat = dbWorkItem();
                            categoryAdapter.changeCursor(subcat);
                            categoryAdapter.notifyDataSetChanged();
                        }
                        else{
                            categoryId = l;
                            item = getItem(categoryId);
                            SQLiteDatabase db = oDbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(OrderContract.OrderEntry.COLUMN_NAME_ITEM, item.getName());
                            values.put(OrderContract.OrderEntry.COLUMN_NAME_TABLE, String.valueOf(table));
                            values.put(OrderContract.OrderEntry.COLUMN_NAME_TIME, String.valueOf(Calendar.getInstance().getTime().getDate()));
                            long newRowId;
                            newRowId = db.insert(
                                    OrderContract.OrderEntry.TABLE_NAME,
                                    null,
                                    values);
                            recreate();
                        }
                        }
                });
            }
        });
    }

    private Cursor itemsPerTable() {
        String[] projection = {
                OrderContract.OrderEntry._ID,
                OrderContract.OrderEntry.COLUMN_NAME_ITEM
        };
        Cursor c = db.query(
                OrderContract.OrderEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
//                null,
                OrderContract.OrderEntry.COLUMN_NAME_TABLE+ " = ?",                                // The columns for the WHERE clause
                new String[] { String.valueOf(table) },
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }

    private Cursor dbWork() {
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

    private Cursor dbWorkSub() {
        String[] projection = {
                MenuContract.SubcategoryEntry._ID,
                MenuContract.SubcategoryEntry.COLUMN_NAME_NAME
        };
        Cursor c = db.query(
                MenuContract.SubcategoryEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                MenuContract.SubcategoryEntry.COLUMN_NAME_CATEGORY+ " = ?",                                // The columns for the WHERE clause
                new String[] { String.valueOf(getCategory()) },
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }

    private String getCategory() {
        String[] projection = {
                MenuContract.CategoryEntry._ID,
                MenuContract.CategoryEntry.COLUMN_NAME_NAME
        };

        Cursor c = db.query(
                MenuContract.CategoryEntry.TABLE_NAME,
                projection,
                MenuContract.CategoryEntry._ID + "=?",
                new String[] { String.valueOf(categoryId) },
                null,
                null,
                null
        );
        if(c.moveToFirst()){
            return c.getString(1);
        }
        if (c !=null && !c.isClosed()){
            c.close();
        }
        return null;
    }
    private String getSubcategory() {
        String[] projection = {
                MenuContract.SubcategoryEntry._ID,
                MenuContract.SubcategoryEntry.COLUMN_NAME_NAME
        };

        Cursor c = db.query(
                MenuContract.SubcategoryEntry.TABLE_NAME,
                projection,
                MenuContract.SubcategoryEntry._ID + "=?",
                new String[] { String.valueOf(categoryId) },
                null,
                null,
                null
        );
        if(c.moveToFirst()){
            return c.getString(1);
        }
        if (c !=null && !c.isClosed()){
            c.close();
        }
        return null;
    }
    private Cursor dbWorkItem() {
        String[] projection = {
                MenuContract.ItemEntry._ID,
                MenuContract.ItemEntry.COLUMN_NAME_NAME,
                MenuContract.ItemEntry.COLUMN_NAME_PRICE,
                MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION
        };
        Cursor c = db.query(
                MenuContract.ItemEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                MenuContract.ItemEntry.COLUMN_NAME_SUBCATEGORY+ " = ?",                                // The columns for the WHERE clause
                new String[] { String.valueOf(subCategoryName) },
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }

    public Item getItem(long id) {
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
                MenuContract.ItemEntry._ID +"=?",                                // The columns for the WHERE clause
                new String[] {String.valueOf(id)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        Item item = new Item(c.getString(1), c.getString(2),c.getString(3));
        return item;
    }
}
