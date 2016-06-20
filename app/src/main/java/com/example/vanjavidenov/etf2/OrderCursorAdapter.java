package com.example.vanjavidenov.etf2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by vanjavidenov on 5/29/16.
 */
public class OrderCursorAdapter extends CursorAdapter {
    public OrderCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvBody = (TextView) view.findViewById(R.id.item_name);
        TextView tvBody2 = (TextView) view.findViewById(R.id.item_price);
        // Extract properties from cursor
        String body = cursor.getString(cursor.getColumnIndexOrThrow("item"));
        String body2 = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
        // Populate fields with extracted properties
        tvBody.setText(body);
        tvBody2.setText(body2);
    }

}
