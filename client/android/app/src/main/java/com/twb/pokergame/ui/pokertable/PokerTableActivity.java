package com.twb.pokergame.ui.pokertable;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.ui.pokergame.PokerGameActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerTableActivity extends AppCompatActivity implements PokerTableAdapter.PokerTableClickListener {
    private RecyclerView recyclerView;
    private PokerTableViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_table);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(PokerTableViewModel.class);
        viewModel.errors.observe(this, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        viewModel.pokerTables.observe(this, dataset -> {
            recyclerView.setAdapter(new PokerTableAdapter(dataset, PokerTableActivity.this));
        });
    }

    @Override
    public void onPokerTableClicked(PokerTable pokerTable) {
        Intent intent = new Intent(this, PokerGameActivity.class);
        intent.putExtras(pokerTable.toBundle());
        startActivity(intent);
    }
}
