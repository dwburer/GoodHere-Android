package com.danielburer.goodhere;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.Map;

public class TabSearchFragment extends Fragment {

    // TODO: real names for these
    ExpandableListView expandableListView;
    EstablishmentExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, Integer> expandableListPk;
    HashMap<String, List<String>> expandableListDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_search_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = (ExpandableListView) getView().findViewById(R.id.lv_search);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListPk = new HashMap<>();
        expandableListAdapter = new EstablishmentExpandableListAdapter(getActivity(), expandableListTitle, expandableListPk, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded. PK: " + expandableListPk.get(expandableListTitle.get(groupPosition)),
                        Toast.LENGTH_SHORT).show();

                // Intent for the activity to open when user selects the notification
                Intent detailsIntent = new Intent(getActivity(), EstablishmentDetailActivity.class);
                Log.d("what?", "" + expandableListPk.get(expandableListTitle.get(groupPosition)));
                detailsIntent.putExtra("establishmentPK", expandableListPk.get(expandableListTitle.get(groupPosition)));
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
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });

        getEstablishments();
    }

    public void getEstablishments() {
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String base_url= sharedPref.getString(getString(R.string.server_api_url), "");
        String query_url = String.format("%sestablishments/", base_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, query_url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        ArrayList<String> responseTitles = new ArrayList<>();
                        HashMap<String, Integer> responsePks = new HashMap<>();
                        HashMap<String, List<String>> reponseListDetail = new HashMap<>();

                        try {
                            JSONArray data = response.getJSONArray("results");
                            for(int i = 0; i < data.length(); i++) {

                                JSONObject establishment = data.getJSONObject(i);
                                String title = establishment.getString("name") + establishment.getInt("pk");

                                ArrayList<String> products = new ArrayList<>();
                                JSONArray responseProducts = establishment.getJSONArray("products");
                                for(int j = 0; j < responseProducts.length(); j++) {
                                    products.add(responseProducts.getString(j));
                                }

                                reponseListDetail.put(title, products);
                                responsePks.put(title, establishment.getInt("pk"));
                            }

                            responseTitles.clear();
                            responseTitles.addAll(reponseListDetail.keySet());

                            expandableListTitle.clear();
                            expandableListTitle.addAll(responseTitles);
                            expandableListPk.clear();
                            expandableListPk.putAll(responsePks);
                            expandableListDetail.clear();
                            expandableListDetail.putAll(reponseListDetail);
                            expandableListAdapter.notifyDataSetChanged();

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