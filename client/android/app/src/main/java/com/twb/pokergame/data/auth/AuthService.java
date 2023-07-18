package com.twb.pokergame.data.auth;

import com.auth0.android.jwt.JWT;

import net.openid.appauth.AuthState;

public class AuthService {
    private static final String USERNAME_CLAIM = "preferred_username";
    private final AuthStateManager authStateManager;

    public AuthService(AuthStateManager authStateManager) {
        this.authStateManager = authStateManager;
    }

    public String getCurrentUser() {
        AuthState currentAuthState = authStateManager.getCurrent();
        if (currentAuthState.getAccessToken() == null) {
            throw new RuntimeException("Cannot get access token as it is null");
        }
        JWT jwt = new JWT(currentAuthState.getAccessToken());
        return jwt.getClaim(USERNAME_CLAIM).asString();
    }
}
