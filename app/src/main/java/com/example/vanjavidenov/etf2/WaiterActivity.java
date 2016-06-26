package com.example.vanjavidenov.etf2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.vanjavidenov.etf2.UserContract.UserEntry;
import com.example.vanjavidenov.etf2.resources.User;

import java.util.List;

public class WaiterActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    Context con;
    UserReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    User user;
    String username;

    UserCursorAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        con = this;
        mDbHelper = new UserReaderDbHelper(con);
        db = mDbHelper.getReadableDatabase();
        username = getIntent().getStringExtra("user");
        user = findUser(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment f = null;
            switch (position){
                case 0:
                    f= ProfileFragment.newInstance(user);
                    break;
                case 1:
                    f = OrderFragment.newInstance(user);
                    break;
                default:
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PROFILE";
                case 1:
                    return "ORDERS";
            }
            return null;
        }
    }

    public User findUser(String username) {
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
                UserEntry.COLUMN_NAME_USERNAME +"=?",                                // The columns for the WHERE clause
                new String[] {username},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        User user = new User(c.getString(1), c.getString(2),c.getString(3), c.getString(4), c.getString(5));
        return user;
    }
}
