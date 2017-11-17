package com.example.jelle.jellevannoord_pset3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OrderActivity extends AppCompatActivity {

    menuItem lastDeleted;
    SharedPreferences sharedPref;
    ListView orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_order);

        sharedPref = getApplicationContext().getSharedPreferences("ORDERS", Context.MODE_PRIVATE);

        orderList = findViewById(R.id.orderList);
        setListAdapter();
        orderList.setOnItemLongClickListener(new listViewLongItemClick());

        Button sendOrder = findViewById(R.id.sendOrder);
        sendOrder.setOnClickListener(new sendOrderListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setListAdapter() {
        Set<String> order = sharedPref.getStringSet("Order", null);

        ArrayList<menuItem> items = new ArrayList<>();
        if(order != null) {
            for (String s : order) {
                items.add(new menuItem(sharedPref.getInt(s,0),s,true));
            }
        }
        itemListAdapter adapter = new itemListAdapter(this, R.layout.order_list_item, items);
        orderList.setAdapter(adapter);
    }

    private class listViewLongItemClick implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            menuItem item = (menuItem) adapterView.getItemAtPosition(position);
            Set<String> order = sharedPref.getStringSet("Order", null);
            if(order != null) {
                lastDeleted = item;
                order.remove(item.getName());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putStringSet("Order",order);
                editor.remove(item.getName());
                editor.apply();
                setListAdapter();
                Snackbar.make(findViewById(R.id.constraintLayout), "The " + item.getName() + " is removed", Snackbar.LENGTH_LONG).setAction("Undo", new MyUndoListener()).show();
            }
            return true;
        }
    }

    private class MyUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Set<String> order = sharedPref.getStringSet("Order", null);
            SharedPreferences.Editor editor = sharedPref.edit();
            if(order.contains(lastDeleted.getName())) {
                Snackbar.make(findViewById(R.id.constraintLayout), "The " + lastDeleted.getName() + " is already in your order", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                order.add(lastDeleted.getName());
                editor.putStringSet("Order",order);
                editor.putInt(lastDeleted.getName(), lastDeleted.getPrice());
                editor.apply();
                setListAdapter();
                Snackbar.make(findViewById(R.id.constraintLayout), "The " + lastDeleted.getName() + " is added to your order", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private class sendOrderListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Set<String> order = sharedPref.getStringSet("Order", null);
            if(order == null) {
                Snackbar.make(findViewById(R.id.constraintLayout), "Your order is empty", Snackbar.LENGTH_LONG).show();
            } else if(order.size() == 0) {
                Snackbar.make(findViewById(R.id.constraintLayout), "Your order is empty", Snackbar.LENGTH_LONG).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, MainActivity.baseUrl + "/order", null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                int time = response.optInt("preparation_time");
                                TextView timeTV = findViewById(R.id.timeTV);
                                timeTV.setText("Your order will be ready in " + String.valueOf(time) + " minutes");
                                timeTV.setVisibility(View.VISIBLE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.clear();
                                editor.commit();
                                Button sendOrder = findViewById(R.id.sendOrder);
                                sendOrder.setVisibility(View.GONE);
                                orderList.setVisibility(View.GONE);
                                setListAdapter();
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsObjRequest);
            }
        }
    }
}
