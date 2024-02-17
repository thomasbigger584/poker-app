package com.twb.pokerapp.ui.activity.pokergame;

import android.content.Intent;
import android.os.Bundle;
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
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.model.dto.pokertable.TableDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.pokergame.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokerapp.ui.activity.pokergame.controller.ControlsController;
import com.twb.pokerapp.ui.activity.pokergame.controller.TableController;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import com.twb.pokerapp.ui.dialog.FinishActivityOnClickListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity {
    private static final String TAG = PokerGameActivity.class.getSimpleName();
    private static final String MODAL_TAG = "modal_alert";

    @Inject
    AuthService authService;

    private PokerGameViewModel viewModel;
    private TableDTO pokerTable;
    private AlertDialog loadingSpinner;
    private ChatBoxRecyclerAdapter chatBoxAdapter;
    private TableController tableController;
    private ControlsController controlsController;

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
        controlsController = new ControlsController(this);

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
            alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
            chatBoxAdapter.add(throwable.getMessage());
        });
        viewModel.closedConnection.observe(this, aVoid -> {
            DialogHelper.dismiss(loadingSpinner);
            String message = "Lost connection with server";
            AlertModalDialog alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, message, new FinishActivityOnClickListener(this));
            alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
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
            PlayerSessionDTO playerSession = playerConnected.getPlayerSession();
            if (!authService.isCurrentUser(playerSession.getUser())) {
                tableController.connectOtherPlayer(playerSession);
                chatBoxAdapter.add("Connected: " + playerSession.getUser().getUsername());
            }
        });
        viewModel.dealerDetermined.observe(this, dealerDetermined -> {
            tableController.dealerDetermined(dealerDetermined.getPlayerSession());
        });
        viewModel.dealPlayerCard.observe(this, dealPlayerCard -> {
            tableController.hidePlayerTurns();
            controlsController.hide();
            PlayerSessionDTO playerSession = dealPlayerCard.getPlayerSession();
            if (authService.isCurrentUser(playerSession.getUser())) {
                tableController.dealCurrentPlayerCard(dealPlayerCard);
            } else {
                tableController.dealOtherPlayerCard(dealPlayerCard);
            }
        });
        viewModel.playerTurn.observe(this, playerTurn -> {
            PlayerSessionDTO playerSession = playerTurn.getPlayerSession();
            tableController.updatePlayerTurn(playerSession);
            if (authService.isCurrentUser(playerSession.getUser())) {
                controlsController.show(playerTurn.getActions());
            } else {
                controlsController.hide();
            }
        });
        viewModel.dealCommunityCard.observe(this, dealCommunityCard -> {
            controlsController.hide();
            tableController.dealCommunityCard(dealCommunityCard);
        });
        viewModel.roundFinished.observe(this, roundFinished -> {
            tableController.hidePlayerTurns();
            controlsController.hide();
            tableController.reset(roundFinished);
        });
        viewModel.gameFinished.observe(this, gameFinished -> {
            FinishActivityOnClickListener clickListener = new FinishActivityOnClickListener(this);
            AlertModalDialog alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.INFO, "Game Finished", clickListener);
            alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
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

    @Override
    protected void onDestroy() {
        viewModel.disconnect();
        super.onDestroy();
    }

    /*
     * Button onClick event methods
     * ****************************************************************************
     */

    public void onFoldClick(View view) {
        viewModel.onPlayerAction("FOLD");
    }

    public void onRaiseClick(View view) {
        viewModel.onPlayerAction("RAISE");
    }

    public void onBetClick(View view) {
        viewModel.onPlayerAction("BET");
    }

    public void onCallClick(View view) {
        viewModel.onPlayerAction("CALL");
    }

    public void onCheckClick(View view) {
        viewModel.onPlayerAction("CHECK");
    }

    /*
     * Helper Methods
     * ****************************************************************************
     */

    private void handleErrorMessage(String message) {
        DialogHelper.dismiss(loadingSpinner);
        FinishActivityOnClickListener clickListener = new FinishActivityOnClickListener(this);
        AlertModalDialog alertModalDialog = AlertModalDialog
                .newInstance(AlertModalDialog.AlertModalType.ERROR, message, clickListener);
        alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
        chatBoxAdapter.add(message);
    }
}
