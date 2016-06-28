package com.example.vanjavidenov.etf2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.vanjavidenov.etf2.UserContract.UserEntry;

public class ReportFragment extends Fragment {
    public UserCursorAdapter userAdapter;
    Context con;
    UserReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    public int selected;
    DatePicker dp;
     Dialog d;

    FloatingActionButton fab;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ReportFragment() {
    }
    public static ReportFragment newInstance(int sectionNumber) {

        ReportFragment fragment = new ReportFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.reports_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                switch (pos){
                    case 0:
                        selected = 1;
                        break;
                    case 1:
                        selected = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button button = (Button) rootView.findViewById(R.id.button_report_show);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selected){
                    case 1:
                        d = new Dialog(con);

                        d.setContentView(R.layout.dialog_pick_date);

                        d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                        d.setTitle(Html.fromHtml("<font color='#ffffff'>Select date</font>"));

                        d.setCancelable(true);
                         dp = (DatePicker) d.getWindow().findViewById(R.id.datePicker);

                        d.findViewById(R.id.button_report_date_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), ReportActivity.class);
                                final int day = dp.getDayOfMonth();
                                final int month = dp.getMonth();
                                intent.putExtra("day", day);
                                intent.putExtra("month", month);
                                intent.putExtra("selected", selected);
                                d.dismiss();
                                startActivity(intent);
                            }
                        });

                        d.show();
                        break;
                    case 2:
                        d = new Dialog(con);

                        d.setContentView(R.layout.fragment_order);
                        GridView gv = (GridView) d.getWindow().findViewById(R.id.lvItems);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.tables_array, R.layout.tables_list_item);
                        gv.setAdapter(adapter);
                        d.getWindow().setBackgroundDrawableResource(R.color.colorBackgroundGray);
                        d.setTitle(Html.fromHtml("<font color='#ffffff'>Select table</font>"));

                        d.setCancelable(true);
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                                Intent intent = new Intent(getActivity(), ReportActivity.class);
                                intent.putExtra("tableNumber", pos+1);
                                intent.putExtra("selected", selected);
                                d.dismiss();
                                startActivity(intent);
                            }});
                        d.show();
                        break;
                }
            }
        });
//
//        lvItems = (ListView) rootView.findViewById(R.id.lvItems);
//        userAdapter = new UserCursorAdapter(con, c, 3);
//        lvItems.setAdapter(userAdapter);

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

    public void refreshUserList(Cursor c){
        userAdapter.changeCursor(c);
        userAdapter.notifyDataSetChanged();
    }


}
