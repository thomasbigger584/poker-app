package com.twb.pokergame.ui.activity.login;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twb.pokergame.R;
import com.twb.pokergame.ui.activity.login.auth.AuthHelper;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String EXTRA_FAILED = "failed";
    private static final int RC_AUTH = 100;

    AuthHelper auth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auth Flow -------------------------------------------------------------
        auth = new AuthHelper(this);
        // -----------------------------------------------------------------------

        setContentView(R.layout.activity_login);
        findViewById(R.id.login_button).setOnClickListener(view -> doAuth());
    }

    private void doAuth() {
        auth.doAuthorization(intent -> startActivityForResult(intent, RC_AUTH));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_AUTH) {
            auth.handlePostAuthFlow(data);
            if (auth.isAuthorised()) {
                Toast.makeText(this, auth.getValidAccessToken(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.isAuthorised()) {
            Log.d(TAG, "User is already authenticated, proceeding to get token");
            Toast.makeText(this, auth.getValidAccessToken(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please Login", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.disposeAuthService();
    }
}
