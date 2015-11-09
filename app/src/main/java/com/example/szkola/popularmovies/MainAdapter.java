package com.example.szkola.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindDrawable;
import butterknife.ButterKnife;


public class MainAdapter extends CursorAdapter {

    @BindDrawable(R.drawable.default_placeholder)
    Drawable defaultPlaceholder;

    public MainAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = new ImageView(context);
        return v;

    }

    @Override
    public void bindView(View view, Context context, Cursor c) {

        ButterKnife.bind(this, view);
        //String img_path = c.getString(c.getColumnIndex(MoviesColumns.COLUMN_POSTER_PATH));

        ImageView image = (ImageView)view;
        image .setAdjustViewBounds(true);

        String img_path = c.getString(MainActivityFragment.COL_POSTER_PATH);

        Picasso.with(context)
                .load(img_path)
                .placeholder(defaultPlaceholder)
                .into(image);

    }
}
