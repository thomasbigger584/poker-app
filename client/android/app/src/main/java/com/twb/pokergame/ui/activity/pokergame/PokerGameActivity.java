package com.twb.pokergame.ui.activity.pokergame;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.PokerTable;
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
        pokerTable = PokerTable.fromBundle(getIntent().getExtras());
        viewModel = new ViewModelProvider(this).get(PokerGameViewModel.class);
        viewModel.errors.observe(this, throwable -> {
            // todo: add throwable to chatbox
            Toast.makeText(PokerGameActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        });
        viewModel.playerConnected.observe(this, playerConnected -> {
            Log.i(TAG, "Event: " + playerConnected);
            //todo: add player connected to chatbox
            //todo: add player to view
        });
        viewModel.chatMessage.observe(this, chatMessage -> {
            Log.i(TAG, "Event: " + chatMessage);
            //todo: add chat message to chat box
        });
        viewModel.logMessage.observe(this, logMessage -> {
            Log.i(TAG, "Event: " + logMessage);
            //todo: add log message to chat box
        });
        viewModel.playerConnected.observe(this, playerDisconnected -> {
            Log.i(TAG, "Event: " + playerDisconnected);
            //todo: add player disconnected to chatbox
            //todo: remove player to from view, if player is current player then finish activity
        });
    }

    @Override
    protected void onAuthorized() {
        viewModel.connect(pokerTable.getId());
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
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }
}
