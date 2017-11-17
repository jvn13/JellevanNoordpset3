package com.example.jelle.jellevannoord_pset3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ItemDisplayActivity extends AppCompatActivity {

    private menuItem item;
    private SharedPreferences sharedPref;
    OrderBadge orderBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_display);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        item = (menuItem) intent.getSerializableExtra("MENU_ITEM");

        getSupportActionBar().setTitle(item.getName());

        sharedPref = getApplicationContext().getSharedPreferences("ORDERS", Context.MODE_PRIVATE);

        TextView title = findViewById(R.id.title);
        title.setText(item.getName());

        TextView price = findViewById(R.id.price);
        price.setText("€" + String.valueOf(item.getPrice()));

        EditText description = findViewById(R.id.description);
        description.setText(item.getDescription());

        new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(item.getImage());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FABClickListener());

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
                Intent intent = new Intent(ItemDisplayActivity.this, OrderActivity.class);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class FABClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Set<String> order = sharedPref.getStringSet("Order", null);
            SharedPreferences.Editor editor = sharedPref.edit();
            if(order != null) {
                if(order.contains(item.getName())) {
                    Snackbar.make(view, "This item is already in your order", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    order.add(item.getName());
                    editor.putStringSet("Order",order);
                    editor.putInt(item.getName(), item.getPrice());
                    editor.apply();
                    orderBadge.onOrderSetChanged(getApplicationContext());
                    Snackbar.make(view, "The " + item.getName() + " is added to your order", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            } else {
                order = new HashSet();
                order.add(item.getName());
                editor.putStringSet("Order",order);
                editor.putInt(item.getName(), item.getPrice());
                editor.apply();
                orderBadge.onOrderSetChanged(getApplicationContext());
                Snackbar.make(view, "The " + item.getName() + " is added to your order", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }
}
