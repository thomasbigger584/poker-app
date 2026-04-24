package com.twb.pokerapp.ui.activity.login;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twb.pokerapp.data.auth.AuthConfiguration;
import com.twb.pokerapp.data.auth.TailscaleController;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public abstract class BaseNetworkActivity extends AppCompatActivity {
    private static final String TAG = BaseNetworkActivity.class.getSimpleName();
    private static final String TAILSCALE_DIALOG_TAG = "tailscale_warning_dialog";

    @Inject
    public TailscaleController tailscaleController;

    @Inject
    public AuthConfiguration authConfiguration;

    protected ExecutorService networkExecutor;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        checkTailscale();
    }

    protected void checkTailscale() {
        networkExecutor.submit(() -> {
            var isConnected = tailscaleController.isTailscaleConnected();
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    Log.i(TAG, "checkTailscale: Finishing or destroying");;
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

    private void showTailscaleWarning() {
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
