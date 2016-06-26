package com.example.vanjavidenov.etf2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.Item;
import com.example.vanjavidenov.etf2.resources.Order;

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
    CharSequence orderItemName;
    CharSequence orderItemSum;
    Order order;
    long categoryId;
    int choose;
    int table;
    Cursor c;
    TextView textViewPayTable;
    TextView textViewTableNumber;
    TextView textViewOrderItemName;
    TextView textViewOrderItemCount;
    EditText edit;
    EditText editp;
    EditText editd;
    int tableOrderCount;
    Dialog d;
    AlertDialog.Builder adb;
    String string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        con = this;
        oDbHelper = new OrderReaderDbHelper(con);
        mDbHelper = new MenuReaderDbHelper(con);
        db = oDbHelper.getReadableDatabase();
        choose = 1;
        table = getIntent().getIntExtra("table", 1);

        c = getTableItems();
        tableOrderCount = c.getCount();
        textViewTableNumber = (TextView) findViewById(R.id.tableNumber);
         textViewPayTable = (TextView) findViewById(R.id.tableNumber1);
         textViewOrderItemName = (TextView) findViewById(R.id.textView3);
         textViewOrderItemCount = (TextView) findViewById(R.id.textView4);
        textViewTableNumber.setText("table " + String.valueOf(table));
        if (tableOrderCount <1 ) {
            textViewPayTable.setVisibility(View.INVISIBLE);
            textViewOrderItemName.setVisibility(View.INVISIBLE);
            textViewOrderItemCount.setVisibility(View.INVISIBLE);
            textViewTableNumber.setBackgroundResource(R.color.colorBackgroundGray);
            db = mDbHelper.getReadableDatabase();
            c = getAllCategories();

            getWindow().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            lvItems = (ListView) findViewById(R.id.lvItems);
            categoryAdapter = new CategoryCursorAdapter(con, c, 3);
            lvItems.setAdapter(categoryAdapter);
            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (choose == 1) {
                        categoryId = l;
                        categoryName = getCategoryName();
                        choose = 2;
                        subcat = getAllSubcategoriesForCategory();
                        categoryAdapter.changeCursor(subcat);
                        categoryAdapter.notifyDataSetChanged();
                    } else if (choose == 2) {
                        categoryId = l;
                        subCategoryName = getSubcategoryName();
                        choose = 3;
                        subcat = getAllItemsForSubcategory();
                        categoryAdapter.changeCursor(subcat);
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        categoryId = l;
                        item = getItem(categoryId);
                        db = oDbHelper.getWritableDatabase();
                        if (checkExistingOrder(item.getName(), String.valueOf(table), db) != null) {
                            Order o = checkExistingOrder(item.getName(), String.valueOf(table), db);
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
                            recreate();

                        } else {
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
                            recreate();
                        }

                    }
                }
            });
        }
        else{
            fab = (FloatingActionButton) findViewById(R.id.fab);

            lvItems = (ListView) findViewById(R.id.lvItems);
            orderAdapter = new OrderCursorAdapter(con, c, 3);
            lvItems.setAdapter(orderAdapter);
            textViewPayTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adb = new AlertDialog.Builder(con);
                    adb.setTitle("PAY?");
                    adb.setMessage("Are you sure you want to pay table " + table);

                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            string = String.valueOf(table);
                            db.execSQL("DELETE FROM " + OrderContract.OrderEntry.TABLE_NAME + " WHERE tableNumber = '" + string + "'");
                            finish();
                        }
                    });
                    adb.show();
                }
            });

            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final long orderItemId = l;
                    d = new Dialog(con);

                    d.setContentView(R.layout.dialog_edit_item);
                    d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                    d.setTitle(Html.fromHtml("<font color='#ffffff'>Edit item</font>"));
                    d.setCancelable(true);
                    order = getOrder(l);
                    item = getItemByName(order.getItem());
                    db = oDbHelper.getReadableDatabase();
                    orderItemName = order.getItem();
                    orderItemSum = order.getSum();

                    edit = (EditText) d.findViewById(R.id.editName);
                    editp = (EditText) d.findViewById(R.id.editPrice);
                    editd = (EditText) d.findViewById(R.id.editDescription);

                    edit.setFocusable(false);
                    editp.setFocusable(false);
                    edit.setText(orderItemName);
                    editp.setText(orderItemSum);
                    editd.setVisibility(View.INVISIBLE);
                    Button bs = (Button) d.findViewById(R.id.buttonSaveCategory);
                    bs.setText("PAY");
                    bs.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            string = String.valueOf(orderItemId);

                            Order o = checkExistingOrder(order.getItem(), order.getTable(), db);
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

                            o = checkExistingOrder(order.getItem(), order.getTable(), db);
                            if (Integer.valueOf(o.getQuantity()) == 0){
                                db.execSQL("DELETE FROM " + OrderContract.OrderEntry.TABLE_NAME + " WHERE _id = '" + string + "'");
                                tableOrderCount -= 1;
                                if (tableOrderCount <1 )
                                    finish();
                            }
                            orderAdapter.changeCursor(getTableItems());
                            orderAdapter.notifyDataSetChanged();
                            d.dismiss();

                        }
                    });

                    Button bd = (Button) d.findViewById(R.id.buttonDeleteCategory);
                    bd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adb = new AlertDialog.Builder(con);
                            adb.setTitle("Delete?");
                            adb.setMessage("Are you sure you want to delete " + orderItemName);
                            adb.setNegativeButton("Cancel", null);
                            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    string = String.valueOf(orderItemId);
                                    Order o = checkExistingOrder(order.getItem(), order.getTable(), db);
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

                                    o = checkExistingOrder(order.getItem(), order.getTable(), db);
                                    if (Integer.valueOf(o.getQuantity()) == 0){
                                        db.execSQL("DELETE FROM " + OrderContract.OrderEntry.TABLE_NAME + " WHERE _id = '" + string + "'");
                                        tableOrderCount -= 1;
                                        if (tableOrderCount < 1 )
                                            finish();
                                    }
                                    orderAdapter.changeCursor(getTableItems());
                                    orderAdapter.notifyDataSetChanged();
                                    d.dismiss();
                                }
                            });
                            adb.show();
                        }
                    });
                    d.show();
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db = mDbHelper.getReadableDatabase();
                    c = getAllCategories();

                    getWindow().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                    lvItems = (ListView) findViewById(R.id.lvItems);
                    categoryAdapter = new CategoryCursorAdapter(con, c, 3);
                    lvItems.setAdapter(categoryAdapter);
                    lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (choose == 1) {
                                categoryId = l;
                                categoryName = getCategoryName();
                                choose = 2;
                                subcat = getAllSubcategoriesForCategory();
                                categoryAdapter.changeCursor(subcat);
                                categoryAdapter.notifyDataSetChanged();
                            } else if (choose == 2) {
                                categoryId = l;
                                subCategoryName = getSubcategoryName();
                                choose = 3;
                                subcat = getAllItemsForSubcategory();
                                categoryAdapter.changeCursor(subcat);
                                categoryAdapter.notifyDataSetChanged();
                            } else {
                                categoryId = l;
                                item = getItem(categoryId);
                                db = oDbHelper.getWritableDatabase();
                                if (checkExistingOrder(item.getName(), String.valueOf(table), db) != null) {
                                    Order o = checkExistingOrder(item.getName(), String.valueOf(table), db);
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
                                    recreate();

                                } else {
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
                                    recreate();
                                }

                            }
                        }
                    });

                }
            });
        }



    }

    private Order checkExistingOrder(String item, String table, SQLiteDatabase db) {
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

    private Cursor getTableItems() {
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

    private Cursor getAllCategories() {
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

    private Cursor getAllSubcategoriesForCategory() {
        String[] projection = {
                MenuContract.SubcategoryEntry._ID,
                MenuContract.SubcategoryEntry.COLUMN_NAME_NAME
        };
        Cursor c = db.query(
                MenuContract.SubcategoryEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                MenuContract.SubcategoryEntry.COLUMN_NAME_CATEGORY + " = ?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(getCategoryName())},
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
    }

    private String getCategoryName() {
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

    private String getSubcategoryName() {
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

    private Cursor getAllItemsForSubcategory() {
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

    public Item getItemByName(String name) {
        String[] projection = {
                MenuContract.ItemEntry._ID,
                MenuContract.ItemEntry.COLUMN_NAME_NAME,
                MenuContract.ItemEntry.COLUMN_NAME_PRICE,
                MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION,
        };

        String sortOrder =
                MenuContract.ItemEntry.COLUMN_NAME_NAME + " DESC";
        db = mDbHelper.getReadableDatabase();
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

    public Order getOrder(long id) {
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
        order = new Order(c.getString(1), c.getString(4), c.getString(6), c.getString(3), c.getString(5), c.getString(2), c.getInt(0));
        return order;
    }
}
