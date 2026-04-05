package com.twb.pokerapp.ui.activity.table.create;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.CreateTableDTO;
import com.twb.pokerapp.databinding.ActivityTableCreateBinding;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableCreateActivity extends BaseAuthActivity {
    private ActivityTableCreateBinding binding;
    private TableCreateViewModel viewModel;
    private AlertDialog loadingSpinner;

    @Override
    protected View getContentView() {
        binding = ActivityTableCreateBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar();

        loadingSpinner = DialogHelper.createLoadingSpinner(this);

        viewModel = new ViewModelProvider(this).get(TableCreateViewModel.class);
        viewModel.errors.observe(this, throwable -> {
            if (throwable == null) return;
            DialogHelper.dismiss(loadingSpinner);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), null);
            alertModalDialog.show(getSupportFragmentManager(), "error_modal");
        });
        viewModel.createdTableLiveData.observe(this, tableDTO -> {
            if (tableDTO == null) {
                Toast.makeText(this, R.string.table_not_created, Toast.LENGTH_SHORT).show();
                return;
            }
            DialogHelper.dismiss(loadingSpinner);
            Toast.makeText(this, R.string.created_table, Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.buttonSubmitTable.setOnClickListener(this::onCreateTableClick);
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
        var createTableDTO = new CreateTableDTO();
        var tableName = binding.editTableName.getText().toString().trim();
        if (tableName.isBlank()) {
            Toast.makeText(this, R.string.error_blank_table_name, Toast.LENGTH_SHORT).show();
            return;
        }
        createTableDTO.setName(tableName);

        var gameTypeSelectedPosition = binding.spinnerGameType.getSelectedItemPosition();
        var gameTypesArray = getResources().getStringArray(R.array.game_types_array);
        var gameType = gameTypesArray[gameTypeSelectedPosition];
        createTableDTO.setGameType(gameType);

        var speedMultiplier = binding.editSpeedMultiplier.getText().toString().trim();
        if (speedMultiplier.isBlank()) {
            Toast.makeText(this, R.string.error_blank_speed_multiplier, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setSpeedMultiplier(Double.parseDouble(speedMultiplier));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_speed_multiplier, Toast.LENGTH_SHORT).show();
            return;
        }
        var totalRounds = binding.editTotalRounds.getText().toString().trim();
        if (totalRounds.isBlank()) {
            totalRounds = "-1"; // infinite
        }
        try {
            createTableDTO.setTotalRounds(Integer.parseInt(totalRounds));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_total_rounds, Toast.LENGTH_SHORT).show();
            return;
        }
        var minPlayersString = binding.editMinPlayers.getText().toString().trim();
        if (minPlayersString.isBlank()) {
            Toast.makeText(this, R.string.error_blank_min_players, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMinPlayers(Integer.parseInt(minPlayersString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_min_players, Toast.LENGTH_SHORT).show();
            return;
        }
        var maxPlayersString = binding.editMaxPlayers.getText().toString().trim();
        if (maxPlayersString.isBlank()) {
            Toast.makeText(this, R.string.error_blank_max_players, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMaxPlayers(Integer.parseInt(maxPlayersString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_max_players, Toast.LENGTH_SHORT).show();
            return;
        }
        var minBuyInString = binding.editMinBuyin.getText().toString().trim();
        if (minBuyInString.isBlank()) {
            Toast.makeText(this, R.string.error_blank_min_buyin, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMinBuyin(Double.parseDouble(minBuyInString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_min_buyin, Toast.LENGTH_SHORT).show();
            return;
        }
        var maxBuyInString = binding.editMaxBuyin.getText().toString().trim();
        if (maxBuyInString.isBlank()) {
            Toast.makeText(this, R.string.error_blank_max_buyin, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMaxBuyin(Double.parseDouble(maxBuyInString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_max_buyin, Toast.LENGTH_SHORT).show();
            return;
        }

        DialogHelper.show(loadingSpinner);
        viewModel.createTable(createTableDTO);
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
