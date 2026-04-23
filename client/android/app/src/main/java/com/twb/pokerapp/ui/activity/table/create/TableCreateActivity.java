package com.twb.pokerapp.ui.activity.table.create;

import static com.twb.pokerapp.ui.dialog.DialogHelper.createLoadingSpinner;
import static com.twb.pokerapp.ui.util.ActivityUtil.setupToolbar;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.ActivityTableCreateBinding;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;

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
        setupToolbar(this, binding.toolbar);

        loadingSpinner = createLoadingSpinner(this);

        viewModel = new ViewModelProvider(this).get(TableCreateViewModel.class);
        viewModel.clearError();
        viewModel.errors.observe(this, throwable -> {
            if (throwable == null) return;
            DialogHelper.dismiss(loadingSpinner);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), new AlertModalDialog.OnAlertClickListener() {
                        @Override
                        public void onSuccessClick() {
                        }

                        @Override
                        public void onCancelClick() {
                        }
                    });
            alertModalDialog.show(getSupportFragmentManager(), "error_modal");
            viewModel.clearError();
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

        viewModel.validationErrorResId.observe(this, resId -> {
            Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
        });

        binding.buttonSubmitTable.setOnClickListener(v -> onCreateTableClick());
    }

    @Override
    protected void onAuthorized() {
        // No-op for now
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        finish();
    }

    private void onCreateTableClick() {
        var name = binding.editTableName.getText().toString();
        var gameTypeSelectedPosition = binding.spinnerGameType.getSelectedItemPosition();
        var gameTypesArray = getResources().getStringArray(R.array.game_types_array);
        var gameType = gameTypesArray[gameTypeSelectedPosition];

        var speedMultiplier = binding.editSpeedMultiplier.getText().toString();
        var totalRounds = binding.editTotalRounds.getText().toString();
        var minPlayers = binding.editMinPlayers.getText().toString();
        var maxPlayers = binding.editMaxPlayers.getText().toString();
        var minBuyIn = binding.editMinBuyin.getText().toString();
        var maxBuyIn = binding.editMaxBuyin.getText().toString();

        DialogHelper.show(loadingSpinner);
        viewModel.validateAndCreate(name, gameType, speedMultiplier, totalRounds,
                minPlayers, maxPlayers, minBuyIn, maxBuyIn);
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
