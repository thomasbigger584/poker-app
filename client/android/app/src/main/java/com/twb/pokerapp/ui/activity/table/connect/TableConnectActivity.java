package com.twb.pokerapp.ui.activity.table.connect;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.ui.activity.game.texas.TexasGameActivity;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableConnectActivity extends BaseAuthActivity {

    private TableDTO table;
    private EditText buyInEditText;
    private RadioGroup connectTypeRadioGroup;

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

        TextView connectToTableTextView = findViewById(R.id.text_connect_to_table);
        connectToTableTextView.setText(String.format("Connect to %s", table.getName()));

        LinearLayout buyInLinearLayout = findViewById(R.id.buy_in_linear_layout);

        connectTypeRadioGroup = findViewById(R.id.connect_Type_radio_group);
        connectTypeRadioGroup.setOnCheckedChangeListener((radioGroup, radioButtonSelectedId) -> {
            if (radioButtonSelectedId == R.id.radio_viewer) {
                buyInLinearLayout.setVisibility(View.GONE);
            } else {
                buyInLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        buyInEditText = findViewById(R.id.buy_in_edit_text);
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
        String buyInStr = buyInEditText.getText().toString().trim();
        if (buyInStr.isBlank()) {
            Toast.makeText(this, "Cannot choose blank buy-in amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double buyIn = Double.parseDouble(buyInStr);
        if (buyIn < table.getMinBuyin() || buyIn > table.getMaxBuyin()) {
            Toast.makeText(this, "Buy-in amount must be between " + table.getMinBuyin() + " and " + table.getMaxBuyin(), Toast.LENGTH_SHORT).show();
            return;
        }
        int radioButtonSelectedId = connectTypeRadioGroup.getCheckedRadioButtonId();
        if (radioButtonSelectedId == -1) {
            Toast.makeText(this, "Must Select a Connection Type", Toast.LENGTH_SHORT).show();
            return;
        }
        String connectionType = "PLAYER";
        if (radioButtonSelectedId == R.id.radio_viewer) {
            connectionType = "LISTENER";
            buyIn = 0d;
        }
        if (table.getGameType().equals("TEXAS_HOLDEM")) {
            TexasGameActivity.startActivity(this, table, connectionType, buyIn);
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

    public void onMinBuyInClick(View view) {
        buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", table.getMinBuyin()));
    }

    public void onMaxBuyInClick(View view) {
        buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", table.getMaxBuyin()));
    }
}
