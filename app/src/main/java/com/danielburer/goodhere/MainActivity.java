package com.danielburer.goodhere;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = Application.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {

        // Initialize the AWS Mobile Client
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
                            Toast.makeText(MainActivity.this, String.format("Signed in with %s",
                                    provider.getDisplayName()), Toast.LENGTH_LONG).show();
                        } else if (authResults.isUserAnonymous()) {
                            // User has an unauthenticated anonymous (guest) identity, either because the user never previously
                            // signed in with any identity provider or because refreshing the provider credentials failed.

                            // Optionally, you can check whether refreshing a previously signed in provider failed.
                            final StartupAuthErrorDetails errors = authResults.getErrorDetails();
                            if (errors.didErrorOccurRefreshingProvider()) {
                                Log.w(LOG_TAG, String.format(
                                        "Credentials for Previously signed-in provider %s could not be refreshed.",
                                        "null"));
                            }

                            Log.d(LOG_TAG, "Continuing with unauthenticated (guest) identity.");
                        } else {
                            // User has no identity because authentication was unsuccessful due to a failure.
                            final StartupAuthErrorDetails errors = authResults.getErrorDetails();
                            Log.e(LOG_TAG, "No Identity could be obtained. Continuing with no identity.",
                                    errors.getUnauthenticatedErrorException());
                        }
                    }
                }, 2000);


    }
}
