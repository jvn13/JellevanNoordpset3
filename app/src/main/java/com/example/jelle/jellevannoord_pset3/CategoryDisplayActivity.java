package com.example.jelle.jellevannoord_pset3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.Menu;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDisplayActivity extends AppCompatActivity {

    private ListView itemsList;
    private ArrayList<menuItem> itemsArrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_display);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String category = intent.getStringExtra("CATEGORY");

        RequestQueue queue = Volley.newRequestQueue(this);
        itemsList = findViewById(R.id.itemsList);
        itemsList.setOnItemClickListener(new listViewItemClick());
        /*
        String[] items = {"item 1", "item 2"};
        CustomArrayAdapter adapter = new CustomArrayAdapter(CategoryDisplayActivity.this, items);
        itemsList.setAdapter(adapter);
        */

        HashMap<String, String> params = new HashMap();
        params.put("category", "entrees");

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
                    for (menuItem m: itemsArrayList) { names.add(m.name); }
                    String[] namesArray = new String[names.size()];
                    CustomArrayAdapter adapter = new CustomArrayAdapter(CategoryDisplayActivity.this, names.toArray(namesArray));
                    itemsList.setAdapter(adapter);
                } catch(JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });
        queue.add(jsObjRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private class listViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView textView = findViewById(R.id.textView);
            menuItem selectec = itemsArrayList.get(position);
            /*
            Intent intent = new Intent(CategoryDisplayActivity.this, CategoryDisplayActivity.class);
            intent.putExtra("CATEGORY", String.valueOf(entry));
            startActivity(intent);
            */
        }
    }

    private class menuItem {

        public String category;
        public String description;
        public int price;
        public String image;
        public int id;
        public String name;

        public menuItem(String cat, String desc, int aPrice, String img, int aId, String aName) {
            category = cat;
            description = desc;
            price = aPrice;
            image = img;
            id = aId;
            name = aName;
        }
    }
}
