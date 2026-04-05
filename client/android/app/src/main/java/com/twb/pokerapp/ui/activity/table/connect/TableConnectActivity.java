package com.twb.pokerapp.ui.activity.table.connect;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.databinding.ActivityTableConnectBinding;
import com.twb.pokerapp.ui.activity.game.texas.TexasGameActivity;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableConnectActivity extends BaseAuthActivity {

    private ActivityTableConnectBinding binding;
    private TableDTO table;

    @Override
    protected View getContentView() {
        binding = ActivityTableConnectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar();
        var intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            return;
        }
        var extras = intent.getExtras();
        if (extras == null) {
            Toast.makeText(this, "Bundle extras is null", Toast.LENGTH_SHORT).show();
            return;
        }
        table = TableDTO.fromBundle(extras);

        binding.textConnectToTable.setText(getString(R.string.connect_to_table_format, table.getName()));

        binding.connectTypeRadioGroup.setOnCheckedChangeListener((radioGroup, radioButtonSelectedId) -> {
            if (radioButtonSelectedId == R.id.radio_viewer) {
                binding.buyInLinearLayout.setVisibility(View.GONE);
            } else {
                binding.buyInLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonMinBuyIn.setOnClickListener(this::onMinBuyInClick);
        binding.buttonMaxBuyIn.setOnClickListener(this::onMaxBuyInClick);
        binding.buttonConnectTable.setOnClickListener(this::onConnectTableClick);
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
        var buyInStr = binding.buyInEditText.getText().toString().trim();
        if (buyInStr.isBlank()) {
            Toast.makeText(this, R.string.error_blank_buy_in, Toast.LENGTH_SHORT).show();
            return;
        }
        var buyIn = Double.parseDouble(buyInStr);
        if (buyIn < table.getMinBuyin() || buyIn > table.getMaxBuyin()) {
            var message = getString(R.string.error_buy_in_range, table.getMinBuyin(), table.getMaxBuyin());
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }
        var radioButtonSelectedId = binding.connectTypeRadioGroup.getCheckedRadioButtonId();
        if (radioButtonSelectedId == -1) {
            Toast.makeText(this, R.string.error_select_connection_type, Toast.LENGTH_SHORT).show();
            return;
        }
        var connectionType = "PLAYER";
        if (radioButtonSelectedId == R.id.radio_viewer) {
            connectionType = "LISTENER";
            buyIn = 0d;
        }
        if (connectionType.equals("LISTENER")) {
            Toast.makeText(this, R.string.error_listener_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        if (table.getGameType().equals("TEXAS_HOLDEM")) {
            TexasGameActivity.startActivity(this, table, connectionType, buyIn);
        } else {
            Toast.makeText(this, R.string.error_unsupported_game_type, Toast.LENGTH_SHORT).show();
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
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            var upArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    public void onMinBuyInClick(View view) {
        binding.buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", table.getMinBuyin()));
    }

    public void onMaxBuyInClick(View view) {
        binding.buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", table.getMaxBuyin()));
    }
}
