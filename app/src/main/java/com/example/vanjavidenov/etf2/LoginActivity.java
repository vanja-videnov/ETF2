package com.example.vanjavidenov.etf2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vanjavidenov.etf2.resources.User;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    Context con;
    UserReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    User user;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] ADMIN = new String[]{"admin"};


    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        con = this;
        mDbHelper = new UserReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();

        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Wrong password");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError("required username");
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mUsernameView.setError("invalid username");
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            user = findUser(email,password);
            if (user!=null || (email.equals("admin") && password.equals("admin"))){
                if(email.equals("admin") && password.equals("admin")){
                Intent homepage = new Intent(this, MainActivity.class);
                    finish();
                startActivity(homepage);}
                else{
                    Intent homepage = new Intent(this, WaiterActivity.class);
                    homepage.putExtra("user", user.getUsername());
                    finish();
                    startActivity(homepage);
                }
                }
            else {
                mPasswordView.setError("Wrong password/username");
                focusView = mPasswordView;
                focusView.requestFocus();
            }

//            finish();
        }
    }
    private boolean isEmailValid(String email) {
//        return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public User findUser(String username, String password) {
        String[] projection = {
                UserContract.UserEntry._ID,
                UserContract.UserEntry.COLUMN_NAME_USERNAME,
                UserContract.UserEntry.COLUMN_NAME_PASSWORD,
                UserContract.UserEntry.COLUMN_NAME_EMAIL,
                UserContract.UserEntry.COLUMN_NAME_PHONE,
                UserContract.UserEntry.COLUMN_NAME_ADMIN
        };

        String sortOrder =
                UserContract.UserEntry.COLUMN_NAME_USERNAME + " DESC";
        Cursor c = db.query(
                UserContract.UserEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                UserContract.UserEntry.COLUMN_NAME_USERNAME +"=? AND "+UserContract.UserEntry.COLUMN_NAME_PASSWORD + "=?",                                // The columns for the WHERE clause
                new String[] {username, password},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (c.getCount() != 0) {
            c.moveToFirst();
            User user = new User(c.getString(1), c.getString(2),c.getString(3), c.getString(4), c.getString(5));
            return user;
        }
        else
            return null;
    }


}

