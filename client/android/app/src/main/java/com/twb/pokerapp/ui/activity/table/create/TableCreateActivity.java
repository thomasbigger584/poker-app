package com.twb.pokerapp.ui.activity.table.create;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.twb.pokerapp.R;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableCreateActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_table_create;
    }

    @Override
    protected void onAuthorized() {
        Toast.makeText(this, "onAuthorized", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        finish();
    }

    public void onCreateTableClick(View view) {
        Toast.makeText(this, "Create Table Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            Drawable upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

}
