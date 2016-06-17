package com.example.vanjavidenov.etf2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.vanjavidenov.etf2.MenuContract.*;

import java.util.zip.Inflater;


public class MenuFragment extends Fragment {
    public CategoryCursorAdapter categoryAdapter;
    Context con;
    MenuReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;

    FloatingActionButton fab;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public MenuFragment() {
    }
    public static MenuFragment newInstance(int sectionNumber) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        con = getContext();
        mDbHelper = new MenuReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Cursor c = dbWork();
        final LayoutInflater inf = inflater;
        final ViewGroup cont = container;
        final View categ;

        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(con);

                d.setContentView(R.layout.dialog_add_category);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<background color='#ffffff'>Add category</font>"));

                d.setCancelable(true);

                final EditText edit = (EditText) d.findViewById(R.id.editName);

                Button b = (Button) d.findViewById(R.id.buttonSaveCategory);

                b.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        String name = edit.getText().toString();
                        if (name.trim().equals("")) {
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
                            SQLiteDatabase db = mDbHelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put(CategoryEntry.COLUMN_NAME_NAME, name);

                            long newRowId;
                            newRowId = db.insert(
                                    CategoryEntry.TABLE_NAME,
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
        lvItems = (ListView) rootView.findViewById(R.id.lvItems);
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
                d.setTitle(Html.fromHtml("<background color='#ffffff'>Edit category</font>"));

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
                        if (un.trim().equals("")) {
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
                            ContentValues cv = new ContentValues();
                            cv.put(CategoryEntry.COLUMN_NAME_NAME, un);
                            String whereClause = CategoryEntry._ID + " == ?";
                            String[] whereArgs = new String[]{
                                    // in order the ? appear
                                    string
                            };
                            db.update(CategoryEntry.TABLE_NAME, cv, whereClause, whereArgs);

                            categoryAdapter.changeCursor(dbWork());
                            categoryAdapter.notifyDataSetChanged();
                            d.dismiss();
                        }
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
                                db.execSQL("DELETE FROM "+ CategoryEntry.TABLE_NAME +" WHERE _id = '" + string + "'");
                                db.execSQL("DELETE FROM "+ SubcategoryEntry.TABLE_NAME +" WHERE category = '" + catName + "'");
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
                Intent intent = new Intent(getActivity(), SubcategoryActivity.class);
                intent.putExtra("categoryId", id);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private Cursor dbWork() {
        String[] projection = {
                CategoryEntry._ID,
                CategoryEntry.COLUMN_NAME_NAME
        };

        String sortOrder =
                CategoryEntry.COLUMN_NAME_NAME + " DESC";
        Cursor c = db.query(
                CategoryEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

}