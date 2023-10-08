package com.twb.pokerapp.ui.activity.pokertable;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthStateManager;
import com.twb.pokerapp.data.model.dto.pokertable.TableDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.pokerapp.PokerGameActivity;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerTableActivity extends BaseAuthActivity
        implements PokerTableAdapter.PokerTableClickListener {

    @Inject
    AuthStateManager authStateManager;
    private AlertDialog loadingSpinner;
    private PokerTableViewModel viewModel;
    private PokerTableAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_poker_table;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingSpinner = DialogHelper.createLoadingSpinner(this);
        DialogHelper.show(loadingSpinner);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PokerTableAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PokerTableViewModel.class);
        viewModel.errors.observe(this, error -> {
            if (error != null) {
                DialogHelper.dismiss(loadingSpinner);
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
        viewModel.getPokerTables().observe(this, pokerTables -> {
            adapter.setData(pokerTables);
            DialogHelper.dismiss(loadingSpinner);
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
    public void onPokerTableClicked(TableDTO pokerTable) {
        Intent intent = new Intent(this, PokerGameActivity.class);
        intent.putExtras(pokerTable.toBundle());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.poker_table_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            endSession();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
