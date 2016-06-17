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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vanjavidenov.etf2.UserContract.UserEntry;
import com.example.vanjavidenov.etf2.resources.User;

public class ProfileFragment extends Fragment {
    Context con;
    UserReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    public ListView lvItems;
    public static User user;

    FloatingActionButton fab;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ProfileFragment() {
    }
    public static ProfileFragment newInstance(User us) {
        user = us;
        ProfileFragment fragment = new ProfileFragment();
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
        db = mDbHelper.getReadableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialog_edit_user, container, false);
        rootView.clearFocus();
        final EditText un = (EditText) rootView.findViewById(R.id.editUsername);
        un.setText(user.getUsername());
        final EditText pa = (EditText) rootView.findViewById(R.id.editPassword);
        pa.setText(user.getPassword());
        final EditText em = (EditText) rootView.findViewById(R.id.editEmail);
        em.setText(user.getEmail());
        final EditText ph = (EditText) rootView.findViewById(R.id.editPhone);
        ph.setText(user.getPhone());
        RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        rg.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        p.addRule(RelativeLayout.BELOW, R.id.editPhone);

        RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutButtons);
        rl.setLayoutParams(p);
        Button bd = (Button) rootView.findViewById(R.id.buttonDeleteUser);
        bd.setVisibility(View.INVISIBLE);

        Button bs = (Button) rootView.findViewById(R.id.buttonSaveUser);
        bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = un.getText().toString();
                String password = pa.getText().toString();
                String email = em.getText().toString();
                String phone = ph.getText().toString();

                if (username.trim().equals("") || password.trim().equals("") || email.trim().equals("") || phone.trim().equals("")) {
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

                    String string = user.getUsername();
                    ContentValues cv = new ContentValues();
                    cv.put(UserEntry.COLUMN_NAME_USERNAME, username);
                    cv.put(UserEntry.COLUMN_NAME_PASSWORD, password);
                    cv.put(UserEntry.COLUMN_NAME_EMAIL, email);
                    cv.put(UserEntry.COLUMN_NAME_PHONE, phone);
                    String whereClause = UserEntry.COLUMN_NAME_USERNAME + " == ?";
                    String[] whereArgs = new String[]{
                            // in order the ? appear
                            string
                    };
                    db.update(UserEntry.TABLE_NAME, cv, whereClause, whereArgs);
                    rootView.clearFocus();
                    Toast.makeText(getActivity(), "Success!",
                            Toast.LENGTH_LONG).show();
                    rootView.invalidate();

                }
            }
        });


        return rootView;
    }

}
