package com.twb.pokerapp.ui.activity.leaderboard;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.twb.pokerapp.databinding.ActivityLeaderboardBinding;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LeaderboardActivity extends BaseAuthActivity {
    private ActivityLeaderboardBinding binding;
    private LeaderboardViewModel viewModel;
    private AlertDialog loadingSpinner;

    @Override
    protected View getContentView() {
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar();

        loadingSpinner = DialogHelper.createLoadingSpinner(this);

        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
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

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        finish();
    }
}
