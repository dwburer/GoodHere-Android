package com.danielburer.goodhere;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For the establishment detail view, set up a "vote/score" card for each product/
 */
public class ProductListAdapter extends ArrayAdapter<Product> {

    private List<Product> items;
    private int layoutResourceId;
    private Context context;
    private View internalView;

    private final SharedPreferences sharedPref;

    public ProductListAdapter(Context context, int layoutResourceId, List<Product> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        internalView = parent;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, parent, false);

        ProductHolder holder = new ProductHolder();
        holder.product = items.get(position);

        holder.name = (TextView)row.findViewById(R.id.product_name);
        holder.score = (ProgressBar) row.findViewById(R.id.product_score);
        holder.voteUp = (Button)row.findViewById(R.id.btn_vote_up);
        holder.voteDown = (Button)row.findViewById(R.id.btn_vote_down);

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(final ProductHolder holder) {
        holder.name.setText(String.format("%s - %d%%", holder.product.getName(), (int)(holder.product.getPercent() * 100)));
        holder.score.setMax(holder.product.getVotes());
        holder.score.setProgress(holder.product.getScore());
        holder.voteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPref.getBoolean(context.getString(R.string.client_authenticated_key), false))
                    patchVote(holder.product, 1);
                else
                    Toast.makeText(context.getApplicationContext(), "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.voteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPref.getBoolean(context.getString(R.string.client_authenticated_key), false))
                    patchVote(holder.product, 0);
                else
                    Toast.makeText(context.getApplicationContext(), "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Not sure if this method belongs in the adapter class, or if we should just be setting onClick
    // Listeners to some version of this in the EstablishmentDetailActivity - may make notifying
    // changed data easier.
    private void patchVote(final Product product, final int voteType) {

        String base_url= sharedPref.getString(context.getString(R.string.server_api_url), "");
        String query_url = String.format("%svotes/", base_url);

        Map<String, String> params = new HashMap<>();
        params.put("product", Integer.toString(product.getPk()));
        params.put("type", Integer.toString(voteType));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest (Request.Method.POST, query_url, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                ((EstablishmentDetailActivity) context).queryEstablishments();
                Toast.makeText(context.getApplicationContext(), "Voted!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley", error.toString());
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String token = sharedPref.getString(context.getString(R.string.client_saved_token_key), "");
                String auth = "Bearer " + token;
                params.put("Authorization", auth);
                return params;
            }
        };

        // Access the RequestQueue through our QueueSingleton class.
        QueueSingleton.getInstance(context).addToRequestQueue(jsObjRequest);

    }

    private static class ProductHolder {
        Product product;
        TextView name;
        ProgressBar score;
        Button voteUp;
        Button voteDown;
    }
}