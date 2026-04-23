package com.twb.pokerapp.ui.activity.transaction;

import static com.twb.pokerapp.ui.dialog.DialogHelper.createLoadingSpinner;
import static com.twb.pokerapp.ui.util.ActivityUtil.setupToolbar;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.ActivityTransactionHistoryBinding;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TransactionHistoryActivity extends BaseAuthActivity {
    private ActivityTransactionHistoryBinding binding;
    private TransactionHistoryViewModel viewModel;
    private TransactionHistoryAdapter adapter;
    private AlertDialog loadingSpinner;

    @Override
    protected View getContentView() {
        binding = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(this, binding.toolbar);
        loadingSpinner = createLoadingSpinner(this);

        viewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);
        viewModel.clearError();
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        setupTypeSpinner();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupTypeSpinner() {
        binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void refresh() {
        var selectedPosition = binding.typeSpinner.getSelectedItemPosition();
        var apiTypes = getResources().getStringArray(R.array.transaction_history_types_array);
        var selectedType = (selectedPosition != -1) ? apiTypes[selectedPosition] : apiTypes[0];

        viewModel.refresh(selectedType);
    }

    private void setupRecyclerView() {
        binding.transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionHistoryAdapter();
        binding.transactionRecyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.transactionsLiveData.observe(this, transactions -> {
            adapter.submitList(transactions);
            binding.noTransactionsText.setVisibility(
                    transactions == null || transactions.isEmpty() ? View.VISIBLE : View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            loadingSpinner.dismiss();
        });

        viewModel.errorLiveData.observe(this, throwable -> {
            if (throwable == null) return;
            binding.swipeRefreshLayout.setRefreshing(false);
            loadingSpinner.dismiss();
            var alertModalDialog = AlertModalDialog.newInstance(AlertModalDialog.AlertModalType.ERROR,
                    throwable.getMessage(), null);
            alertModalDialog.show(getSupportFragmentManager(), "error_dialog");
            viewModel.clearError();
        });
    }

    @Override
    protected void onAuthorized() {
        binding.swipeRefreshLayout.setRefreshing(true);
        refresh();
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
