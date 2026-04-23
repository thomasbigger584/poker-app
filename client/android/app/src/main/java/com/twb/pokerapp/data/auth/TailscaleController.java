package com.twb.pokerapp.data.auth;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.twb.pokerapp.data.retrofit.api.HealthApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class TailscaleController {
    private static final String TAG = TailscaleController.class.getSimpleName();
    private final AuthConfiguration authConfiguration;
    private final ConnectivityManager connectivityManager;
    private final HealthApi healthApi;

    @Inject
    public TailscaleController(AuthConfiguration authConfiguration,
                               ConnectivityManager connectivityManager,
                               HealthApi healthApi) {
        this.authConfiguration = authConfiguration;
        this.connectivityManager = connectivityManager;
        this.healthApi = healthApi;
    }

    public boolean isTailscaleCheckRequired() {
        var discoveryUri = authConfiguration.getDiscoveryUri();
        return discoveryUri != null && discoveryUri.getHost() != null && discoveryUri.getHost().endsWith(".ts.net");
    }

    @WorkerThread
    public boolean isTailscaleConnected() {
        if (!isTailscaleCheckRequired()) {
            return true;
        }

        var activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return false;

        var caps = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (caps == null || !caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
            return false;
        }

        var discoveryUri = authConfiguration.getDiscoveryUri();
        try {
            String url = new Uri.Builder()
                    .scheme(discoveryUri.getScheme())
                    .encodedAuthority(discoveryUri.getEncodedAuthority())
                    .path("/")
                    .build().toString();

            var response = healthApi.healthCheck(url).blockingGet();
            return response.isSuccessful();
        } catch (Exception e) {
            Log.w(TAG, "Tailscale connection check failed: " + e.getMessage());
            return false;
        }
    }
}
