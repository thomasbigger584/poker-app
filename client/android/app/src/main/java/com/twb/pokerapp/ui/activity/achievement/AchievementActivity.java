package com.twb.pokerapp.ui.activity.achievement;

import static com.twb.pokerapp.ui.dialog.DialogHelper.createLoadingSpinner;
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
import com.twb.pokerapp.databinding.ActivityAchievementBinding;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AchievementActivity extends BaseAuthActivity {
    private ActivityAchievementBinding binding;
    private AchievementViewModel viewModel;
    private AlertDialog loadingSpinner;

    @Override
    protected View getContentView() {
        binding = ActivityAchievementBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(this, binding.toolbar);
        loadingSpinner = createLoadingSpinner(this);
        viewModel = new ViewModelProvider(this).get(AchievementViewModel.class);
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
