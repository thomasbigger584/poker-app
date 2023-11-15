package com.twb.pokerapp.ui.activity.pokergame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.model.dto.appuser.AppUserDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.model.dto.pokertable.TableDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.pokergame.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import com.twb.pokerapp.ui.dialog.FinishActivityOnClickListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity {
    private static final String TAG = PokerGameActivity.class.getSimpleName();

    @Inject
    AuthService authService;

    private PokerGameViewModel viewModel;
    private TableDTO pokerTable;
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
        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Toast.makeText(this, "Bundle extras is null", Toast.LENGTH_SHORT).show();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pokerTable = TableDTO.fromBundle(extras);

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
        viewModel.playerSubscribed.observe(this, playerSubscribed -> {
            String currentUsername = authService.getCurrentUser();
            PlayerSessionDTO currentPlayerSession =
                    playerSubscribed.getCurrentPlayerSession(currentUsername);
            tableController.connectCurrentPlayer(currentPlayerSession);
            for (PlayerSessionDTO playerSession : playerSubscribed.getPlayerSessions()) {
                if (!playerSession.getUser().getUsername().equals(currentUsername)) {
                    tableController.connectOtherPlayer(playerSession);
                }
            }
            chatBoxAdapter.add("Connected: " + currentUsername);
            DialogHelper.dismiss(loadingSpinner);
        });
        viewModel.playerConnected.observe(this, playerConnected -> {
            String currentUsername = authService.getCurrentUser();
            PlayerSessionDTO playerSession = playerConnected.getPlayerSession();
            AppUserDTO user = playerSession.getUser();
            if (!user.getUsername().equals(currentUsername)) {
                tableController.connectOtherPlayer(playerSession);
                chatBoxAdapter.add("Connected: " + user.getUsername());
            }
        });
        viewModel.dealerDetermined.observe(this, dealerDetermined -> {
            tableController.dealerDetermined(dealerDetermined.getPlayerSession());
        });
        viewModel.dealPlayerCard.observe(this, dealPlayerCard -> {
            String currentUsername = authService.getCurrentUser();
            PlayerSessionDTO playerSession = dealPlayerCard.getPlayerSession();
            AppUserDTO user = playerSession.getUser();
            if (user.getUsername().equals(currentUsername)) {
                tableController.dealCurrentPlayerCard(dealPlayerCard);
            } else {
                tableController.dealOtherPlayerCard(dealPlayerCard);
            }
        });
        viewModel.playerTurn.observe(this, playerTurn -> {
            tableController.updatePlayerTurn(playerTurn.getPlayerSession());
        });
        viewModel.dealCommunityCard.observe(this, dealCommunityCard -> {
            tableController.dealCommunityCard(dealCommunityCard);
        });
        viewModel.roundFinished.observe(this, roundFinished -> {
            tableController.reset(roundFinished);
        });
        viewModel.gameFinished.observe(this, gameFinished -> {
            FinishActivityOnClickListener clickListener = new FinishActivityOnClickListener(this);
            AlertModalDialog alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.INFO, "Game Finished", clickListener);
            alertModalDialog.show(getSupportFragmentManager(), "modal_alert");
            chatBoxAdapter.add("Game Finished");
        });


        //todo: add more

        viewModel.chatMessage.observe(this, chatMessage -> {
            chatBoxAdapter.add(chatMessage.getUsername() + ": " + chatMessage.getMessage());
        });
        viewModel.logMessage.observe(this, logMessage -> {
            chatBoxAdapter.add(logMessage.getMessage());
        });
        viewModel.errorMessage.observe(this, errorMessage -> {
            handleErrorMessage(errorMessage.getMessage());
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
        handleErrorMessage(message);
    }

    private void handleErrorMessage(String message) {
        DialogHelper.dismiss(loadingSpinner);
        FinishActivityOnClickListener clickListener = new FinishActivityOnClickListener(this);
        AlertModalDialog alertModalDialog = AlertModalDialog
                .newInstance(AlertModalDialog.AlertModalType.ERROR, message, clickListener);
        alertModalDialog.show(getSupportFragmentManager(), "modal_alert");
        chatBoxAdapter.add(message);
    }

    @Override
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }
}
