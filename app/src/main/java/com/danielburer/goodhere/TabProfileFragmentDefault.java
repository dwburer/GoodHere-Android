package com.danielburer.goodhere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TabProfileFragmentDefault extends Fragment {

    ProfileFragmentListener profileListener;
    private EditText username, password;
    private Button login;

    public TabProfileFragmentDefault() {

    }

    @SuppressLint("ValidFragment")
    public TabProfileFragmentDefault(PagerAdapter.ProfilePageListener listener) {
        this.profileListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_profile_fragment_default, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = (EditText) getView().findViewById(R.id.et_username);
        password = (EditText) getView().findViewById(R.id.et_password);
        login = (Button) getView().findViewById(R.id.btn_login);


        password.setImeActionLabel("GO", KeyEvent.KEYCODE_ENTER);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                login();
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String base_url = sharedPref.getString(getString(R.string.server_api_url), "");
        final String appID = sharedPref.getString(getString(R.string.client_id_key), "");
        final String appSecret = sharedPref.getString(getString(R.string.client_secret_key), "");
        final String token_url = String.format("%so/token/", base_url);

        JsonObjectRequest tokenRequest = new JsonObjectRequest(Request.Method.POST, token_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("access_token");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.client_saved_token_key), token);
                    editor.putBoolean(getString(R.string.client_authenticated_key), true);
                    editor.apply();

                    profileListener.onSwitchToNextFragment();
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
            public byte[] getBody() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "password");
                params.put("username", username.getText().toString().trim());
                params.put("password", password.getText().toString().trim());

                if (params.size() > 0) {
                    return encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", appID, appSecret);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        QueueSingleton.getInstance(getActivity()).addToRequestQueue(tokenRequest);
    }

    /**
     * This method was private in the com.Android.Volley.Request class. I had to copy it here so as to encode my parameters.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}