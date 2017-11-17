package com.example.jelle.jellevannoord_pset3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public RequestQueue queue;
    static public String baseUrl = "https://resto.mprog.nl";
    private ListView categoriesList;
    OrderBadge orderBadge;
    Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        queue = Volley.newRequestQueue(this);
        categoriesList = findViewById(R.id.categoriesList);
        categoriesList.setOnItemClickListener(new listViewItemClick());

        // Request a string response from the provided URL.
        JsonObjectRequest categoriesRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.baseUrl + "/categories", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jArray = response.getJSONArray("categories");
                            ArrayList<menuItem> itemList = new ArrayList<>();
                            if (jArray != null) {
                                for (int i=0;i<jArray.length();i++){
                                    itemList.add(new menuItem(jArray.getString(i)));
                                }
                            }
                            itemListAdapter adapter = new itemListAdapter(getApplicationContext(), R.layout.custom_list_item, itemList);
                            categoriesList.setAdapter(adapter);
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
        // Add the request to the RequestQueue.
        queue.add(categoriesRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
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
            case R.id.action_cart:
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class listViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            menuItem entry = (menuItem) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, CategoryDisplayActivity.class);
            intent.putExtra("CATEGORY", entry.getName());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}