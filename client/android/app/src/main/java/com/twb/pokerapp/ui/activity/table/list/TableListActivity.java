package com.twb.pokerapp.ui.activity.table.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.ActivityTableListBinding;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.table.connect.TableConnectActivity;
import com.twb.pokerapp.ui.activity.table.create.TableCreateActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableListActivity extends BaseAuthActivity implements TableListAdapter.TableClickListener {
    private ActivityTableListBinding binding;
    private TableListViewModel viewModel;
    private TableListAdapter adapter;

    @Override
    protected View getContentView() {
        binding = ActivityTableListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(binding.toolbar);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TableListAdapter(this);
        binding.recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TableListViewModel.class);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
        viewModel.tablesLiveData.observe(this, tables -> {
            adapter.submitList(tables);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.errorLiveData.observe(this, throwable -> {
            if (throwable == null) return;
            binding.swipeRefreshLayout.setRefreshing(false);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), new AlertModalDialog.OnAlertClickListener() {
                        @Override
                        public void onSuccessClick() {
                            endSession();
                        }

                        @Override
                        public void onCancelClick() {
                            endSession();
                        }
                    });
            var prev = getSupportFragmentManager().findFragmentByTag("error_dialog");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "error_dialog");
            } else {
                Log.d("DEBUG", "Dialog error_dialog already visible!");
            }
        });
    }

    @Override
    protected void onAuthorized() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.refresh();
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable throwable) {
        if (throwable != null) {
            Toast.makeText(this, message + " " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onTableClicked(TableDTO table) {
        var intent = new Intent(this, TableConnectActivity.class);
        intent.putExtras(table.toBundle());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var id = item.getItemId();
        if (id == R.id.action_create_table) {
            var intent = new Intent(this, TableCreateActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
