package com.twb.pokerapp.ui.activity.table.connect;

import static com.twb.pokerapp.ui.util.ActivityUtil.setupToolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.protobuf.InvalidProtocolBufferException;
import com.twb.pokerapp.R;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.databinding.ActivityTableConnectBinding;
import com.twb.pokerapp.ui.activity.game.texas.TexasGameActivity;
import com.twb.pokerapp.ui.activity.base.BaseAuthActivity;
import com.twb.pokerapp.util.Protos;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableConnectActivity extends BaseAuthActivity {

    /** Binary-protobuf {@link TableDTO} passed in by the caller. */
    public static final String EXTRA_TABLE = "EXTRA_TABLE";

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
        setupToolbar(this, binding.toolbar);

        binding.textConnectToTable.setText(getString(R.string.connect_to_table_format, table.getName()));

        binding.connectTypeRadioGroup.setOnCheckedChangeListener((radioGroup, radioButtonSelectedId) -> {
            boolean isViewer = radioButtonSelectedId == R.id.radio_viewer;
            binding.buyInLinearLayout.setVisibility(isViewer ? View.GONE : View.VISIBLE);
        });

        binding.buttonMinBuyIn.setOnClickListener(v -> {
            binding.buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", Protos.money(table.getMinBuyin())));
        });
        binding.buttonMaxBuyIn.setOnClickListener(v -> {
            binding.buyInEditText.setText(String.format(Locale.getDefault(), "%.2f", Protos.money(table.getMaxBuyin())));
        });

        viewModel = new ViewModelProvider(this).get(TableConnectViewModel.class);
        viewModel.errorResId.observe(this, resId -> {
            if (resId == R.string.error_buy_in_range) {
                Toast.makeText(this, getString(resId, Protos.money(table.getMinBuyin()), Protos.money(table.getMaxBuyin())), Toast.LENGTH_SHORT).show();
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
        var tableBytes = intent.getByteArrayExtra(EXTRA_TABLE);
        if (tableBytes == null) {
            Toast.makeText(this, "Table is missing", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            table = TableDTO.parseFrom(tableBytes);
        } catch (InvalidProtocolBufferException e) {
            Toast.makeText(this, "Table is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
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
}
