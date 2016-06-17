package com.example.vanjavidenov.etf2;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class TableActivity extends AppCompatActivity {

    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        int table = getIntent().getIntExtra("table", 1);
        TextView tn = (TextView) findViewById(R.id.tableNumber);
        tn.setText("table "+String.valueOf(table));
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }
}
