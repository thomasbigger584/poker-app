package com.twb.pokergame.data.auth;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.auth0.android.jwt.JWT;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;

public class AuthService {
    private static final String TAG = AuthService.class.getSimpleName();
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final int TOKEN_EXPIRY_LEEWAY_SECONDS = 10;
    private final AuthStateManager authStateManager;
    private final AuthorizationService authService;

    public AuthService(Context context, AuthStateManager authStateManager,
                       AuthConfiguration authConfiguration) {
        this.authStateManager = authStateManager;
        this.authService = new AuthorizationService(context, new AppAuthConfiguration.Builder()
                .setConnectionBuilder(authConfiguration.getConnectionBuilder())
                .setSkipIssuerHttpsCheck(!authConfiguration.isHttpsRequired())
                .build());
    }

    public String getCurrentUser() {
        JWT jwt = getJwt();
        return jwt.getClaim(USERNAME_CLAIM).asString();
    }

    public String getAccessToken() {
        return getJwt().toString();
    }

    @WorkerThread // blocks until a new token is retrieved
    public String getAccessTokenWithRefresh() {
        JWT jwt = getJwt();
        if (jwt.isExpired(TOKEN_EXPIRY_LEEWAY_SECONDS)) {
            Thread thread = new Thread(() -> {
                AuthState currentAuthState = authStateManager.getCurrent();
                TokenRequest tokenRefreshRequest = currentAuthState.createTokenRefreshRequest();
                performTokenRequest(tokenRefreshRequest, (response, ex) -> {
                    authStateManager.updateAfterTokenResponse(response, ex);
                });
            });
            thread.start();
            try {
                thread.join();
                return authStateManager.getCurrent().getAccessToken();
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to wait for refresh thread", e);
            }
        }
        return jwt.toString();
    }

    private void performTokenRequest(TokenRequest request, AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = authStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            return;
        }
        authService.performTokenRequest(request, clientAuthentication, callback);
    }

    @NonNull
    private JWT getJwt() {
        AuthState currentAuthState = authStateManager.getCurrent();
        if (currentAuthState.getAccessToken() == null) {
            throw new RuntimeException("Cannot get access token as it is null");
        }
        return new JWT(currentAuthState.getAccessToken());
    }
}
