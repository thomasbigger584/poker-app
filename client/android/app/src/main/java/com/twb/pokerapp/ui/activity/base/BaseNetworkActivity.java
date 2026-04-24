package com.twb.pokerapp.ui.activity.base;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.TailscaleController;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public abstract class BaseNetworkActivity extends BasePermissionActivity {
    private static final String TAG = BaseNetworkActivity.class.getSimpleName();
    private static final String TAILSCALE_DIALOG_TAG = "tailscale_warning_dialog";
    private static final String NETWORK_DIALOG_TAG = "network_warning_dialog";

    @Inject
    public TailscaleController tailscaleController;

    @Inject
    public AuthConfiguration authConfiguration;

    @Inject
    public ConnectivityManager connectivityManager;

    protected ExecutorService networkExecutor;

    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        registerNetworkCallback();
        if (!isNetworkAvailable()) {
            showNetworkWarning();
        }
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        if (!isNetworkAvailable()) {
            showNetworkWarning();
        } else {
            checkTailscale();
        }
    }

    @Override
    @CallSuper
    protected void onStop() {
        unregisterNetworkCallback();
        super.onStop();
    }

    protected void checkTailscale() {
        networkExecutor.submit(() -> {
            var isConnected = tailscaleController.isTailscaleConnected();
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    Log.i(TAG, "checkTailscale: Finishing or destroying");
                    return;
                }
                if (isConnected) {
                    Log.i(TAG, "checkTailscale: isConnected 1");
                    dismissTailscaleWarning();
                    Log.i(TAG, "checkTailscale: isConnected 2");
                    onTailscaleSuccess();
                    Log.i(TAG, "checkTailscale: isConnected 3");
                } else {
                    Log.i(TAG, "checkTailscale: is NOT Connected 1");
                    showTailscaleWarning();
                    Log.i(TAG, "checkTailscale: is NOT Connected 2");
                    onTailscaleFailure();
                    Log.i(TAG, "checkTailscale: is NOT Connected 3");
                }
            });
        });
    }

    private void dismissTailscaleWarning() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        var fragment = getSupportFragmentManager().findFragmentByTag(TAILSCALE_DIALOG_TAG);
        if (fragment instanceof AlertModalDialog) {
            ((AlertModalDialog) fragment).dismissAllowingStateLoss();
        }
    }

    private void registerNetworkCallback() {
        if (networkCallback != null) return;
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                runOnUiThread(() -> dismissNetworkWarning());
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                runOnUiThread(() -> showNetworkWarning());
            }
        };
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    private void unregisterNetworkCallback() {
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        }
    }

    private boolean isNetworkAvailable() {
        var activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return false;
        var capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void showNetworkWarning() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        dismissTailscaleWarning();
        var fragment = getSupportFragmentManager().findFragmentByTag(NETWORK_DIALOG_TAG);
        if (fragment != null) {
            return;
        }
        var subtitle = "You are not connected to the internet. Please enable in your network settings.";
        var dialog = AlertModalDialog.newInstance(AlertModalDialog.AlertModalType.WARNING, subtitle, new AlertModalDialog.OnAlertClickListener() {
            @Override
            public void onSuccessClick() {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }

            @Override
            public void onCancelClick() {
            }
        });
        dialog.show(getSupportFragmentManager(), NETWORK_DIALOG_TAG);
    }

    private void dismissNetworkWarning() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        var fragment = getSupportFragmentManager().findFragmentByTag(NETWORK_DIALOG_TAG);
        if (fragment instanceof AlertModalDialog) {
            ((AlertModalDialog) fragment).dismissAllowingStateLoss();
            checkTailscale();
        }
    }

    private void showTailscaleWarning() {
        if (getSupportFragmentManager().findFragmentByTag(NETWORK_DIALOG_TAG) != null) {
            return;
        }
        var fragment = getSupportFragmentManager().findFragmentByTag(TAILSCALE_DIALOG_TAG);
        if (fragment != null) {
            return;
        }
        var discoveryHost = "auth server";
        if (authConfiguration.getDiscoveryUri() != null) {
            discoveryHost = authConfiguration.getDiscoveryUri().getHost();
        }
        var subtitle = "You are not connected to the Tailscale VPN or the appropriate Tailscale network. Click Confirm to open or install Tailscale. Please check your device has access to " + discoveryHost;
        var dialog = AlertModalDialog.newInstance(AlertModalDialog.AlertModalType.WARNING, subtitle, new AlertModalDialog.OnAlertClickListener() {
            @Override
            public void onSuccessClick() {
                var tailscalePackageName = "com.tailscale.ipn";
                var intent = getPackageManager().getLaunchIntentForPackage(tailscalePackageName);
                if (intent != null) {
                    startActivity(intent);
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + tailscalePackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + tailscalePackageName)));
                    }
                }
            }

            @Override
            public void onCancelClick() {
            }
        });
        dialog.show(getSupportFragmentManager(), TAILSCALE_DIALOG_TAG);
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        if (networkExecutor != null) {
            networkExecutor.shutdownNow();
        }
        super.onDestroy();
    }

    protected abstract void onTailscaleSuccess();

    protected void onTailscaleFailure() {
    }
}
