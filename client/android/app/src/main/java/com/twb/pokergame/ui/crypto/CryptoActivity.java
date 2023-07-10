package com.twb.pokergame.ui.crypto;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokergame.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CryptoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CryptoViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(CryptoViewModel.class);
        viewModel.cryptoLiveData.observe(this, dataset -> {
            recyclerView.setAdapter(new CryptoAdapter(dataset));
        });
    }
}
