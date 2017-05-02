package com.danielburer.goodhere;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TabProfileFragmentAuthenticated extends Fragment {

    ProfileFragmentListener profileListener;
    TextView userName;
    TextView userEmail;
    ImageView profilePicture;

    public TabProfileFragmentAuthenticated() { }

    @SuppressLint("ValidFragment")
    public TabProfileFragmentAuthenticated(PagerAdapter.ProfilePageListener listener) {
        this.profileListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_profile_fragment_authenticated, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userName = (TextView)getView().findViewById(R.id.tv_name);
        userEmail = (TextView)getView().findViewById(R.id.tv_email);
        profilePicture = (ImageView) getView().findViewById(R.id.iv_profile_picture);

        getProfile();
    }

    public void getProfile() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String base_url= sharedPref.getString(getString(R.string.server_api_url), "");
        String query_url = String.format("%sprofiles/", base_url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest (Request.Method.GET, query_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject userProfile = response.getJSONArray("results").getJSONObject(0);
                    JSONObject user = userProfile.getJSONObject("user");
                    userName.setText(user.getString("username"));
                    userEmail.setText(user.getString("email"));

                    String imageUrl = userProfile.getString("profile_picture");
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
                    profilePicture.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
