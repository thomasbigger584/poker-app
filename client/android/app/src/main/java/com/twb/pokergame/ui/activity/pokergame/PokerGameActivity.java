package com.twb.pokergame.ui.activity.pokergame;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.data.websocket.message.PokerAppWebSocketMessage;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends AppCompatActivity {
    private static final String TAG = PokerGameActivity.class.getSimpleName();
    private PokerGameViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game);

        PokerTable pokerTable = PokerTable.fromBundle(getIntent().getExtras());
        Toast.makeText(this, pokerTable.toString(), Toast.LENGTH_SHORT).show();

        viewModel = new ViewModelProvider(this).get(PokerGameViewModel.class);
        viewModel.errors.observe(this, this::onError);
        viewModel.messages.observe(this, this::onMessage);
    }

    public void connect(View view) {
        viewModel.connect();
        viewModel.subscribe();
    }

    private void onMessage(PokerAppWebSocketMessage message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void disconnect(View view) {
        viewModel.disconnect();
    }

    @Override
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }
}
