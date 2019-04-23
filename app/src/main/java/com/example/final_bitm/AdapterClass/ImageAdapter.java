package com.example.final_bitm.AdapterClass;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Nizam Uddin Shamrat on 2/13/2018.
 */

public class ImageAdapter extends BaseAdapter {


    // Keep all Images in array
    /*public Integer[] mThumbIds = {

            R.drawable.blue_backround,
            R.drawable.darkblue_backround,
            R.drawable.opening_screen,
            R.drawable.float_button

    };*/
    private Context mContext;
    public ArrayList<String> photoUris = new ArrayList<>();


    // Constructor
   /* public ImageAdapter(Context c){
        mContext = c;
    }*/

    public ImageAdapter(Context mContext, ArrayList<String> photoUris) {
        this.mContext = mContext;
        this.photoUris = photoUris;
    }

    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return photoUris.size();
    }

    @Override
    public Object getItem(int position) {
        return photoUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(500, 500));
        String url="";
        try {
            url=photoUris.get(position);
        }catch (NumberFormatException e){

        }

        Picasso.with(mContext).load(url).into(imageView);
        return imageView;
    }
}
