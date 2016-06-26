package com.example.vanjavidenov.etf2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.Item;
import com.example.vanjavidenov.etf2.resources.User;

public class ItemActivity extends AppCompatActivity {

    public ItemCursorAdapter categoryAdapter;
    Context con;
    MenuReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    long subcategoryId;
    String subcategoryName;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent myIntent = getIntent(); // gets the previously created intent
        subcategoryId = myIntent.getLongExtra("subcategoryId", 1);
        con = this ;
        mDbHelper = new MenuReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();
        subcategoryName = getSubcategory();

        TextView category_name = (TextView) findViewById(R.id.item_subcategory_name);
        category_name.setText(subcategoryName);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        final Cursor c = dbWork();

        lvItems = (ListView) findViewById(R.id.lvItems);
        categoryAdapter = new ItemCursorAdapter(con, c, 3);
        lvItems.setAdapter(categoryAdapter);

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final long itemId = id;
                final Dialog d = new Dialog(con);

                d.setContentView(R.layout.dialog_edit_item);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<font color='#ffffff'>Edit item</font>"));

                d.setCancelable(true);
                Item item = findItem(id);

                final CharSequence catName = item.getName();
                final CharSequence catPrice = item.getPrice();
                final CharSequence catDesc = item.getDescription();

                final EditText edit = (EditText) d.findViewById(R.id.editName);
                final EditText editp = (EditText) d.findViewById(R.id.editPrice);
                final EditText editpd = (EditText) d.findViewById(R.id.editDescription);
                edit.setText(catName);
                editp.setText(catPrice);
                editpd.setText(catDesc);

                Button bs = (Button) d.findViewById(R.id.buttonSaveCategory);
                bs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText tv = (EditText) d.findViewById(R.id.editName);
                        EditText tve = (EditText) d.findViewById(R.id.editPrice);
                        EditText tved = (EditText) d.findViewById(R.id.editDescription);
                        String un = String.valueOf(tv.getText());
                        String une = String.valueOf(tve.getText());
                        String uned = String.valueOf(tved.getText());
                        String string = String.valueOf(itemId);
                        ContentValues cv = new ContentValues();
                        cv.put(MenuContract.ItemEntry.COLUMN_NAME_NAME, un);
                        cv.put(MenuContract.ItemEntry.COLUMN_NAME_PRICE, une);
                        cv.put(MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION, uned);
                        String whereClause = MenuContract.ItemEntry._ID+ " == ?";
                        String[] whereArgs = new String[] {
                                // in order the ? appear
                                string
                        };
                        db.update(MenuContract.ItemEntry.TABLE_NAME,cv,whereClause,whereArgs);

                        categoryAdapter.changeCursor(dbWork());
                        categoryAdapter.notifyDataSetChanged();
                        d.dismiss();
                    }
                });

                Button bd = (Button) d.findViewById(R.id.buttonDeleteCategory);
                bd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder adb=new AlertDialog.Builder(con);
                        adb.setTitle("Delete?");
                        adb.setMessage("Are you sure you want to delete " + catName);
                        final long catIdToRemove = itemId;
                        adb.setNegativeButton("Cancel", null);
                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String string =String.valueOf(catIdToRemove);
                                db.execSQL("DELETE FROM "+ MenuContract.ItemEntry.TABLE_NAME +" WHERE _id = '" + string + "'");
                                categoryAdapter.changeCursor(dbWork());
                                categoryAdapter.notifyDataSetChanged();
                                d.dismiss();
                            }});
                        adb.show();
                    }
                });
                d.show();
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(con);

                d.setContentView(R.layout.dialog_add_item);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<font color='#ffffff'>Add item</font>"));

                d.setCancelable(true);

                final EditText editName = (EditText) d.findViewById(R.id.editName);
                final EditText editPrice = (EditText) d.findViewById(R.id.editPrice);
                final EditText editDescription = (EditText) d.findViewById(R.id.editDescription);

                Button b = (Button) d.findViewById(R.id.buttonSaveCategory);

                b.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        String name = editName.getText().toString();
                        String price = editPrice.getText().toString();
                        String desc = editDescription.getText().toString();
                        if (name.trim().equals("") || price.trim().equals("") || desc.trim().equals("")) {
                            final Dialog d = new Dialog(con);

                            d.setContentView(R.layout.dialog_error);

                            d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                            d.setTitle(Html.fromHtml("<font color='#ffffff'>Error</font>"));

                            d.setCancelable(true);
                            d.findViewById(R.id.button_error_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    d.dismiss();
                                }
                            });

                            d.show();
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                            lp.copyFrom(d.getWindow().getAttributes());
                            lp.width = 500;
                            lp.height = 500;
                            lp.x=-170;
                            lp.y=100;
                            d.getWindow().setAttributes(lp);
                        } else {

                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(MenuContract.ItemEntry.COLUMN_NAME_NAME, name);
                            values.put(MenuContract.ItemEntry.COLUMN_NAME_PRICE, price);
                            values.put(MenuContract.ItemEntry.COLUMN_NAME_DESCRIPTION, desc);
                            values.put(MenuContract.ItemEntry.COLUMN_NAME_SUBCATEGORY, subcategoryName);
                            long newRowId;
                            newRowId = db.insert(
                                    MenuContract.ItemEntry.TABLE_NAME,
                                    null,
                                    values);
                            categoryAdapter.changeCursor(dbWork());
                            categoryAdapter.notifyDataSetChanged();
                            d.dismiss();
                        }
                    }
                });
                d.show();
            }
        });
    }


    private Cursor dbWork() {
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
                new String[] { String.valueOf(subcategoryName) },
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null
        );
        return c;
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
                new String[] { String.valueOf(subcategoryId) },
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
    public Item findItem(long id) {
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
