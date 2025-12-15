package com.twb.pokerapp.ui.activity.table.create;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.CreateTableDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableCreateActivity extends BaseAuthActivity {
    private static final String MODAL_TAG = "modal_alert";
    private TableCreateViewModel viewModel;
    private AlertDialog loadingSpinner;
    private EditText tableNameEditText;
    private Spinner gameTypeSpinner;
    private EditText minPlayersEditText;
    private EditText maxPlayersEditText;
    private EditText minBuyInEditText;
    private EditText maxBuyInEditText;

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
            alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
        });
        viewModel.createdTableLiveData.observe(this, tableDTO -> {
            if (tableDTO == null) {
                Toast.makeText(this, "Table not created", Toast.LENGTH_SHORT).show();
                return;
            }
            DialogHelper.dismiss(loadingSpinner);
            Toast.makeText(this, "Created Table", Toast.LENGTH_SHORT).show();
            finish();
        });

        tableNameEditText = findViewById(R.id.edit_table_name);
        gameTypeSpinner = findViewById(R.id.spinner_game_type);
        minPlayersEditText = findViewById(R.id.edit_min_players);
        maxPlayersEditText = findViewById(R.id.edit_max_players);
        minBuyInEditText = findViewById(R.id.edit_min_buyin);
        maxBuyInEditText = findViewById(R.id.edit_max_buyin);
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
        var createTableDTO = new CreateTableDTO();
        var tableName = tableNameEditText.getText().toString().trim();
        if (tableName.isBlank()) {
            Toast.makeText(this, "Please enter a table name", Toast.LENGTH_SHORT).show();
            return;
        }
        createTableDTO.setName(tableName);

        var gameTypeSelectedPosition = gameTypeSpinner.getSelectedItemPosition();
        var gameTypesArray = getResources().getStringArray(R.array.game_types_array);
        var gameType = gameTypesArray[gameTypeSelectedPosition];
        createTableDTO.setGameType(gameType);

        var minPlayersString = minPlayersEditText.getText().toString().trim();
        if (minPlayersString.isBlank()) {
            Toast.makeText(this, "Please enter a minimum number of players", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMinPlayers(Integer.parseInt(minPlayersString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid minimum number of players", Toast.LENGTH_SHORT).show();
            return;
        }
        var maxPlayersString = maxPlayersEditText.getText().toString().trim();
        if (maxPlayersString.isBlank()) {
            Toast.makeText(this, "Please enter a maximum number of players", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMaxPlayers(Integer.parseInt(maxPlayersString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid maximum number of players", Toast.LENGTH_SHORT).show();
            return;
        }
        var minBuyInString = minBuyInEditText.getText().toString().trim();
        if (minBuyInString.isBlank()) {
            Toast.makeText(this, "Please enter a minimum buy-in amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMinBuyin(Double.parseDouble(minBuyInString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid minimum buy-in amount", Toast.LENGTH_SHORT).show();
            return;
        }
        var maxBuyInString = maxBuyInEditText.getText().toString().trim();
        if (maxBuyInString.isBlank()) {
            Toast.makeText(this, "Please enter a maximum buy-in amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            createTableDTO.setMaxBuyin(Double.parseDouble(maxBuyInString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid maximum buy-in amount", Toast.LENGTH_SHORT).show();
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
        var toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
