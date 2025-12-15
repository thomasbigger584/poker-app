package com.twb.pokerapp.ui.activity.game.texas;

import android.app.Activity;
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
import com.twb.pokerapp.data.model.dto.table.TableDTO;
import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerActionedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;
import com.twb.pokerapp.ui.activity.login.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.game.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokerapp.ui.activity.game.texas.controller.ControlsController;
import com.twb.pokerapp.ui.activity.game.texas.controller.TableController;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.game.BaseGameDialog;
import com.twb.pokerapp.ui.dialog.game.BetRaiseGameDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import com.twb.pokerapp.ui.dialog.FinishActivityOnClickListener;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TexasGameActivity extends BaseAuthActivity implements BetRaiseGameDialog.BetRaiseClickListener {
    private static final String TAG = TexasGameActivity.class.getSimpleName();
    private static final String MODAL_TAG = "modal_alert";
    private static final String KEY_CONNECTION_TYPE = "CONNECTION_TYPE";
    private static final String KEY_BUY_IN_AMOUNT = "BUY_IN_AMOUNT";

    @Inject
    AuthService authService;

    private TexasGameViewModel viewModel;
    private TableDTO table;
    private AlertDialog loadingSpinner;
    private BaseGameDialog betRaisePokerGameDialog;
    private ChatBoxRecyclerAdapter chatBoxAdapter;
    private TableController tableController;
    private ControlsController controlsController;
    private String connectionType;
    private Double buyInAmount;

    private PlayerTurnDTO currentPlayerTurn;

    public static void startActivity(Activity activity, TableDTO table, String connectionType, Double buyInAmount) {
        var intent = new Intent(activity, TexasGameActivity.class);
        intent.putExtras(table.toBundle());
        intent.putExtra(KEY_CONNECTION_TYPE, connectionType);
        intent.putExtra(KEY_BUY_IN_AMOUNT, buyInAmount);
        activity.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_game_texas;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!initIncomingData()) return;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadingSpinner = DialogHelper.createLoadingSpinner(this);
        DialogHelper.show(loadingSpinner);

        tableController = new TableController(this);
        controlsController = new ControlsController(this);

        var chatBoxRecyclerView = (RecyclerView) findViewById(R.id.chatBoxRecyclerView);
        var layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatBoxRecyclerView.setLayoutManager(layoutManager);

        chatBoxAdapter = new ChatBoxRecyclerAdapter(layoutManager);
        chatBoxRecyclerView.setAdapter(chatBoxAdapter);

        viewModel = new ViewModelProvider(this).get(TexasGameViewModel.class);
        viewModel.errors.observe(this, throwable -> {
            if (throwable == null) return;
            DialogHelper.dismiss(loadingSpinner);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), null);
            alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
            chatBoxAdapter.add(throwable.getMessage());
        });
        viewModel.closedConnection.observe(this, aVoid -> {
            dismissDialogs();
            var message = "Lost connection with server";
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, message, new FinishActivityOnClickListener(this));
            alertModalDialog.show(getSupportFragmentManager(), MODAL_TAG);
            chatBoxAdapter.add(message);
        });
        viewModel.playerSubscribed.observe(this, playerSubscribed -> {
            var currentUsername = authService.getCurrentUser();
            var currentPlayerSession =
                    playerSubscribed.getCurrentPlayerSession(currentUsername);
            tableController.connectCurrentPlayer(currentPlayerSession);
            for (var playerSession : playerSubscribed.getPlayerSessions()) {
                if (!playerSession.getUser().getUsername().equals(currentUsername)) {
                    tableController.connectOtherPlayer(playerSession);
                }
            }
            chatBoxAdapter.add("Connected: " + currentUsername);
            dismissDialogs();
        });
        viewModel.playerConnected.observe(this, playerConnected -> {
            var playerSession = playerConnected.getPlayerSession();
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
            var playerSession = dealPlayerCard.getPlayerSession();
            if (authService.isCurrentUser(playerSession.getUser())) {
                tableController.dealCurrentPlayerCard(dealPlayerCard);
            } else {
                tableController.dealOtherPlayerCard(dealPlayerCard);
            }
        });
        viewModel.playerTurn.observe(this, playerTurn -> {
            var playerSession = playerTurn.getPlayerSession();
            tableController.updatePlayerTurn(playerSession);
            if (authService.isCurrentUser(playerSession.getUser())) {
                controlsController.show(playerTurn.getNextActions());
                currentPlayerTurn = playerTurn;
            } else {
                controlsController.hide();
                currentPlayerTurn = null;
            }
        });
        viewModel.playerActioned.observe(this, playerActioned -> {
            dismissDialogs();

            var playerSession =
                    playerActioned.getAction().getPlayerSession();
            tableController.updateDetails(playerSession);

            controlsController.hide();
            chatBoxAdapter.add(getPlayerActionedMessage(playerActioned));
        });
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
            var clickListener = new FinishActivityOnClickListener(this);
            var alertModalDialog = AlertModalDialog
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
        viewModel.validationMessage.observe(this, validation -> {
            Log.w(TAG, "VALIDATION: Invalid PlayerAction Request: " + validation.toString());
            Toast.makeText(this, "Invalid PlayerAction Request", Toast.LENGTH_SHORT).show();
        });
        viewModel.playerDisconnected.observe(this, playerDisconnected -> {
            var username = playerDisconnected.getUsername();
            chatBoxAdapter.add("Disconnected: " + username);
            var current = authService.getCurrentUser();
            if (username.equals(current)) {
                finish();
            } else {
                tableController.disconnectOtherPlayer(username);
            }
        });
    }



    @Override
    protected void onAuthorized() {
        viewModel.connect(table.getId(), connectionType, buyInAmount);
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
        viewModel.onPlayerAction(ActionType.FOLD);
    }

    public void onCheckClick(View view) {
        viewModel.onPlayerAction(ActionType.CHECK);
    }

    public void onBetClick(View view) {
        dismissDialogs();
        var minimumBet = 10d;
        betRaisePokerGameDialog = BetRaiseGameDialog.newInstance(ActionType.BET, buyInAmount, minimumBet, this);
        betRaisePokerGameDialog.show(getSupportFragmentManager(), "bet_dialog");
    }

    public void onCallClick(View view) {
        viewModel.onPlayerAction(ActionType.CALL);
    }

    public void onRaiseClick(View view) {
        dismissDialogs();
        var minimumBet = getRaiseMinimumBet();
        betRaisePokerGameDialog = BetRaiseGameDialog.newInstance(ActionType.RAISE, buyInAmount, minimumBet, this);
        betRaisePokerGameDialog.show(getSupportFragmentManager(), "raise_dialog");
    }

    /*
     * Modal Callbacks
     * ****************************************************************************
     */

    @Override
    public void onBetSelected(ActionType actionType, double betAmount) {
        viewModel.onPlayerAction(actionType, betAmount);
    }

    /*
     * Helper Methods
     * ****************************************************************************
     */

    private boolean initIncomingData() {
        var intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            return false;
        }
        var extras = intent.getExtras();
        if (extras == null) {
            Toast.makeText(this, "Bundle extras is null", Toast.LENGTH_SHORT).show();
            return false;
        }
        table = TableDTO.fromBundle(extras);
        connectionType = intent.getStringExtra(KEY_CONNECTION_TYPE);
        buyInAmount = intent.getDoubleExtra(KEY_BUY_IN_AMOUNT, 0d);
        return true;
    }

    private double getRaiseMinimumBet() {
        if (currentPlayerTurn != null) {
            var amountToCall = currentPlayerTurn.getAmountToCall();
            if (amountToCall != null) {
                return amountToCall + 0.01;
            }
        }
        return 10d;
    }

    private String getPlayerActionedMessage(PlayerActionedDTO playerActioned) {
        var playerAction = playerActioned.getAction();
        var user = playerAction.getPlayerSession().getUser();
        var stringBuilderList = new ArrayList<String>();
        if (authService.isCurrentUser(user)) {
            stringBuilderList.add("You");
        } else {
            stringBuilderList.add(user.getUsername());
        }
        stringBuilderList.add(playerAction.getActionType().toLowerCase());
        var amount = playerAction.getAmount();
        if (amount != null && amount > 0d) {
            stringBuilderList.add("with");
            stringBuilderList.add(String.format(Locale.getDefault(), "%.2f", playerAction.getAmount()));
        }
        var bettingRound = playerAction.getBettingRound();
        var bettingRoundPot = bettingRound.getPot();
        if (bettingRoundPot != null && bettingRoundPot > 0) {
            stringBuilderList.add(String.format(Locale.getDefault(), "(%.2f)", bettingRoundPot));
        }
        return String.join(" ", stringBuilderList);
    }

    private void handleErrorMessage(String message) {
        DialogHelper.dismiss(loadingSpinner);
        var clickListener = new FinishActivityOnClickListener(this);
        var alertModalDialog = AlertModalDialog
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
