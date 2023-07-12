/*
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twb.pokergame.ui.activity.login;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.snackbar.Snackbar;
import com.twb.pokergame.R;
import com.twb.pokergame.data.auth.AuthConfiguration;
import com.twb.pokergame.data.auth.AuthStateManager;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Demonstrates the usage of the AppAuth to authorize a user with an OAuth2 / OpenID Connect
 * provider. Based on the configuration provided in `res/raw/auth_config.json`, the code
 * contained here will:
 * <p>
 * - Retrieve an OpenID Connect discovery document for the provider, or use a local static
 * configuration.
 * - Utilize dynamic client registration, if no static client id is specified.
 * - Initiate the authorization request using the built-in heuristics or a user-selected browser.
 * <p>
 * _NOTE_: From a clean checkout of this project, the authorization service is not configured.
 * Edit `res/raw/auth_config.json` to provide the required configuration properties. See the
 * README.md in the app/ directory for configuration instructions, and the adjacent IDP-specific
 * instructions.
 */
@AndroidEntryPoint
public final class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String EXTRA_FAILED = "com.twb.pokergame.auth.failed";
    private final AtomicReference<String> clientId = new AtomicReference<>();
    private final AtomicReference<AuthorizationRequest> authRequest = new AtomicReference<>();
    private final AtomicReference<CustomTabsIntent> authIntent = new AtomicReference<>();
    @Inject
    public AuthStateManager authStateManager;
    @Inject
    public AuthConfiguration authConfiguration;
    private CountDownLatch authIntentLatch = new CountDownLatch(1);
    private AuthorizationService authService;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newSingleThreadExecutor();

        if (authStateManager.getCurrent().isAuthorized()
                && !authConfiguration.hasConfigurationChanged()) {
            Log.i(TAG, "User is already authenticated, proceeding to token activity");
            startActivity(new Intent(this, TokenActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        findViewById(R.id.retry).setOnClickListener((View view) ->
                executor.submit(this::initializeAppAuth));
        findViewById(R.id.start_auth).setOnClickListener((View view) -> startAuth());

        ((EditText) findViewById(R.id.login_hint_value)).addTextChangedListener(
                new LoginHintChangeHandler());

        if (!authConfiguration.isValid()) {
            displayError(authConfiguration.getConfigurationError(), false);
            return;
        }

        if (authConfiguration.hasConfigurationChanged()) {
            // discard any existing authorization state due to the change of configuration
            Log.i(TAG, "Configuration change detected, discarding old state");
            authStateManager.replace(new AuthState());
            authConfiguration.acceptConfiguration();
        }

        if (getIntent().getBooleanExtra(EXTRA_FAILED, false)) {
            displayAuthCancelled();
        }

        displayLoading("Initializing");
        executor.submit(this::initializeAppAuth);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    protected void onStop() {
        executor.shutdownNow();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (authService != null) {
            authService.dispose();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displayAuthOptions();
        if (resultCode == RESULT_CANCELED) {
            displayAuthCancelled();
        }
    }

    @MainThread
    void startAuth() {
        displayLoading("Making authorization request");

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        executor.submit(this::doAuth);
    }

    /**
     * Initializes the authorization service configuration if necessary, either from the local
     * static values or by retrieving an OpenID discovery document.
     */
    @WorkerThread
    private void initializeAppAuth() {
        Log.i(TAG, "Initializing AppAuth");
        recreateAuthorizationService();

        if (authStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
            // configuration is already created, skip to client initialization
            Log.i(TAG, "auth config already established");
            initializeClient();
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        runOnUiThread(() -> displayLoading("Retrieving discovery document"));
        Log.i(TAG, "Retrieving OpenID discovery doc");
        AuthorizationServiceConfiguration.fetchFromUrl(
                authConfiguration.getDiscoveryUri(),
                this::handleConfigurationRetrievalResult,
                authConfiguration.getConnectionBuilder());
    }

    @MainThread
    private void handleConfigurationRetrievalResult(AuthorizationServiceConfiguration config, AuthorizationException ex) {
        if (config == null) {
            Log.i(TAG, "Failed to retrieve discovery document", ex);
            displayError("Failed to retrieve discovery document: " + ex.getMessage(), true);
            return;
        }
        Log.i(TAG, "Discovery document retrieved");
        authStateManager.replace(new AuthState(config));
        executor.submit(this::initializeClient);
    }

    /**
     * Initiates a dynamic registration request if a client ID is not provided by the static
     * configuration.
     */
    @WorkerThread
    private void initializeClient() {

        if (authConfiguration.getClientId() != null) {
            Log.i(TAG, "Using static client ID: " + authConfiguration.getClientId());
            clientId.set(authConfiguration.getClientId());
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        RegistrationResponse lastResponse =
                authStateManager.getCurrent().getLastRegistrationResponse();

        if (lastResponse != null) {
            Log.i(TAG, "Using dynamic client ID: " + lastResponse.clientId);
            clientId.set(lastResponse.clientId);
            runOnUiThread(this::initializeAuthRequest);
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        runOnUiThread(() -> displayLoading("Dynamically registering client"));
        Log.i(TAG, "Dynamically registering client");

        RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
                authStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                Collections.singletonList(authConfiguration.getRedirectUri()))
                .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
                .build();

        authService.performRegistrationRequest(
                registrationRequest,
                this::handleRegistrationResponse);
    }

    @MainThread
    private void handleRegistrationResponse(RegistrationResponse response, AuthorizationException ex) {
        authStateManager.updateAfterRegistration(response, ex);

        if (response == null) {
            Log.i(TAG, "Failed to dynamically register client", ex);
            displayErrorLater("Failed to register client: " + ex.getMessage(), true);
            return;
        }

        Log.i(TAG, "Dynamically registered client: " + response.clientId);
        clientId.set(response.clientId);
        initializeAuthRequest();
    }

    @WorkerThread
    private void doAuth() {
        try {
            authIntentLatch.await();
        } catch (InterruptedException ex) {
            Log.w(TAG, "Interrupted while waiting for auth intent");
        }

        final Intent completionIntent = new Intent(this, TokenActivity.class);
        final Intent cancelIntent = new Intent(this, LoginActivity.class);
        cancelIntent.putExtra(EXTRA_FAILED, true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }

        PendingIntent completedIntent = PendingIntent
                .getActivity(this, 0, completionIntent, flags);
        PendingIntent canceledIntent = PendingIntent
                .getActivity(this, 0, cancelIntent, flags);

        authService.performAuthorizationRequest(
                authRequest.get(), completedIntent, canceledIntent, authIntent.get());
    }

    private void recreateAuthorizationService() {
        if (authService != null) {
            Log.i(TAG, "Discarding existing AuthService instance");
            authService.dispose();
        }
        authService = createAuthorizationService();
        authRequest.set(null);
        authIntent.set(null);
    }

    private AuthorizationService createAuthorizationService() {
        Log.i(TAG, "Creating authorization service");
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(AnyBrowserMatcher.INSTANCE);
        builder.setConnectionBuilder(authConfiguration.getConnectionBuilder());
        builder.setSkipIssuerHttpsCheck(!authConfiguration.isHttpsRequired());

        return new AuthorizationService(this, builder.build());
    }

    @MainThread
    private void displayLoading(String loadingMessage) {
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
        findViewById(R.id.auth_container).setVisibility(View.GONE);
        findViewById(R.id.error_container).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.loading_description)).setText(loadingMessage);
    }

    @MainThread
    private void displayError(String error, boolean recoverable) {
        findViewById(R.id.error_container).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);
        findViewById(R.id.auth_container).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.error_description)).setText(error);
        findViewById(R.id.retry).setVisibility(recoverable ? View.VISIBLE : View.GONE);
    }

    // WrongThread inference is incorrect in this case
    @SuppressWarnings("WrongThread")
    @AnyThread
    private void displayErrorLater(final String error, final boolean recoverable) {
        runOnUiThread(() -> displayError(error, recoverable));
    }

    @MainThread
    private void initializeAuthRequest() {
        createAuthRequest(getLoginHint());
        warmUpBrowser();
        displayAuthOptions();
    }

    @MainThread
    private void displayAuthOptions() {
        findViewById(R.id.auth_container).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);
        findViewById(R.id.error_container).setVisibility(View.GONE);

        AuthState state = authStateManager.getCurrent();
        AuthorizationServiceConfiguration config = state.getAuthorizationServiceConfiguration();

        String authEndpointStr;
        if (config.discoveryDoc != null) {
            authEndpointStr = "Discovered auth endpoint: \n";
        } else {
            authEndpointStr = "Static auth endpoint: \n";
        }
        authEndpointStr += config.authorizationEndpoint;
        ((TextView) findViewById(R.id.auth_endpoint)).setText(authEndpointStr);

        String clientIdStr;
        if (state.getLastRegistrationResponse() != null) {
            clientIdStr = "Dynamic client ID: \n";
        } else {
            clientIdStr = "Static client ID: \n";
        }
        clientIdStr += clientId;
        ((TextView) findViewById(R.id.client_id)).setText(clientIdStr);
    }

    private void displayAuthCancelled() {
        Snackbar.make(findViewById(R.id.coordinatorLayout),
                "Authorization canceled", Snackbar.LENGTH_SHORT).show();
    }

    private void warmUpBrowser() {
        authIntentLatch = new CountDownLatch(1);
        executor.execute(() -> {
            Log.i(TAG, "Warming up browser instance for auth request");
            CustomTabsIntent.Builder intentBuilder =
                    authService.createCustomTabsIntentBuilder(authRequest.get().toUri());
            intentBuilder.setToolbarColor(getColor(R.color.colorPrimary));
            authIntent.set(intentBuilder.build());
            authIntentLatch.countDown();
        });
    }

    private void createAuthRequest(@Nullable String loginHint) {
        Log.i(TAG, "Creating auth request for login hint: " + loginHint);
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                authStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                clientId.get(),
                ResponseTypeValues.CODE,
                authConfiguration.getRedirectUri())
                .setScope(authConfiguration.getScope());

        if (!TextUtils.isEmpty(loginHint)) {
            authRequestBuilder.setLoginHint(loginHint);
        }

        authRequest.set(authRequestBuilder.build());
    }

    private String getLoginHint() {
        return ((EditText) findViewById(R.id.login_hint_value))
                .getText().toString().trim();
    }

    /**
     * Responds to changes in the login hint. After a "debounce" delay, warms up the browser
     * for a request with the new login hint; this avoids constantly re-initializing the
     * browser while the user is typing.
     */
    private final class LoginHintChangeHandler implements TextWatcher {
        private static final int DEBOUNCE_DELAY_MS = 500;

        private final Handler handler;
        private RecreateAuthRequestTask task;

        LoginHintChangeHandler() {
            handler = new Handler(Looper.getMainLooper());
            task = new RecreateAuthRequestTask();
        }

        @Override
        public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            task.cancel();
            task = new RecreateAuthRequestTask();
            handler.postDelayed(task, DEBOUNCE_DELAY_MS);
        }

        @Override
        public void afterTextChanged(Editable ed) {
        }
    }

    private final class RecreateAuthRequestTask implements Runnable {
        private final AtomicBoolean canceled = new AtomicBoolean();

        @Override
        public void run() {
            if (canceled.get()) {
                return;
            }

            createAuthRequest(getLoginHint());
            warmUpBrowser();
        }

        public void cancel() {
            canceled.set(true);
        }
    }
}
