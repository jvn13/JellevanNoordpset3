package com.example.jelle.jellevannoord_pset3;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDisplayActivity extends AppCompatActivity {

    private ListView itemsList;
    private ArrayList<menuItem> itemsArrayList = new ArrayList();
    OrderBadge orderBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_display);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String category = intent.getStringExtra("CATEGORY");

        RequestQueue queue = Volley.newRequestQueue(this);
        itemsList = findViewById(R.id.itemsList);
        itemsList.setOnItemClickListener(new listViewItemClick());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.baseUrl + "/menu?category=" + category, null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jArray = response.getJSONArray("items");
                    if (jArray != null) {
                        for (int i=0;i<jArray.length();i++){
                            JSONObject item = jArray.getJSONObject(i);
                            itemsArrayList.add(new menuItem(item.getString("category"),item.getString("description"),
                                    item.getInt("price"),item.getString("image_url"),item.getInt("id"),item.getString("name")));
                        }
                    }
                    ArrayList<String> names = new ArrayList();
                    for (menuItem m: itemsArrayList) { names.add(m.getName()); }
                    String[] namesArray = new String[names.size()];
                    itemListAdapter adapter = new itemListAdapter(getApplicationContext(), R.layout.custom_list_item, itemsArrayList);
                    itemsList.setAdapter(adapter);
                } catch(JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = MenuItemCompat.getActionView(menuItem);
        orderBadge = new OrderBadge((TextView) actionView.findViewById(R.id.cart_badge));
        orderBadge.onOrderSetChanged(getApplicationContext());
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cart:
                Intent intent = new Intent(CategoryDisplayActivity.this, OrderActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private class listViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            menuItem selected = itemsArrayList.get(position);
            Intent intent = new Intent(CategoryDisplayActivity.this, ItemDisplayActivity.class);
            intent.putExtra("MENU_ITEM", selected);
            startActivity(intent);
        }
    }
}
