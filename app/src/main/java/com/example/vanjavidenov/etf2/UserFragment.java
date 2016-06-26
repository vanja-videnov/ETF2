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
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.List;
import com.example.vanjavidenov.etf2.UserContract.*;
import com.example.vanjavidenov.etf2.resources.User;

public class UserFragment extends Fragment {
    public UserCursorAdapter userAdapter;
    Context con;
    UserReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;

    FloatingActionButton fab;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public UserFragment() {
    }
    public static UserFragment newInstance(int sectionNumber) {

        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        con = getContext();
        mDbHelper = new UserReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Cursor c = dbWork();

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);


        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(con);

                d.setContentView(R.layout.dialog_add_user);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<font color='#ffffff'>Add user</font>"));

                d.setCancelable(true);

                final EditText edit_name = (EditText) d.findViewById(R.id.editUsername);
                final EditText edit_pass = (EditText) d.findViewById(R.id.editPassword);
                final EditText edit_email = (EditText) d.findViewById(R.id.editEmail);
                final EditText edit_phone = (EditText) d.findViewById(R.id.editPhone);
                final RadioButton edit_admin = (RadioButton) d.findViewById(R.id.radio_admin);
                final RadioButton edit_waiter = (RadioButton) d.findViewById(R.id.radio_waiter);
                final String selected_admin_radio;
                if (edit_admin.isChecked() ) selected_admin_radio = "admin";
                else selected_admin_radio = "waiter";

                Button b = (Button) d.findViewById(R.id.buttonSaveUser);

                b.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        String username = edit_name.getText().toString();
                        String password = edit_pass.getText().toString();
                        String email = edit_email.getText().toString();
                        String phone = edit_phone.getText().toString();

                        if (username.trim().equals("") || password.trim().equals("") || email.trim().equals("") || phone.trim().equals("")) {
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
                            values.put(UserEntry.COLUMN_NAME_USERNAME, username);
                            values.put(UserEntry.COLUMN_NAME_PASSWORD, password);
                            values.put(UserEntry.COLUMN_NAME_EMAIL, email);
                            values.put(UserEntry.COLUMN_NAME_PHONE, phone);
                            values.put(UserEntry.COLUMN_NAME_ADMIN, selected_admin_radio);

                            long newRowId;
                            newRowId = db.insert(
                                    UserEntry.TABLE_NAME,
                                    null,
                                    values);

                            userAdapter.changeCursor(dbWork());
                            userAdapter.notifyDataSetChanged();
                            d.dismiss();
                        }
                    }
                });
                d.show();
            }
        });

        lvItems = (ListView) rootView.findViewById(R.id.lvItems);
        userAdapter = new UserCursorAdapter(con, c, 3);
        lvItems.setAdapter(userAdapter);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final long userId = id;
                final Dialog d = new Dialog(con);

                d.setContentView(R.layout.dialog_edit_user);

                d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                d.setTitle(Html.fromHtml("<font color='#ffffff'>Edit user</font>"));


                d.setCancelable(true);
                User u = findUser(id);


                final EditText edit_name = (EditText) d.findViewById(R.id.editUsername);
                final EditText edit_pass = (EditText) d.findViewById(R.id.editPassword);
                final EditText edit_email = (EditText) d.findViewById(R.id.editEmail);
                final EditText edit_phone = (EditText) d.findViewById(R.id.editPhone);
                final RadioButton edit_admin = (RadioButton) d.findViewById(R.id.radio_admin);
                final RadioButton edit_waiter = (RadioButton) d.findViewById(R.id.radio_waiter);
                final String selected_admin_radio = u.getAdmin();
                if (selected_admin_radio.equals("admin") ) edit_admin.setChecked(true);
                else edit_waiter.setChecked(true);

                edit_name.setText(u.getUsername());
                edit_pass.setText(u.getPassword());
                edit_email.setText(u.getEmail());
                edit_phone.setText(u.getPhone());

                Button bs = (Button) d.findViewById(R.id.buttonSaveUser);
                bs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edit_name.getText().toString().trim().equals("") || edit_pass.getText().toString().trim().equals("") || edit_email.getText().toString().trim().equals("") || edit_phone.getText().toString().trim().equals("")) {
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
                            String isAdmin;
                            if (edit_admin.isChecked()) isAdmin = "admin";
                            else isAdmin = "waiter";

                            String string = String.valueOf(userId);
                            ContentValues cv = new ContentValues();
                            cv.put(UserEntry.COLUMN_NAME_USERNAME, edit_name.getText().toString());
                            cv.put(UserEntry.COLUMN_NAME_PASSWORD, edit_pass.getText().toString());
                            cv.put(UserEntry.COLUMN_NAME_EMAIL, edit_email.getText().toString());
                            cv.put(UserEntry.COLUMN_NAME_PHONE, edit_phone.getText().toString());
                            cv.put(UserEntry.COLUMN_NAME_ADMIN, isAdmin);
                            String whereClause = UserEntry._ID + " == ?";
                            String[] whereArgs = new String[]{
                                    // in order the ? appear
                                    string
                            };
                            db.update(UserEntry.TABLE_NAME, cv, whereClause, whereArgs);

                            userAdapter.changeCursor(dbWork());
                            userAdapter.notifyDataSetChanged();
                            d.dismiss();
                        }
                    }
                });

                Button bd = (Button) d.findViewById(R.id.buttonDeleteUser);
                bd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder adb=new AlertDialog.Builder(con);
                        adb.setTitle("Delete?");
                        adb.setMessage("Are you sure you want to delete " + edit_name.getText());
                        final long userIdToRemove = userId;
                        adb.setNegativeButton("Cancel", null);
                        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String string =String.valueOf(userIdToRemove);
                                db.execSQL("DELETE FROM "+ UserEntry.TABLE_NAME +" WHERE _id = '" + string + "'");
                                userAdapter.changeCursor(dbWork());
                                userAdapter.notifyDataSetChanged();
                                d.dismiss();
                            }});
                        adb.show();
                    }
                });
                d.show();
                return true;
            }
        });

        return rootView;
    }

    public Cursor dbWork() {
        String[] projection = {
                UserEntry._ID,
                UserEntry.COLUMN_NAME_USERNAME,
                UserEntry.COLUMN_NAME_PASSWORD,
                UserEntry.COLUMN_NAME_EMAIL,
                UserEntry.COLUMN_NAME_PHONE,
                UserEntry.COLUMN_NAME_ADMIN
        };

        String sortOrder =
                UserEntry.COLUMN_NAME_USERNAME + " DESC";
        Cursor c = db.query(
                UserEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    public User findUser(long id) {
        String[] projection = {
                UserEntry._ID,
                UserEntry.COLUMN_NAME_USERNAME,
                UserEntry.COLUMN_NAME_PASSWORD,
                UserEntry.COLUMN_NAME_EMAIL,
                UserEntry.COLUMN_NAME_PHONE,
                UserEntry.COLUMN_NAME_ADMIN
        };

        String sortOrder =
                UserEntry.COLUMN_NAME_USERNAME + " DESC";
        Cursor c = db.query(
                UserEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                UserEntry._ID +"=?",                                // The columns for the WHERE clause
                new String[] {String.valueOf(id)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        User user = new User(c.getString(1), c.getString(2),c.getString(3), c.getString(4), c.getString(5));
        return user;
    }

    public void refreshUserList(Cursor c){
        userAdapter.changeCursor(c);
        userAdapter.notifyDataSetChanged();
    }


}
