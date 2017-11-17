package com.example.jelle.jellevannoord_pset3;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class OrderBadge {

    private TextView cartBadge;

    public OrderBadge(TextView cartBadge) {
        this.cartBadge = cartBadge;
    }

    public void onOrderSetChanged(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("ORDERS", Context.MODE_PRIVATE);
        Set<String> order = sharedPref.getStringSet("Order", null);
        if(order == null) {
            this.cartBadge.setVisibility(View.GONE);
        } else if(order.size() == 0) {
            this.cartBadge.setVisibility(View.GONE);
        } else {
            this.cartBadge.setVisibility(View.VISIBLE);
            this.cartBadge.setText(String.valueOf(order.size()));
        }
    }
}
