package com.danielburer.goodhere;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.database.DataSetObserver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.DefaultSignInResultHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;
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

public class MainActivity extends AppCompatActivity {

    private static final String[] NETWORK_PERMISSIONS = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private static final int NETWORK_PERMISSIONS_CALLBACK = 99;

    private final static String LOG_TAG = Application.class.getSimpleName();

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        requestPermissions();

        expandableListView = (ExpandableListView) findViewById(R.id.elv_establishment);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });

        initializeApplication();
    }

    private void initializeApplication() {

        // Initialize the AWS Mobile Client
//        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
//
//        final IdentityManager identityManager =
//                AWSMobileClient.defaultMobileClient().getIdentityManager();

        /// TODO: This is using AWS MobileHub / OAUTH - will almost definitely be switching to using DRF

//        identityManager.signInOrSignUp(this,
//                new DefaultSignInResultHandler() {
//                    @Override
//                    public void onSuccess(final Activity callingActivity, final IdentityProvider provider) {
//                        if (provider != null) {
//                            Log.d(LOG_TAG, String.format("User sign-in with %s provider succeeded",
//                                    provider.getDisplayName()));
//                            Toast.makeText(callingActivity, String.format("Sign-in with %s succeeded.",
//                                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public boolean onCancel(final Activity callingActivity) {
//                        return true;
//                    }
//                });


        final TextView mTxtDisplay;
        mTxtDisplay = (TextView) findViewById(R.id.testjson);
        String url = "http://10.0.2.2:8000/api/v1/establishments/";

        final ArrayList<String> responseTitles = new ArrayList<>();
        final HashMap<String, List<String>> reponseListDetail = new HashMap<>();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mTxtDisplay.setText("Response: " + response.getString("count"));
                            JSONArray data = response.getJSONArray("results");
                            for(int i = 0; i < data.length(); i++) {

                                JSONObject establishment = data.getJSONObject(i);
                                String title = establishment.getString("name");

                                ArrayList<String> products = new ArrayList<>();
                                JSONArray responseProducts = establishment.getJSONArray("products");
                                for(int j = 0; j < responseProducts.length(); j++) {
                                    products.add(responseProducts.getString(j));
                                }

                                reponseListDetail.put(title, products);
                            }

                            responseTitles.clear();
                            responseTitles.addAll(reponseListDetail.keySet());

                            expandableListTitle.clear();
                            expandableListTitle = responseTitles;
                            expandableListDetail.clear();
                            expandableListDetail = reponseListDetail;
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
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    // TODO: Make this much much much more compliant
    void requestPermissions() {

        String temp = android.Manifest.permission.INTERNET;

        int temporary = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//                        != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
//                        != PackageManager.PERMISSION_GRANTED) {

        ActivityCompat.requestPermissions(this, NETWORK_PERMISSIONS, NETWORK_PERMISSIONS_CALLBACK);

//        }
    }
}
