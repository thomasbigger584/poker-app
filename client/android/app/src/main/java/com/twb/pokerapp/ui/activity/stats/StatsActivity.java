package com.twb.pokerapp.ui.activity.stats;

import static com.twb.pokerapp.ui.util.ActivityUtil.setupToolbar;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.twb.pokerapp.databinding.ActivityStatsBinding;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatsActivity extends BaseAuthActivity {
    private ActivityStatsBinding binding;
    private StatsViewModel viewModel;
    private AlertDialog loadingSpinner;

    @Override
    protected View getContentView() {
        binding = ActivityStatsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(this, binding.toolbar);
        loadingSpinner = DialogHelper.createLoadingSpinner(this);
        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
    }

    @Override
    protected void onAuthorized() {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        finish();
    }
}
