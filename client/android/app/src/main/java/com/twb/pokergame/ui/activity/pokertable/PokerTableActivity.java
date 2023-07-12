package com.twb.pokergame.ui.activity.pokertable;

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

import com.twb.pokergame.R;
import com.twb.pokergame.data.auth.AuthStateManager;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.ui.activity.login.BaseAuthActivity;
import com.twb.pokergame.ui.activity.pokergame.PokerGameActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerTableActivity extends BaseAuthActivity implements PokerTableAdapter.PokerTableClickListener {
    @Inject
    AuthStateManager authStateManager;
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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PokerTableAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PokerTableViewModel.class);
        viewModel.errors.observe(this, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onAuthorized() {
        viewModel.getPokerTables()
                .observe(this, pokerTables -> adapter.setData(pokerTables));
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        if (t != null) {
            Toast.makeText(this, message + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPokerTableClicked(PokerTable pokerTable) {
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
