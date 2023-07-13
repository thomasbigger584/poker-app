package com.twb.pokergame.ui.activity.pokergame;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.twb.pokergame.R;
import com.twb.pokergame.data.message.client.CreateChatMessageDTO;
import com.twb.pokergame.data.message.server.ServerMessage;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.ui.activity.login.BaseAuthActivity;
import com.twb.stomplib.dto.LifecycleEvent;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity implements PokerGameViewModel.WebSocketListener {
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
        ;
        viewModel = new ViewModelProvider(this).get(PokerGameViewModel.class);
    }

    @Override
    protected void onAuthorized() {
        viewModel.connect(pokerTable.getId(), this);
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
    public void onOpened(LifecycleEvent event) {
        Log.i(TAG, "onOpened: " + event);

        CreateChatMessageDTO message = new CreateChatMessageDTO();
        message.setMessage("message sent from client");

        viewModel.sendChatMessage(pokerTable.getId(), message, new PokerGameViewModel.SendListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PokerGameActivity.this, "Successful Send", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(PokerGameActivity.this, "Failed Send: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectError(LifecycleEvent event) {
        Toast.makeText(this, "Subscribe Error: " + event.getException().getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClosed(LifecycleEvent event) {
        Log.i(TAG, "onClosed: Connection Closed: " + event.getMessage());
    }

    @Override
    public void onFailedServerHeartbeat(LifecycleEvent event) {
        Toast.makeText(this, "Failed Heartbeat: " + event.getException().getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessage(ServerMessage message) {
        Log.i(TAG, "onMessage: " + message.toString());
    }

    @Override
    public void onSubscribeError(Throwable throwable) {
        Toast.makeText(this, "Subscribe Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }
}
