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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SubcategoryActivity extends AppCompatActivity {

    public CategoryCursorAdapter categoryAdapter;
    Context con;
    MenuReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    long categoryId;
    String categoryName;

    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);
        Intent myIntent = getIntent(); // gets the previously created intent
        categoryId = myIntent.getLongExtra("categoryId", 1);
        con = this ;
        mDbHelper = new MenuReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();
        categoryName = getCategory();

        TextView category_name = (TextView) findViewById(R.id.subcategory_category_name);
        category_name.setText(categoryName);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        final Cursor c = dbWork();

        lvItems = (ListView) findViewById(R.id.lvItems);
        categoryAdapter = new CategoryCursorAdapter(con, c, 3);
        lvItems.setAdapter(categoryAdapter);

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final long categoryId = id;
                final Dialog d = new Dialog(con);
                final View item = view;

                d.setContentView(R.layout.dialog_edit_category);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<background color='#ffffff'>Edit subcategory</font>"));

                d.setCancelable(true);
                TextView v = (TextView)view.findViewById(R.id.category_name);
                final CharSequence catName = v.getText();

                final EditText edit = (EditText) d.findViewById(R.id.editCategoryName);
                edit.setText(catName);
                final EditText input = new EditText(con);
                input.setText(catName);

                Button bs = (Button) d.findViewById(R.id.buttonSaveCategory);
                bs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText tv = (EditText) d.findViewById(R.id.editCategoryName);
                        String un = String.valueOf(tv.getText());
                        String string = String.valueOf(categoryId);
                        ContentValues cv = new ContentValues();
                        cv.put(MenuContract.SubcategoryEntry.COLUMN_NAME_NAME, un);
                        String whereClause = MenuContract.SubcategoryEntry._ID+ " == ?";
                        String[] whereArgs = new String[] {
                                // in order the ? appear
                                string
                        };
                        db.update(MenuContract.SubcategoryEntry.TABLE_NAME,cv,whereClause,whereArgs);

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
                        final long catIdToRemove = categoryId;
                        adb.setNegativeButton("Cancel", null);
                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String string =String.valueOf(catIdToRemove);
                                db.execSQL("DELETE FROM "+ MenuContract.SubcategoryEntry.TABLE_NAME +" WHERE _id = '" + string + "'");
                                db.execSQL("DELETE FROM "+ MenuContract.ItemEntry.TABLE_NAME +" WHERE subcategory = '" + catName + "'");

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

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(con, ItemActivity.class);
                intent.putExtra("subcategoryId", id);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(con);

                d.setContentView(R.layout.dialog_add_category);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<background color='#ffffff'>Add subcategory</font>"));

                d.setCancelable(true);

                final EditText edit = (EditText) d.findViewById(R.id.editName);

                Button b = (Button) d.findViewById(R.id.buttonSaveCategory);

                b.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if (edit.getText().toString().trim().equals("")) {
                            final Dialog d = new Dialog(con);

                            d.setContentView(R.layout.dialog_error);

                            d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                            d.setTitle(Html.fromHtml("<background color='#ffffff'>Error</font>"));

                            d.setCancelable(true);
                            d.findViewById(R.id.button_error_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    d.dismiss();
                                }
                            });

                            d.show();

                        } else {

                            String name = edit.getText().toString();

                            SQLiteDatabase db = mDbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(MenuContract.SubcategoryEntry.COLUMN_NAME_NAME, name);
                            values.put(MenuContract.SubcategoryEntry.COLUMN_NAME_CATEGORY, categoryName);
                            long newRowId;
                            newRowId = db.insert(
                                    MenuContract.SubcategoryEntry.TABLE_NAME,
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
                MenuContract.SubcategoryEntry._ID,
                MenuContract.SubcategoryEntry.COLUMN_NAME_NAME
        };
        Cursor c = db.query(
                MenuContract.SubcategoryEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                //null,
                MenuContract.SubcategoryEntry.COLUMN_NAME_CATEGORY+ " = ?",                                // The columns for the WHERE clause
                new String[] { String.valueOf(categoryName) },
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
}
