package com.twb.pokergame.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.twb.pokergame.data.auth.AuthConfiguration;
import com.twb.pokergame.data.auth.AuthStateManager;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.EndSessionRequest;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * Displays the authorized state of the user. This activity is provided with the outcome of the
 * authorization flow, which it uses to negotiate the final authorized state,
 * by performing an authorization code exchange if necessary. After this, the activity provides
 * additional post-authorization operations if available, such as fetching user info and refreshing
 * access tokens.
 */
@AndroidEntryPoint
public abstract class BaseAuthActivity extends AppCompatActivity {
    private static final String TAG = BaseAuthActivity.class.getSimpleName();
    private static final String KEY_USER_INFO = "userInfo";
    private static final int END_SESSION_REQUEST_CODE = 911;

    private final AtomicReference<JSONObject> userInfoJson = new AtomicReference<>();

    @Inject
    public AuthStateManager authStateManager;

    @Inject
    public AuthConfiguration authConfiguration;

    private AuthorizationService authService;

    private ExecutorService executor;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newSingleThreadExecutor();

        if (authConfiguration.hasConfigurationChanged()) {
            Toast.makeText(this, "Configuration change detected", Toast.LENGTH_SHORT).show();
            signOut();
            return;
        }

        authService = new AuthorizationService(this, new AppAuthConfiguration.Builder()
                .setConnectionBuilder(authConfiguration.getConnectionBuilder())
                .setSkipIssuerHttpsCheck(!authConfiguration.isHttpsRequired())
                .build());

        setContentView(getContentView());

        if (savedInstanceState != null) {
            try {
                userInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }
        }
    }


    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();

        if (executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }

        if (authStateManager.getCurrent().isAuthorized()) {
            onAuthorized();
            return;
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            authStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            authStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            onNotAuthorized("Authorization flow failed", ex);
        } else {
            onNotAuthorized("No authorization state retained - reauthorization required", null);
        }
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (userInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, userInfoJson.toString());
        }
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        authService.dispose();
        executor.shutdownNow();
    }

    protected void refreshAccessToken() {
        Log.i(TAG, "Refreshing access token...");
        performTokenRequest(
                authStateManager.getCurrent().createTokenRefreshRequest(),
                this::handleAccessTokenResponse);
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        Log.i(TAG, "Exchanging authorization code...");
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @MainThread
    private void performTokenRequest(TokenRequest request, AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = authStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            onNotAuthorized("Client authentication method is unsupported", ex);
            return;
        }

        authService.performTokenRequest(request, clientAuthentication, callback);
    }

    @WorkerThread
    private void handleAccessTokenResponse(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException authException) {
        authStateManager.updateAfterTokenResponse(tokenResponse, authException);
        runOnUiThread(this::onAuthorized);
    }

    @WorkerThread
    private void handleCodeExchangeResponse(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException authException) {
        authStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!authStateManager.getCurrent().isAuthorized()) {
            runOnUiThread(() -> onNotAuthorized("Authorization Code exchange failed", authException));
        } else {
            runOnUiThread(this::onAuthorized);
        }
    }


    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == END_SESSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            signOut();
            finish();
        }
    }

    @MainThread
    protected void endSession() {
        AuthState currentState = authStateManager.getCurrent();
        AuthorizationServiceConfiguration config =
                currentState.getAuthorizationServiceConfiguration();
        if (config.endSessionEndpoint != null) {
            Intent endSessionIntent = authService.getEndSessionRequestIntent(
                    new EndSessionRequest.Builder(config)
                            .setIdTokenHint(currentState.getIdToken())
                            .setPostLogoutRedirectUri(authConfiguration.getEndSessionRedirectUri())
                            .build());
            startActivityForResult(endSessionIntent, END_SESSION_REQUEST_CODE);
        } else {
            signOut();
        }
    }

    @MainThread
    private void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = authStateManager.getCurrent();
        AuthState clearedState = new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        authStateManager.replace(clearedState);

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    @LayoutRes
    protected abstract int getContentView();

    protected abstract void onAuthorized();

    protected abstract void onNotAuthorized(String message, @Nullable Throwable t);
}
