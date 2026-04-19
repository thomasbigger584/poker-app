package com.twb.pokerapp.ui.activity.table.connect;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

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
    private TableConnectViewModel viewModel;

    @Override
    protected View getContentView() {
        binding = ActivityTableConnectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!initIncomingData()) return;

        setupToolbar();

        binding.textConnectToTable.setText(getString(R.string.connect_to_table_format, table.getName()));

        binding.connectTypeRadioGroup.setOnCheckedChangeListener((radioGroup, radioButtonSelectedId) -> {
            boolean isViewer = radioButtonSelectedId == R.id.radio_viewer;
            binding.buyInLinearLayout.setVisibility(isViewer ? View.GONE : View.VISIBLE);
        });

        binding.buttonMinBuyIn.setOnClickListener(v -> {
            binding.buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", table.getMinBuyin()));
        });
        binding.buttonMaxBuyIn.setOnClickListener(v -> {
            binding.buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", table.getMaxBuyin()));
        });

        viewModel = new ViewModelProvider(this).get(TableConnectViewModel.class);
        viewModel.errorResId.observe(this, resId -> {
            if (resId == R.string.error_buy_in_range) {
                Toast.makeText(this, getString(resId, table.getMinBuyin(), table.getMaxBuyin()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.connectEvent.observe(this, event -> {
            TexasGameActivity.startActivity(this, event.getTable(), event.getConnectionType(), event.getBuyInAmount());
            finish();
        });

        binding.buttonConnectTable.setOnClickListener(v -> {
            var buyInStr = binding.buyInEditText.getText().toString();
            var selectedRadioId = binding.connectTypeRadioGroup.getCheckedRadioButtonId();
            viewModel.onConnectTableClick(table, selectedRadioId, buyInStr);
        });
    }

    private boolean initIncomingData() {
        var intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            return false;
        }
        var extras = intent.getExtras();
        if (extras == null) {
            Toast.makeText(this, "Bundle extras is null", Toast.LENGTH_SHORT).show();
            return false;
        }
        table = TableDTO.fromBundle(extras);
        return true;
    }

    @Override
    protected void onAuthorized() {
        // No-op for now
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        finish();
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
}
