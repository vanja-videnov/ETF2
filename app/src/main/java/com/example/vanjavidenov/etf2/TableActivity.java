package com.example.vanjavidenov.etf2;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.example.vanjavidenov.etf2.resources.Menu;
import com.example.vanjavidenov.etf2.resources.Order;

public class TableActivity extends AppCompatActivity {

    FloatingActionButton fab;
    public OrderCursorAdapter orderAdapter;
    public CategoryCursorAdapter categoryAdapter;
    Context con;
    OrderReaderDbHelper oDbHelper;
    MenuReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
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
    TextView textViewTableSum;
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

        c = Order.getTableItems(db, table);
        tableOrderCount = c.getCount();
        textViewTableNumber = (TextView) findViewById(R.id.tableNumber);
        textViewPayTable = (TextView) findViewById(R.id.tableNumber1);
        textViewOrderItemName = (TextView) findViewById(R.id.textView3);
        textViewOrderItemCount = (TextView) findViewById(R.id.textView4);
        textViewTableSum = (TextView) findViewById(R.id.tableSum);
        textViewTableNumber.setText("table " + String.valueOf(table));
        textViewTableSum.setText("SUM: "+Order.getTableSum(db,table));

        lvItems = (ListView) findViewById(R.id.lvItems);
        if (tableOrderCount < 1) {
            textViewPayTable.setVisibility(View.INVISIBLE);
            textViewOrderItemName.setVisibility(View.INVISIBLE);
            textViewOrderItemCount.setVisibility(View.INVISIBLE);
            textViewTableNumber.setBackgroundResource(R.color.colorBackgroundGray);
            db = mDbHelper.getReadableDatabase();
            c = Menu.getAllCategories(db);

            getWindow().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            categoryAdapter = new CategoryCursorAdapter(con, c, 3);
            lvItems.setAdapter(categoryAdapter);
            setListenerForListView();
        } else {
            fab = (FloatingActionButton) findViewById(R.id.fab);
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
                            Order.deleteOrderForTable(db,string);
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
                    order = Order.getOrder(db, l);
                    item = Item.getItemByName(mDbHelper.getReadableDatabase(), order.getItem());
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
                            Order.updateOrderMinus(order, item, db);

                            Order o = Order.checkExistingOrder(order.getItem(), order.getTable(), db);
                            checkQuantity(o);
                            changeCursorForOrderAdapter(Order.getTableItems(db, table), orderAdapter);
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
                                    Order.updateOrderMinus(order, item, db);

                                    Order o = Order.checkExistingOrder(order.getItem(), order.getTable(), db);
                                    checkQuantity(o);
                                    changeCursorForOrderAdapter(Order.getTableItems(db, table), orderAdapter);
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
                    c = Menu.getAllCategories(db);

                    getWindow().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                    categoryAdapter = new CategoryCursorAdapter(con, c, 3);
                    lvItems.setAdapter(categoryAdapter);
                    setListenerForListView();

                }
            });
        }
    }

    public void changeCursorForCategoryAdapter(Cursor c, CategoryCursorAdapter a) {
        a.changeCursor(c);
        a.notifyDataSetChanged();
    }

    public void changeCursorForOrderAdapter(Cursor c, OrderCursorAdapter a) {
        a.changeCursor(c);
        a.notifyDataSetChanged();
    }
    public void checkQuantity(Order o){
        if (Integer.valueOf(o.getQuantity()) == 0) {
            Order.deleteOrder(db, string);
            tableOrderCount -= 1;
            if (tableOrderCount < 1)
                finish();
        }
    }

    public void setListenerForListView(){
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (choose == 1) {
                    categoryId = l;
                    categoryName = Menu.getCategoryName(db, categoryId);
                    choose = 2;
                    changeCursorForCategoryAdapter(Menu.getAllSubcategoriesForCategory(db, categoryId), categoryAdapter);
                } else if (choose == 2) {
                    categoryId = l;
                    subCategoryName = Menu.getSubcategoryName(db, categoryId);
                    choose = 3;
                    changeCursorForCategoryAdapter(Item.getAllItemsForSubcategory(db, subCategoryName), categoryAdapter);
                } else {
                    categoryId = l;
                    item = Item.getItem(db, categoryId);
                    db = oDbHelper.getWritableDatabase();
                    if (Order.checkExistingOrder(item.getName(), String.valueOf(table), db) != null) {
                        Order.updateOrderPlus(item, table, db);
                        recreate();
                    } else {
                        Order.insertOrder(item, table, db);
                        recreate();
                    }

                }
            }
        });
    }
}
