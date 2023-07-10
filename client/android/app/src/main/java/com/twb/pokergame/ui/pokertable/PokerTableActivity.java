package com.twb.pokergame.ui.pokertable;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokergame.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerTableActivity extends AppCompatActivity {
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
            recyclerView.setAdapter(new PokerTableAdapter(dataset));
        });
    }
}
