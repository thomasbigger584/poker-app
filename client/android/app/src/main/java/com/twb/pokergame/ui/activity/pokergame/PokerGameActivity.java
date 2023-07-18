package com.twb.pokergame.ui.activity.pokergame;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokergame.R;
import com.twb.pokergame.data.auth.AuthService;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.ui.activity.login.BaseAuthActivity;
import com.twb.pokergame.ui.activity.pokergame.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokergame.ui.dialog.AlertModalDialog;
import com.twb.pokergame.ui.dialog.DialogHelper;
import com.twb.pokergame.ui.dialog.FinishActivityOnClickListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity {
    private static final String TAG = PokerGameActivity.class.getSimpleName();

    @Inject
    AuthService authService;

    private PokerGameViewModel viewModel;
    private PokerTable pokerTable;
    private AlertDialog loadingSpinner;
    private ChatBoxRecyclerAdapter chatBoxAdapter;
    private TableController tableController;

    @Override
    protected int getContentView() {
        return R.layout.activity_poker_game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pokerTable = PokerTable.fromBundle(getIntent().getExtras());

        loadingSpinner = DialogHelper.createLoadingSpinner(this);
        DialogHelper.show(loadingSpinner);

        tableController = new TableController(this);

        RecyclerView chatBoxRecyclerView = findViewById(R.id.chatBoxRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatBoxRecyclerView.setLayoutManager(layoutManager);

        chatBoxAdapter = new ChatBoxRecyclerAdapter(layoutManager);
        chatBoxRecyclerView.setAdapter(chatBoxAdapter);

        viewModel = new ViewModelProvider(this).get(PokerGameViewModel.class);
        viewModel.errors.observe(this, throwable -> {
            if (throwable == null) return;
            DialogHelper.dismiss(loadingSpinner);
            AlertModalDialog alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), null);
            alertModalDialog.show(getSupportFragmentManager(), "modal_alert");
            chatBoxAdapter.add(throwable.getMessage());
        });
        viewModel.closedConnection.observe(this, aVoid -> {
            DialogHelper.dismiss(loadingSpinner);
            String message = "Lost connection with server";
            AlertModalDialog alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, message, new FinishActivityOnClickListener(this));
            alertModalDialog.show(getSupportFragmentManager(), "modal_alert");
            chatBoxAdapter.add(message);
        });
        viewModel.playerSubscribed.observe(this, playerConnected -> {
            String currentUsername = authService.getCurrentUser();

            PlayerSessionDTO currentPlayerSession =
                    playerConnected.getCurrentPlayerSession(currentUsername);
            tableController.connectCurrentPlayer(currentPlayerSession);

            for (PlayerSessionDTO playerSession : playerConnected.getPlayerSessions()) {
                if (!playerSession.getUser().getUsername().equals(currentUsername)) {
                    tableController.connectOtherPlayer(playerSession);
                }
            }
            chatBoxAdapter.add("Connected: " + currentUsername);
            DialogHelper.dismiss(loadingSpinner);
        });
        viewModel.chatMessage.observe(this, chatMessage -> {
            chatBoxAdapter.add(chatMessage.getUsername() + ": " + chatMessage.getMessage());
        });
        viewModel.logMessage.observe(this, logMessage -> {
            chatBoxAdapter.add(logMessage.getMessage());
        });
        viewModel.playerDisconnected.observe(this, playerDisconnected -> {
            String username = playerDisconnected.getUsername();
            chatBoxAdapter.add("Disconnected: " + username);
            String current = authService.getCurrentUser();
            if (username.equals(current)) {
                finish();
            } else {
                tableController.disconnectOtherPlayer(username);
            }
        });
    }

    @Override
    protected void onAuthorized() {
        viewModel.connect(pokerTable.getId());
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable throwable) {
        DialogHelper.dismiss(loadingSpinner);
        AlertModalDialog alertModalDialog = AlertModalDialog
                .newInstance(AlertModalDialog.AlertModalType.ERROR, message, new FinishActivityOnClickListener(this));
        alertModalDialog.show(getSupportFragmentManager(), "modal_alert");
        chatBoxAdapter.add(message);
    }

    @Override
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }
}
