package com.twb.pokerapp.ui.activity.login;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.AuthStateManager;
import com.twb.pokerapp.databinding.ActivityLoginBinding;
import com.twb.pokerapp.ui.activity.table.list.TableListActivity;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.RegistrationResponse;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.browser.AnyBrowserMatcher;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public final class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String EXTRA_FAILED = "com.twb.pokerapp.auth.failed";
    private static final Class<? extends AppCompatActivity> AUTH_COMPLETED_ACTIVITY = TableListActivity.class;

    private final AtomicReference<String> clientId = new AtomicReference<>();
    private final AtomicReference<AuthorizationRequest> authRequest = new AtomicReference<>();
    private final AtomicReference<CustomTabsIntent> authIntent = new AtomicReference<>();
    private final CountDownLatch authIntentLatch = new CountDownLatch(1);

    @Inject
    public AuthStateManager authStateManager;
    @Inject
    public AuthConfiguration authConfiguration;

    private ActivityLoginBinding binding;
    private AuthorizationService authService;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newSingleThreadExecutor();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Force state reset if configuration changed
        if (authConfiguration.hasConfigurationChanged()) {
            Log.i(TAG, "Configuration change detected, clearing old state");
            authStateManager.replace(new AuthState());
            authConfiguration.acceptConfiguration();
        }

        // 2. Already Authorized Check
        if (authStateManager.getCurrent().isAuthorized()) {
            Log.i(TAG, "User is already authenticated, proceeding to next activity");
            binding.loginButton.setEnabled(true);
            startActivity(new Intent(this, AUTH_COMPLETED_ACTIVITY));
            finish();
            return;
        }

        binding.loginButton.setOnClickListener(this::onLoginClick);
        binding.websiteTextView.setOnClickListener(this::onWebsiteClick);

        if (!authConfiguration.isValid()) {
            displayError(authConfiguration.getConfigurationError(), false);
            return;
        }

        if (getIntent().getBooleanExtra(EXTRA_FAILED, false)) {
            displaySnackbarMessage("Authorization Canceled or Failed");
        }

        executor.submit(this::initializeAppAuth);
    }

    public void onLoginClick(View view) {
        // Prevent double clicks or clicking before initialization is done
        binding.loginButton.setEnabled(false);
        executor.submit(this::doAuth);
    }

    public void onWebsiteClick(View view) {
        var intent = new CustomTabsIntent.Builder().build();
        intent.launchUrl(this, Uri.parse("https://poker-app.dinosaur-emperor.ts.net"));
    }

    @WorkerThread
    private void initializeAppAuth() {
        Log.i(TAG, "Initializing AppAuth");
        recreateAuthorizationService();

        if (authStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
            initializeClient();
            return;
        }

        runOnUiThread(() -> displaySnackbarMessage("Retrieving discovery document..."));

        // Pass the custom connection builder for discovery fetch
        if (authConfiguration.getDiscoveryUri() == null) {
            Log.e(TAG, "Failed to retrieve discovery document");
            return;
        }
        AuthorizationServiceConfiguration.fetchFromUrl(
                authConfiguration.getDiscoveryUri(),
                this::handleConfigurationRetrievalResult,
                authConfiguration.getConnectionBuilder());
    }

    @MainThread
    private void handleConfigurationRetrievalResult(AuthorizationServiceConfiguration config, AuthorizationException ex) {
        if (config == null) {
            Log.e(TAG, "Failed to retrieve discovery document", ex);
            displayError("Failed to connect to auth server", true);
            binding.loginButton.setEnabled(true);
            return;
        }
        authStateManager.replace(new AuthState(config));
        executor.submit(this::initializeClient);
    }

    @WorkerThread
    private void initializeClient() {
        if (authConfiguration.getClientId() != null) {
            clientId.set(authConfiguration.getClientId());
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        // Handle dynamic registration if enabled
        var lastResponse = authStateManager.getCurrent().getLastRegistrationResponse();
        if (lastResponse != null) {
            clientId.set(lastResponse.clientId);
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        var registrationRequest = new RegistrationRequest.Builder(
                authStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                Collections.singletonList(authConfiguration.getRedirectUri()))
                .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
                .build();

        authService.performRegistrationRequest(registrationRequest, this::handleRegistrationResponse);
    }

    @MainThread
    private void handleRegistrationResponse(RegistrationResponse response, AuthorizationException ex) {
        authStateManager.updateAfterRegistration(response, ex);
        if (response == null) {
            displayErrorLater("Registration failed", true);
            return;
        }
        clientId.set(response.clientId);
        initializeAuthRequest();
    }

    @MainThread
    private void initializeAuthRequest() {
        createAuthRequest();
        warmUpBrowser();
    }

    private void createAuthRequest() {
        // ADDED PROMPT("login") to fix the session stickiness issue
        var builder = new AuthorizationRequest.Builder(
                authStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                clientId.get(),
                ResponseTypeValues.CODE,
                authConfiguration.getRedirectUri())
                .setScope(authConfiguration.getScope())
                .setPrompt("login");

        authRequest.set(builder.build());
    }

    private void warmUpBrowser() {
        executor.execute(() -> {
            Log.i(TAG, "Warming up browser");
            var intentBuilder = authService.createCustomTabsIntentBuilder(authRequest.get().toUri());
            intentBuilder.setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .build());
            authIntent.set(intentBuilder.build());
            authIntentLatch.countDown();
            runOnUiThread(() -> binding.loginButton.setEnabled(true));
        });
    }

    @WorkerThread
    private void doAuth() {
        try {
            authIntentLatch.await(); // Ensure warmUpBrowser finished
        } catch (InterruptedException ex) {
            Log.w(TAG, "Interrupted waiting for auth intent");
        }

        var completionIntent = new Intent(this, AUTH_COMPLETED_ACTIVITY);
        var cancelIntent = new Intent(this, LoginActivity.class);
        cancelIntent.putExtra(EXTRA_FAILED, true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        var flags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? PendingIntent.FLAG_MUTABLE : 0;

        var completedIntent = PendingIntent.getActivity(this, 0, completionIntent, flags);
        var canceledIntent = PendingIntent.getActivity(this, 0, cancelIntent, flags);

        authService.performAuthorizationRequest(
                authRequest.get(),
                completedIntent,
                canceledIntent,
                authIntent.get());
    }

    private void recreateAuthorizationService() {
        if (authService != null) {
            authService.dispose();
        }
        var builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(AnyBrowserMatcher.INSTANCE);
        builder.setConnectionBuilder(authConfiguration.getConnectionBuilder());
        builder.setSkipIssuerHttpsCheck(!authConfiguration.isHttpsRequired());
        authService = new AuthorizationService(this, builder.build());
    }

    // Standard cleanup and display logic below
    @Override
    protected void onDestroy() {
        if (authService != null) authService.dispose();
        executor.shutdownNow();
        super.onDestroy();
    }

    @MainThread
    private void displaySnackbarMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @MainThread
    private void displayError(String message, boolean recoverable) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    @SuppressWarnings("WrongThread")
    private void displayErrorLater(final String message, final boolean recoverable) {
        runOnUiThread(() -> displayError(message, recoverable));
    }
}
