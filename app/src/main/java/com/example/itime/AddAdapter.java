package com.example.itime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AddAdapter extends ArrayAdapter {

    private final int resouseId;

    public AddAdapter(@NonNull Context context, int resource, @NonNull List<Add_item> objects) {
        super(context, resource, objects);
        this.resouseId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Add_item add_item = (Add_item) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resouseId,null);
        ImageView addImage = (ImageView) view.findViewById(R.id.imageView_list_main);
        TextView mainText = (TextView) view.findViewById(R.id.textView_head_list_main);
        TextView subText = (TextView) view.findViewById(R.id.textView_sub_list_main);
        TextView leftText = (TextView) view.findViewById(R.id.textView_left_list_main);
        addImage.setImageResource(add_item.getAdd_image());
        mainText.setText(add_item.getMaintext());
        subText.setText(add_item.getSubtext());
        leftText.setText(add_item.getLefttext());
        return view;
    }
}
