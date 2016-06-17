package com.example.vanjavidenov.etf2;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.vanjavidenov.etf2.R;

/**
 * Created by vanjavidenov on 6/16/16.
 */
public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (pos){
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}