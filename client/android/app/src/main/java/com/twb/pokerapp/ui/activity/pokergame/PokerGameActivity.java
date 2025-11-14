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
import com.twb.pokerapp.data.model.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.data.model.dto.playeraction.PlayerActionDTO;
import com.twb.pokerapp.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.data.model.dto.pokertable.TableDTO;
import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.pokergame.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokerapp.ui.activity.pokergame.controller.ControlsController;
import com.twb.pokerapp.ui.activity.pokergame.controller.TableController;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.BasePokerGameDialog;
import com.twb.pokerapp.ui.dialog.BetRaisePokerGameDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import com.twb.pokerapp.ui.dialog.FinishActivityOnClickListener;

import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity implements BetRaisePokerGameDialog.BetRaiseClickListener {
    private static final String TAG = PokerGameActivity.class.getSimpleName();
    private static final String MODAL_TAG = "modal_alert";

    @Inject
    AuthService authService;

    private PokerGameViewModel viewModel;
    private TableDTO pokerTable;
    private AlertDialog loadingSpinner;
    private BasePokerGameDialog betRaisePokerGameDialog;
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
            dismissDialogs();
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
            dismissDialogs();
        });
        viewModel.playerConnected.observe(this, playerConnected -> {
            PlayerSessionDTO playerSession = playerConnected.getPlayerSession();
            if (!authService.isCurrentUser(playerSession.getUser())) {
                tableController.connectOtherPlayer(playerSession);
                chatBoxAdapter.add("Connected: " + playerSession.getUser().getUsername());
            }
        });
        viewModel.dealerDetermined.observe(this, dealerDetermined -> {
            dismissDialogs();
            tableController.dealerDetermined(dealerDetermined.getPlayerSession());
        });
        viewModel.dealPlayerCard.observe(this, dealPlayerCard -> {
            dismissDialogs();
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
        viewModel.playerAction.observe(this, playerActionEvent -> {
            dismissDialogs();
            controlsController.hide();
            PlayerActionDTO playerAction = playerActionEvent.getAction();
            String thirdPerson = playerAction.getPlayerSession().getUser().getUsername();
            if (authService.isCurrentUser(playerAction.getPlayerSession().getUser())) {
                thirdPerson = "You";
            }
            String message = String.format("%s %s", thirdPerson, playerAction.getActionType().toLowerCase());
            Double amount = playerAction.getAmount();
            if (amount != null && amount > 0) {
                message += " with " + playerAction.getAmount();
            }
            BettingRoundDTO bettingRound = playerAction.getBettingRound();
            Double bettingRoundPot = bettingRound.getPot();
            if (bettingRoundPot != null && bettingRoundPot > 0) {
                message += String.format(Locale.getDefault(), " (betting round: %f)", bettingRoundPot);
            }
            chatBoxAdapter.add(message);
        });


        //todo: add more
        viewModel.dealCommunityCard.observe(this, dealCommunityCard -> {
            dismissDialogs();
            controlsController.hide();
            tableController.dealCommunityCard(dealCommunityCard);
        });
        viewModel.roundFinished.observe(this, roundFinished -> {
            dismissDialogs();
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
     * Button onClick Event Methods
     * ****************************************************************************
     */

    public void onFoldClick(View view) {
        viewModel.onPlayerAction("FOLD");
    }

    public void onRaiseClick(View view) {
        dismissDialogs();
        double funds = 1000d; //todo: get current player funds
        double minimumBet = 10d; //todo: get minimum bet
        betRaisePokerGameDialog = BetRaisePokerGameDialog.newInstance(ActionType.RAISE, funds, minimumBet, this);
        betRaisePokerGameDialog.show(getSupportFragmentManager());
    }

    public void onBetClick(View view) {
        dismissDialogs();
        double funds = 1000d; //todo: get current player funds
        double minimumBet = 10d; //todo: get current minimum bet
        betRaisePokerGameDialog = BetRaisePokerGameDialog.newInstance(ActionType.BET, funds, minimumBet, this);
        betRaisePokerGameDialog.show(getSupportFragmentManager());
    }

    public void onCallClick(View view) {
        viewModel.onPlayerAction("CALL");
    }

    public void onCheckClick(View view) {
        viewModel.onPlayerAction("CHECK");
    }

    /*
     * Modal Callbacks
     * ****************************************************************************
     */

    @Override
    public void onBetSelected(ActionType type, double betAmount) {
        switch (type) {
            case BET: {
                viewModel.onPlayerAction("BET", betAmount);
                break;
            }
            case RAISE: {
                viewModel.onPlayerAction("RAISE", betAmount);
                break;
            }
        }
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

    private void dismissDialogs() {
        DialogHelper.dismiss(loadingSpinner);
        if (betRaisePokerGameDialog != null) {
            betRaisePokerGameDialog.dismissAllowingStateLoss();
            betRaisePokerGameDialog = null;
        }
    }
}
