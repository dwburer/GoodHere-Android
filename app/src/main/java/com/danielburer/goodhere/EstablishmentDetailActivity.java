package com.danielburer.goodhere;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EstablishmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_detail);

        final TextView name = (TextView) findViewById(R.id.tv_estDetailName);

        final ArrayList<String> productNames = new ArrayList<>();
        final ListView products = (ListView) findViewById(R.id.lv_estDetailProducts);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNames);
        products.setAdapter(adapter);

        String url = String.format("http://10.0.2.2:8001/api/v1/establishments/%d/", getIntent().getIntExtra("establishmentPK", 0));
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            name.setText(response.getString("name"));

                            ArrayList<String> newProducts = new ArrayList<>();
                            JSONArray responseProducts = response.getJSONArray("products");
                            for(int j = 0; j < responseProducts.length(); j++) {
                                newProducts.add(responseProducts.getString(j));
                            }

                            String imageUrl = response.getString("brand_image");

                            try {
                                ImageView i = (ImageView)findViewById(R.id.brand_image);
                                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
                                i.setImageBitmap(bitmap);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            productNames.clear();
                            productNames.addAll(newProducts);

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley", error.toString());
                    }
                });

        // Access the RequestQueue through our QueueSingleton class.
        QueueSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
