package com.danielburer.goodhere;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Preliminaries and setup.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String[] NETWORK_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private static final int NETWORK_PERMISSIONS_CALLBACK = 99;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Setup app ID and secret for communication with Django
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        editor.putString(getString(R.string.server_api_url), "http://goodhere-backend.wwwmbrmkmi.us-east-1.elasticbeanstalk.com/api/v1/");
        editor.putString(getString(R.string.client_id_key), "bEgw6lU7JwWKsqpR947DXocYVtxer57VIC5WwwDi");
        editor.putString(getString(R.string.client_secret_key), "9zG0IlrEEXIdo0YBp6otvaM8ZJqWQ3gYf4Xc6eg4z2GKPjS9HdSGP0c0xXcQK895L3mKrGmd1L3y7ZPflQiSnEk2dlUxdl63yV9CNaya2kKGp78FYvyFchIFVZOFEs8t");
        editor.putBoolean(getString(R.string.client_authenticated_key), false);
        editor.apply();

        requestPermissions();

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
        }

        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        checkAuth();
    }

    public void checkAuth() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String base_url= sharedPref.getString(getString(R.string.server_api_url), "");
        String query_url = String.format("%sprofiles/", base_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest (Request.Method.GET, query_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject userProfile = response.getJSONArray("results").getJSONObject(0);
                    editor.putBoolean(getString(R.string.client_authenticated_key), false);
                    editor.apply();
                    goMain(SplashActivity.this);
                } catch (JSONException e) {
                    // Not authenticated, proceed
                    goMain(SplashActivity.this);
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
        QueueSingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void goMain(final Activity callingActivity) {

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                callingActivity.startActivity(new Intent(callingActivity, MainTabActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                callingActivity.finish();
            }
        }, 3000);


    }

    // TODO: Make this much more compliant.
    void requestPermissions() {
        ActivityCompat.requestPermissions(this, NETWORK_PERMISSIONS, NETWORK_PERMISSIONS_CALLBACK);
    }
}
