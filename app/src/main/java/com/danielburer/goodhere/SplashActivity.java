package com.danielburer.goodhere;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;

/**
 * Get basic permissions here...?
 */
public class SplashActivity extends AppCompatActivity {

    private static final String LOG_TAG = Application.class.getSimpleName();
    private static final String[] NETWORK_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private static final int NETWORK_PERMISSIONS_CALLBACK = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestPermissions();

        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();

        identityManager.doStartupAuth(this,
                new StartupAuthResultHandler() {
                    @Override
                    public void onComplete(final StartupAuthResult authResults) {
                        if (authResults.isUserSignedIn()) {
                            // User has successfully signed in with an identity provider.
                            final IdentityProvider provider = identityManager.getCurrentIdentityProvider();
                            Log.d(LOG_TAG, "Signed in with " + provider.getDisplayName());
                            // If we were signed in previously with a provider indicate that to the user with a toast.
                            Toast.makeText(SplashActivity.this, String.format("Signed in with %s",
                                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
                        } else if (authResults.isUserAnonymous()) {
                            // User has an unauthenticated anonymous (guest) identity, either because the user never previously
                            // signed in with any identity provider or because refreshing the provider credentials failed.

                            // Optionally, you can check whether refreshing a previously signed in provider failed.
                            final StartupAuthErrorDetails errors = authResults.getErrorDetails();
                            if (errors.didErrorOccurRefreshingProvider()) {
                                Log.w(LOG_TAG, String.format(
                                        "Credentials for Previously signed-in providersdfsdfd could not be refreshed."));
                            }

                            Log.d(LOG_TAG, "Continuing with unauthenticated (guest) identity.");
                        } else {
                            // User has no identity because authentication was unsuccessful due to a failure.
                            final StartupAuthErrorDetails errors = authResults.getErrorDetails();
                            Log.e(LOG_TAG, "No Identity could be obtained. Continuing with no identity.",
                                    errors.getUnauthenticatedErrorException());
                        }
                        goMain(SplashActivity.this);
                    }
                }, 2000);

    }

    /** Go to the main activity. */
    private void goMain(final Activity callingActivity) {
        callingActivity.startActivity(new Intent(callingActivity, MainTabActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        callingActivity.finish();
    }

    // TODO: Make this much much much more compliant
    void requestPermissions() {

        String temp = Manifest.permission.INTERNET;

        int temporary = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

            ActivityCompat.requestPermissions(this, NETWORK_PERMISSIONS, NETWORK_PERMISSIONS_CALLBACK);

//        }
    }

}
