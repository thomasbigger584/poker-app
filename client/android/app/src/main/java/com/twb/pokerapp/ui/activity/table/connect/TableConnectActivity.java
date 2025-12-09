package com.twb.pokerapp.ui.activity.table.connect;

import android.content.Intent;
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
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.ui.activity.game.texas.TexasGameActivity;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableConnectActivity extends BaseAuthActivity {

    private TableDTO table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Toast.makeText(this, "Bundle extras is null", Toast.LENGTH_SHORT).show();
            return;
        }
        table = TableDTO.fromBundle(extras);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_table_connect;
    }

    @Override
    protected void onAuthorized() {
        Toast.makeText(this, "onAuthorized", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        finish();
    }

    public void onConnectTableClick(View view) {
        if (table.getGameType().equals("TEXAS_HOLDEM")) {
            Intent intent = new Intent(this, TexasGameActivity.class);
            intent.putExtras(table.toBundle());
            intent.putExtra("CONNECTION_TYPE", "PLAYER");
            intent.putExtra("BUY-IN", 10_000d);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unsupported Game Type", Toast.LENGTH_SHORT).show();
        }
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
