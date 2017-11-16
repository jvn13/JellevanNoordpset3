package com.example.jelle.jellevannoord_pset3;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public RequestQueue queue;
    static public String baseUrl = "https://resto.mprog.nl";
    public String category = "";
    private ListView categoriesList;

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
        StringRequest categoriesRequest = new StringRequest(Request.Method.GET, baseUrl + "/categories",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject test = new JSONObject(response);
                            JSONArray jArray = test.getJSONArray("categories");

                            String[] items = new String[jArray.length()];
                            if (jArray != null) {
                                for (int i=0;i<jArray.length();i++){
                                    items[i] = jArray.getString(i);
                                }
                            }
                            CustomArrayAdapter adapter = new CustomArrayAdapter(MainActivity.this, items);
                            categoriesList.setAdapter(adapter);
                        } catch(JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(categoriesRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private class listViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Object entry = parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, CategoryDisplayActivity.class);
            intent.putExtra("CATEGORY", String.valueOf(entry));
            startActivity(intent);
        }
    }
}