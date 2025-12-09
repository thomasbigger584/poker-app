package com.twb.pokerapp.ui.activity.table.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.table.connect.TableConnectActivity;
import com.twb.pokerapp.ui.activity.table.create.TableCreateActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableListActivity extends BaseAuthActivity
        implements TableListAdapter.PokerTableClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TableListViewModel viewModel;
    private TableListAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_table_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getTables().observe(this, pokerTables -> {
            adapter.setData(pokerTables);
            swipeRefreshLayout.setRefreshing(false);
        }));

        adapter = new TableListAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TableListViewModel.class);
        viewModel.errors.observe(this, error -> {
            if (error != null) {
                swipeRefreshLayout.setRefreshing(false);
                AlertModalDialog alertModalDialog = AlertModalDialog
                        .newInstance(AlertModalDialog.AlertModalType.ERROR, error.getMessage(), new AlertModalDialog.OnAlertClickListener() {
                            @Override
                            public void onSuccessClick() {
                                endSession();
                            }

                            @Override
                            public void onCancelClick() {
                                endSession();
                            }
                        });
                alertModalDialog.show(getSupportFragmentManager(), "modal_alert");
            }
        });
    }

    @Override
    protected void onAuthorized() {
        swipeRefreshLayout.setRefreshing(true);
        viewModel.getTables().observe(this, tables -> {
            adapter.setData(tables);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable throwable) {
        if (throwable != null) {
            Toast.makeText(this, message + " " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTableClicked(TableDTO table) {
        Intent intent = new Intent(this, TableConnectActivity.class);
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
        int id = item.getItemId();
        if (id == R.id.action_create_table) {
            Intent intent = new Intent(this, TableCreateActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
