package com.drexel.rephrem.drexelshuttleapp.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drexel.rephrem.drexelshuttleapp.R;

class ImageRowLayoutAdapter extends ArrayAdapter<String> {
    public ImageRowLayoutAdapter(Context context, String[] values) {
        super(context, R.layout.row_layout_2, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(getContext());

        View thisView = inflater.inflate(R.layout.row_layout_2,
                parent, false
        );

        String stop = getItem(position);

        TextView theText = (TextView) thisView.findViewById(R.id.rowLayoutTextView2);

        theText.setText(stop);

        ImageView image = (ImageView) thisView.findViewById(R.id.imageViewID);

        image.setImageResource(R.drawable.dot);

        return thisView;

    }
}