package com.example.vanjavidenov.etf2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vanjavidenov.etf2.UserContract.UserEntry;
import com.example.vanjavidenov.etf2.resources.Order;
import com.example.vanjavidenov.etf2.resources.User;

public class OrderFragment extends Fragment {
    Context con;
    UserReaderDbHelper mDbHelper;
    OrderReaderDbHelper oDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    public static User user;

    FloatingActionButton fab;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public OrderFragment() {
    }
    public static OrderFragment newInstance(User us) {
        user = us;
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        con = getContext();
        mDbHelper = new UserReaderDbHelper(con);
        oDbHelper = new OrderReaderDbHelper(con);
        db = oDbHelper.getReadableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lvItems = (ListView) rootView.findViewById(R.id.lvItems);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.tables_array, android.R.layout.simple_list_item_1);
        lvItems.setAdapter(adapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(getActivity(), TableActivity.class);
                intent.putExtra("table", pos+1);
                startActivity(intent);
            }});
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(con);
                adb.setTitle("PAY?");
                final int tableIdToRemove = pos+1;
                adb.setMessage("Are you sure you want to pay table " + tableIdToRemove);

                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String string = String.valueOf(tableIdToRemove);
                        db.execSQL("DELETE FROM " + OrderContract.OrderEntry.TABLE_NAME + " WHERE tableNumber = '" + string + "'");
                    }
                });
                adb.show();
                return true;
            }
        });
        return rootView;
    }
}
