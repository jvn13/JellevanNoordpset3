package com.example.jelle.jellevannoord_pset3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class itemListAdapter extends ArrayAdapter<menuItem> {

    private Context mContext;
    int mResource;

    public itemListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<menuItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        if(getItem(position).isOrder()) {
            String name = getItem(position).getName();
            int price = getItem(position).getPrice();

            TextView tvName = convertView.findViewById(R.id.name);
            TextView tvPRice = convertView.findViewById(R.id.price);

            tvName.setText(name);
            tvPRice.setText("€" + String.valueOf(price));

            return convertView;
        } else {
            String name = getItem(position).getName();

            TextView tvName = convertView.findViewById(R.id.label);
            ImageView arrow = convertView.findViewById(R.id.icon);

            tvName.setText(name);
            arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);

            return convertView;
        }
    }
}
