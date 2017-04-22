package com.danielburer.goodhere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
