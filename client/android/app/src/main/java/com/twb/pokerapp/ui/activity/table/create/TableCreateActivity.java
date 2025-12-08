package com.twb.pokerapp.ui.activity.table.create;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;

public class TableCreateActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    protected void onAuthorized() {

    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {

    }
}
