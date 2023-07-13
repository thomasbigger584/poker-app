package com.twb.pokergame.ui.activity.pokergame;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.data.websocket.message.PokerAppWebSocketMessage;
import com.twb.pokergame.ui.activity.login.BaseAuthActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity {
    private static final String TAG = PokerGameActivity.class.getSimpleName();
    private PokerGameViewModel viewModel;
    private PokerTable pokerTable;

    @Override
    protected int getContentView() {
        return R.layout.activity_poker_game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_game);

        pokerTable = PokerTable.fromBundle(getIntent().getExtras());
        Toast.makeText(this, pokerTable.toString(), Toast.LENGTH_SHORT).show();

        viewModel = new ViewModelProvider(this).get(PokerGameViewModel.class);
        viewModel.errors.observe(this, this::onError);
        viewModel.messages.observe(this, this::onMessage);

    }

    @Override
    protected void onAuthorized() {
        try {
            viewModel.connect();
            viewModel.subscribe(pokerTable);
        } catch (Exception e) {
            Log.e(TAG, "onAuthorized: Failed to connect to websocket", e);
        }
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable t) {
        if (t != null) {
            Toast.makeText(this, message + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void onMessage(PokerAppWebSocketMessage message) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }
}
