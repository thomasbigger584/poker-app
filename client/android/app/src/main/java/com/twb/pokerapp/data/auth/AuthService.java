package com.twb.pokerapp.data.auth;

import android.content.Context;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.auth0.android.jwt.JWT;
import com.twb.pokerapp.data.exception.UnauthorizedException;
import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class AuthService {
    private static final String TAG = AuthService.class.getSimpleName();
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final int TOKEN_EXPIRY_LEEWAY_SECONDS = 10;
    private static final int REFRESH_TIMEOUT_SECONDS = 3;
    private final AuthStateManager authStateManager;
    private final AuthorizationService authService;
    private final ReentrantLock refreshLock = new ReentrantLock();

    public AuthService(Context context, AuthStateManager authStateManager,
                       AuthConfiguration authConfiguration) {
        this.authStateManager = authStateManager;
        this.authService = new AuthorizationService(context, new AppAuthConfiguration.Builder()
                .setConnectionBuilder(authConfiguration.getConnectionBuilder())
                .setSkipIssuerHttpsCheck(!authConfiguration.isHttpsRequired())
                .build());
    }

    public String getCurrentUser() {
        var jwt = getJwt();
        if (jwt == null) return null;
        return jwt.getClaim(USERNAME_CLAIM).asString();
    }

    public boolean isCurrentUser(AppUserDTO user) {
        var currentUser = getCurrentUser();
        return currentUser != null && currentUser.equals(user.getUsername());
    }

    public String getAccessToken() {
        var jwt = getJwt();
        return (jwt != null) ? jwt.toString() : null;
    }

    @WorkerThread // blocks until a new token is retrieved
    public String getAccessTokenWithRefresh() throws UnauthorizedException {
        refreshLock.lock();
        try {
            var jwt = getJwt();
            if (jwt == null) return null;

            if (jwt.isExpired(TOKEN_EXPIRY_LEEWAY_SECONDS)) {
                var latch = new CountDownLatch(1);
                final var errorRef = new AtomicReference<AuthorizationException>();

                var currentAuthState = authStateManager.getCurrent();
                var tokenRefreshRequest = currentAuthState.createTokenRefreshRequest();

                performTokenRequest(tokenRefreshRequest, (response, ex) -> {
                    authStateManager.updateAfterTokenResponse(response, ex);
                    if (ex != null) {
                        errorRef.set(ex);
                    }
                    latch.countDown();
                });

                try {
                    if (latch.await(REFRESH_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        if (errorRef.get() != null) {
                            // Check for the specific "invalid_grant" error
                            if ("invalid_grant".equals(errorRef.get().error)) {
                                AuthEventBus.triggerLogout();
                                throw new UnauthorizedException("Session expired", errorRef.get());
                            }
                            return null;
                        }
                        return authStateManager.getCurrent().getAccessToken();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            }
            return jwt.toString();
        } finally {
            refreshLock.unlock();
        }
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

    private JWT getJwt() {
        var currentAuthState = authStateManager.getCurrent();
        var accessToken = currentAuthState.getAccessToken();
        if (accessToken == null) {
            Log.w(TAG, "Cannot get access token as it is null");
            return null;
        }
        return new JWT(accessToken);
    }
}
