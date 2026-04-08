package com.twb.pokerapp.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.AuthStateManager;
import com.twb.pokerapp.data.exception.UnauthorizedException;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
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
            signOut();
            return;
        }

        authService = new AuthorizationService(this, new AppAuthConfiguration.Builder()
                .setConnectionBuilder(authConfiguration.getConnectionBuilder())
                .setSkipIssuerHttpsCheck(!authConfiguration.isHttpsRequired())
                .build());

        var view = getContentView();
        if (view != null) {
            setContentView(view);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_USER_INFO)) {
            try {
                var userInfo = savedInstanceState.getString(KEY_USER_INFO);
                if (userInfo != null) {
                    userInfoJson.set(new JSONObject(userInfo));
                }
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON", ex);
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

        // 1. Check if we already have a valid authorized state
        if (authStateManager.getCurrent().isAuthorized()) {
            onAuthorized();
            return;
        }

        // 2. Check if we are returning from the browser with an auth code
        var response = AuthorizationResponse.fromIntent(getIntent());
        var ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            authStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            onNotAuthorized("Authorization flow failed", ex);
        } else {
            // No state and no response in intent means we are just not logged in
            onNotAuthorized("No authorization state retained", null);
        }
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        Log.i(TAG, "Exchanging authorization code...");
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @MainThread
    protected void performTokenRequest(TokenRequest request,
                                       AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = authStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            onNotAuthorized("Client authentication method is unsupported", ex);
            return;
        }

        authService.performTokenRequest(request, clientAuthentication, callback);
    }

    @WorkerThread
    private void handleCodeExchangeResponse(@Nullable TokenResponse tokenResponse,
                                            @Nullable AuthorizationException authException) {
        authStateManager.updateAfterTokenResponse(tokenResponse, authException);

        if (authException != null && "invalid_grant".equals(authException.error)) {
            // The refresh token is dead; clean up and bail
            runOnUiThread(this::signOut);
            return;
        }

        if (!authStateManager.getCurrent().isAuthorized()) {
            runOnUiThread(() -> onNotAuthorized("Authorization failed", authException));
        } else {
            runOnUiThread(this::onAuthorized);
        }
    }

    /**
     * Helper for child activities to run actions with a guaranteed fresh token.
     */
    @MainThread
    protected void performActionWithFreshTokens(@NonNull AuthState.AuthStateAction action) {
        authStateManager.getCurrent().performActionWithFreshTokens(
                authService,
                (accessToken, idToken, ex) -> {
                    if (ex != null) {
                        handleUnauthorizedException(ex);
                        return;
                    }
                    action.execute(accessToken, idToken, null);
                });
    }

    @MainThread
    protected void endSession() {
        var currentState = authStateManager.getCurrent();
        var config = currentState.getAuthorizationServiceConfiguration();
        if (config != null && config.endSessionEndpoint != null && currentState.getIdToken() != null) {
            var endSessionIntent = authService.getEndSessionRequestIntent(
                    new EndSessionRequest.Builder(config)
                            .setIdTokenHint(currentState.getIdToken())
                            .setPostLogoutRedirectUri(authConfiguration.getEndSessionRedirectUri())
                            .build());
            startActivityForResult(endSessionIntent, END_SESSION_REQUEST_CODE);
        } else {
            signOut();
        }
    }

    protected void handleUnauthorizedException(Throwable throwable) {
        if (throwable == null) return;
        if (isAuthFailure(throwable) || (throwable.getCause() != null && isAuthFailure(throwable.getCause()))) {
            Log.e(TAG, "Auth failure detected, signing out...", throwable);
            signOut();
            Toast.makeText(this, R.string.you_have_been_signed_out, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAuthFailure(@NonNull Throwable t) {
        if (t instanceof UnauthorizedException) {
            return true;
        }
        // Check for AppAuth's specific error code
        if (t instanceof AuthorizationException) {
            var authEx = (AuthorizationException) t;
            return "invalid_grant".equals(authEx.error);
        }
        return false;
    }

    @MainThread
    protected void signOut() {
        var currentState = authStateManager.getCurrent();
        var config = currentState.getAuthorizationServiceConfiguration();

        // Clear auth state but keep config if possible
        AuthState clearedState = (config != null) ? new AuthState(config) : new AuthState();
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }

        authStateManager.replace(clearedState);

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }


    @Override
    @CallSuper
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        JSONObject currentInfo = userInfoJson.get();
        if (currentInfo != null) {
            state.putString(KEY_USER_INFO, currentInfo.toString());
        }
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        if (authService != null) authService.dispose();
        if (executor != null) executor.shutdownNow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == END_SESSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            signOut();
        }
    }

    @Nullable
    protected abstract View getContentView();

    protected abstract void onAuthorized();

    protected abstract void onNotAuthorized(String message, @Nullable Throwable t);
}
