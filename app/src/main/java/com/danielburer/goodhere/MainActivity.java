package com.danielburer.goodhere;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.DefaultSignInResultHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;

public class MainActivity extends AppCompatActivity {

    private static final String[] NETWORK_PERMISSIONS = new String[]{
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private static final int NETWORK_PERMISSIONS_CALLBACK = 99;

    private final static String LOG_TAG = Application.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        requestPermissions();
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();

        identityManager.signInOrSignUp(this,
                new DefaultSignInResultHandler() {
                    @Override
                    public void onSuccess(final Activity callingActivity, final IdentityProvider provider) {
                        if (provider != null) {
                            Log.d(LOG_TAG, String.format("User sign-in with %s provider succeeded",
                                    provider.getDisplayName()));
                            Toast.makeText(callingActivity, String.format("Sign-in with %s succeeded.",
                                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public boolean onCancel(final Activity callingActivity) {
                        return true;
                    }
                });
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
