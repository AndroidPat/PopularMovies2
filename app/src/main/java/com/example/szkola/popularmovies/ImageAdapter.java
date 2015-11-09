package com.example.szkola.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ImageAdapter extends BaseAdapter {
    private MainActivityFragment mainActivityFragment;
    private Context mContext;
    private final String[] posterURLArr;


    public ImageAdapter(MainActivityFragment mainActivityFragment, Context c, String[] posterURLArr) {
        this.mainActivityFragment = mainActivityFragment;
        mContext = c;
        this.posterURLArr = posterURLArr;
    }

    @Override
    public int getCount() {
        if (posterURLArr != null) {
            return posterURLArr.length;
        } else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if (posterURLArr != null) {
            return posterURLArr[position];
        } else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }


        if (mainActivityFragment.mImageAdapter != null) {
            Picasso.with(mContext)
                    .load(String.valueOf(mainActivityFragment.mImageAdapter.getItem(position)))
                    .placeholder(R.drawable.default_placeholder)
                    .into(imageView);
        }
        return imageView;

    }
}
