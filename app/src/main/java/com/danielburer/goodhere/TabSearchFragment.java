package com.danielburer.goodhere;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class TabSearchFragment extends Fragment {

    static final int PLACE_PICKER_REQUEST = 1;

    ListView estListView;
    ArrayList<Establishment> establishments;
    ArrayAdapter<Establishment> adapter;
    SearchView search;
    Button nearby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_search_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        estListView = (ListView) getView().findViewById(R.id.lv_search);
        establishments = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, establishments);
        estListView.setAdapter(adapter);
        estListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                queueDetailIntent(adapter.getItem(position).getPk());
            }
        });

        nearby = (Button) getView().findViewById(R.id.btn_nearby);
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openPicker();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });

        search = (SearchView) getView().findViewById(R.id.sv_search);
        search.setQueryHint("Search all restaurants");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0)
                    queryEstablishments(query);
                else {
                    establishments.clear();
                    adapter.notifyDataSetChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 0)
                    queryEstablishments(newText);
                else {
                    establishments.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    public void openPicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                getEstablishmentFromPlacesId(place.getId());
            }
        }
    }

    public void queueDetailIntent(int pk) {
        // Intent for the activity to open when user selects the notification
        Intent detailsIntent = new Intent(getActivity(), EstablishmentDetailActivity.class);
        detailsIntent.putExtra("establishmentPK", pk);
        Intent mainIntent = new Intent(getActivity(), MainTabActivity.class);

        // Use TaskStackBuilder to build the back stack and get the PendingIntent
        PendingIntent pendingIntent =
                TaskStackBuilder.create(getActivity())
                        // add all of DetailsActivity's parents to the stack,
                        // followed by DetailsActivity itself
                        .addNextIntentWithParentStack(mainIntent)
                        .addNextIntentWithParentStack(detailsIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setContentIntent(pendingIntent);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void getEstablishmentFromPlacesId(String id) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String base_url= sharedPref.getString(getString(R.string.server_api_url), "");
        String query_url = String.format("%splaces/?place=%s", base_url, id);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, query_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject establishment = response.getJSONArray("results").getJSONObject(0);
                            int pk = establishment.getInt("pk");
                            queueDetailIntent(pk);
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Location not yet in GoodHere database!", Toast.LENGTH_LONG).show();
                        }
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
                String token = sharedPref.getString(getString(R.string.client_saved_token_key), "");
                String auth = "Bearer " + token;
                params.put("Authorization", auth);
                return params;
            }
        };

        // Access the RequestQueue through our QueueSingleton class.
        QueueSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    public void queryEstablishments(String query) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String base_url= sharedPref.getString(getString(R.string.server_api_url), "");
        String query_url = String.format("%ssearch/?query=%s", base_url, query);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, query_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        ArrayList<Establishment> responseEsts = new ArrayList<>();

                        try {
                            JSONArray data = response.getJSONArray("results");
                            for(int i = 0; i < data.length(); i++) {

                                JSONObject establishment = data.getJSONObject(i);
                                String name = establishment.getString("name");
                                int pk = establishment.getInt("pk");

                                responseEsts.add(new Establishment(name, pk));
                            }

                            establishments.clear();
                            establishments.addAll(responseEsts);
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
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String token = sharedPref.getString(getString(R.string.client_saved_token_key), "");
                String auth = "Bearer " + token;
                params.put("Authorization", auth);
                return params;
            }
        };

        // Access the RequestQueue through our QueueSingleton class.
        QueueSingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }
}
