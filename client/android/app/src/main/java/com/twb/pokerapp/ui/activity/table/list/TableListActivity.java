package com.twb.pokerapp.ui.activity.table.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;
import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;
import com.twb.pokerapp.data.repository.RepositoryCallback;
import com.twb.pokerapp.databinding.ActivityTableListBinding;
import com.twb.pokerapp.databinding.NavHeaderTableListBinding;
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.ui.activity.achievement.AchievementActivity;
import com.twb.pokerapp.ui.activity.leaderboard.LeaderboardActivity;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.stats.StatsActivity;
import com.twb.pokerapp.ui.activity.table.connect.TableConnectActivity;
import com.twb.pokerapp.ui.activity.table.create.TableCreateActivity;
import com.twb.pokerapp.ui.activity.transaction.TransactionHistoryActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TableListActivity extends BaseAuthActivity implements
        TableListAdapter.TableClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    public AuthService authService;

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

        var toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.openid_logo_content_description, R.string.openid_logo_content_description);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TableListAdapter(this);
        binding.recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TableListViewModel.class);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
        viewModel.tablesLiveData.observe(this, tables -> {
            adapter.submitList(tables);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.userLiveData.observe(this, this::updateDrawerHeader);
        viewModel.errorLiveData.observe(this, throwable -> {
            if (throwable == null) return;
            binding.swipeRefreshLayout.setRefreshing(false);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), new AlertModalDialog.OnAlertClickListener() {
                        @Override
                        public void onSuccessClick() {
                        }

                        @Override
                        public void onCancelClick() {
                        }
                    });
            var prev = getSupportFragmentManager().findFragmentByTag("error_dialog");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "error_dialog");
            } else {
                Log.d("DEBUG", "Dialog error_dialog already visible!");
            }
        });

        setupDrawerHeader();
    }

    private void setupDrawerHeader() {
        var headerView = binding.navView.getHeaderView(0);
        var headerBinding = NavHeaderTableListBinding.bind(headerView);

        var username = authService.getCurrentUser();
        headerBinding.usernameTextView.setText(username);
        headerBinding.emailTextView.setText(username);
    }

    private void updateDrawerHeader(AppUserDTO user) {
        var headerView = binding.navView.getHeaderView(0);
        var headerBinding = NavHeaderTableListBinding.bind(headerView);

        headerBinding.usernameTextView.setText(user.getUsername());
        headerBinding.emailTextView.setText(user.getEmail());
        headerBinding.fundsTextView.setText(getString(R.string.funds_format, user.getTotalFunds()));
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            var listener = new AlertModalDialog.OnAlertClickListener() {
                @Override
                public void onSuccessClick() {
                    signOut();
                }

                @Override
                public void onCancelClick() {
                }
            };
            var alertModalDialog = AlertModalDialog.newInstance(AlertModalDialog.AlertModalType.CONFIRM,
                    getString(R.string.logout_confirm), listener);
            var prev = getSupportFragmentManager().findFragmentByTag("logout_modal");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "logout_modal");
            } else {
                Log.d("DEBUG", "Dialog logout_modal already visible!");
            }
        } else if (id == R.id.nav_reset_funds) {
            var listener = new AlertModalDialog.OnAlertClickListener() {
                @Override
                public void onSuccessClick() {
                    viewModel.resetFunds(new RepositoryCallback<>() {
                        @Override
                        public void onSuccess(AppUserDTO result) {
                            Toast.makeText(TableListActivity.this, "Funds Reset", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Toast.makeText(TableListActivity.this, "Failed to reset funds", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelClick() {
                }
            };
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.CONFIRM, getString(R.string.reset_funds_confirm), listener);
            var prev = getSupportFragmentManager().findFragmentByTag("reset_funds_modal");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "reset_funds_modal");
            } else {
                Log.d("DEBUG", "Dialog reset_funds_modal already visible!");
            }
        } else if (id == R.id.nav_player_stats) {
            startActivity(new Intent(this, StatsActivity.class));
        } else if (id == R.id.nav_transaction_history) {
            startActivity(new Intent(this, TransactionHistoryActivity.class));
        } else if (id == R.id.nav_achievements) {
            startActivity(new Intent(this, AchievementActivity.class));
        } else if (id == R.id.nav_leaderboards) {
            startActivity(new Intent(this, LeaderboardActivity.class));
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onAuthorized() {
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void refreshData() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.refresh();
    }
}
