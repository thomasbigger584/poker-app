package com.twb.pokerapp.ui.activity.game.texas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.ActivityGameTexasBinding;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TexasGameActivity extends BaseAuthActivity implements BetRaiseGameDialog.BetRaiseClickListener {
    private static final String TAG = TexasGameActivity.class.getSimpleName();
    private static final String KEY_CONNECTION_TYPE = "CONNECTION_TYPE";
    private static final String KEY_BUY_IN_AMOUNT = "BUY_IN_AMOUNT";

    @Inject
    AuthService authService;

    private ActivityGameTexasBinding binding;
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
    protected View getContentView() {
        binding = ActivityGameTexasBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!initIncomingData()) return;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadingSpinner = DialogHelper.createLoadingSpinner(this);
        DialogHelper.show(loadingSpinner);

        tableController = new TableController(binding);
        controlsController = new ControlsController(binding);

        initClickListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onLeaveTable();
            }
        });

        var layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.chatBoxRecyclerView.setLayoutManager(layoutManager);

        chatBoxAdapter = new ChatBoxRecyclerAdapter();
        binding.chatBoxRecyclerView.setAdapter(chatBoxAdapter);
        chatBoxAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.chatBoxRecyclerView.smoothScrollToPosition(chatBoxAdapter.getItemCount() - 1);
            }
        });

        viewModel = new ViewModelProvider(this).get(TexasGameViewModel.class);
        viewModel.errors.observe(this, throwable -> {
            if (throwable == null) return;
            DialogHelper.dismiss(loadingSpinner);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, throwable.getMessage(), null);
            var prev = getSupportFragmentManager().findFragmentByTag("error_modal");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "error_modal");
            } else {
                Log.d("DEBUG", "Dialog error_modal already visible!");
            }
            chatBoxAdapter.add(throwable.getMessage());
        });
        viewModel.closedConnection.observe(this, aVoid -> {
            dismissDialogs();
            var message = getString(R.string.lost_connection);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.ERROR, message, new FinishActivityOnClickListener(this));
            var prev = getSupportFragmentManager().findFragmentByTag("closed_connection_modal");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "closed_connection_modal");
            } else {
                Log.d("DEBUG", "Dialog closed_connection_modal already visible!");
            }
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
            chatBoxAdapter.add(getString(R.string.connected_format, currentUsername));
            dismissDialogs();
        });
        viewModel.playerConnected.observe(this, playerConnected -> {
            var playerSession = playerConnected.getPlayerSession();
            if (!authService.isCurrentUser(playerSession.getUser())) {
                tableController.connectOtherPlayer(playerSession);
                chatBoxAdapter.add(getString(R.string.connected_format, playerSession.getUser().getUsername()));
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
                controlsController.show(playerTurn);
                currentPlayerTurn = playerTurn;
            } else {
                controlsController.hide();
                currentPlayerTurn = null;
            }
        });
        viewModel.playerActioned.observe(this, playerActioned -> {
            dismissDialogs();

            var playerSession = playerActioned.getAction().getPlayerSession();
            tableController.updateDetails(playerSession);

            tableController.hidePlayerTurns();
            controlsController.hide();

            chatBoxAdapter.add(getPlayerActionedMessage(playerActioned));
        });
        viewModel.bettingRoundUpdated.observe(this, bettingRoundUpdated -> {
            tableController.updateBettingRound(bettingRoundUpdated);
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
            tableController.update(roundFinished);
        });
        viewModel.gameFinished.observe(this, gameFinished -> {
            var clickListener = new FinishActivityOnClickListener(this);
            var alertModalDialog = AlertModalDialog
                    .newInstance(AlertModalDialog.AlertModalType.INFO, getString(R.string.game_finished), clickListener);
            var prev = getSupportFragmentManager().findFragmentByTag("game_finished_modal");
            if (prev == null) {
                alertModalDialog.show(getSupportFragmentManager(), "game_finished_modal");
            } else {
                Log.d("DEBUG", "Dialog game_finished_modal already visible!");
            }
            chatBoxAdapter.add(getString(R.string.game_finished));
        });
        viewModel.chatMessage.observe(this, chatMessage -> {
            var user = chatMessage.getUsername();
            if (chatMessage.getUsername().equals(authService.getCurrentUser())) {
               user = "You";
            }
            chatBoxAdapter.add(getString(R.string.chat_message_format, user, chatMessage.getMessage()));
        });
        viewModel.logMessage.observe(this, logMessage -> {
            chatBoxAdapter.add(logMessage.getMessage());
        });
        viewModel.errorMessage.observe(this, errorMessage -> {
            handleErrorMessage(errorMessage.getMessage());
        });
        viewModel.validationMessage.observe(this, validation -> {
            Log.w(TAG, "VALIDATION: Invalid PlayerAction Request: " + validation.toString());
            Toast.makeText(this, getString(R.string.invalid_player_action), Toast.LENGTH_SHORT).show();
        });
        viewModel.playerDisconnected.observe(this, playerDisconnected -> {
            var username = playerDisconnected.getUsername();
            chatBoxAdapter.add(getString(R.string.disconnected_format, username));
            var current = authService.getCurrentUser();
            if (username.equals(current)) {
                finish();
            } else {
                tableController.disconnectOtherPlayer(username);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showCurrentWidth();
    }

    private void showCurrentWidth() {
        var width = getResources().getConfiguration().screenWidthDp;
        var msg = "Width: " + width + "dp";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void initClickListeners() {
        binding.foldButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.FOLD);
        });
        binding.checkButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.CHECK);
        });
        binding.betButton.setOnClickListener(v -> onBetClick());
        binding.callButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.CALL);
        });
        binding.raiseButton.setOnClickListener(v -> onRaiseClick());
        binding.allInButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.ALL_IN);
        });
        binding.chatBoxRecyclerView.setOnClickListener(v -> onChatClick());
        binding.chatButton.setOnClickListener(v -> onChatClick());
        binding.menuButton.setOnClickListener(this::onMenuClick);
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

    private void onBetClick() {
        dismissDialogs();
        var minimumBet = 10d;
        var maximumBet = tableController.getPlayerCardPairLayout().getPlayerSession().getFunds();
        betRaisePokerGameDialog = BetRaiseGameDialog.newInstance(ActionType.BET, maximumBet, minimumBet, this);
        var prev = getSupportFragmentManager().findFragmentByTag("bet_dialog");
        if (prev == null) {
            betRaisePokerGameDialog.show(getSupportFragmentManager(), "bet_dialog");
        } else {
            Log.d("DEBUG", "Dialog bet_dialog already visible!");
        }
    }

    private void onRaiseClick() {
        dismissDialogs();
        var minimumBet = getRaiseMinimumBet();
        betRaisePokerGameDialog = BetRaiseGameDialog.newInstance(ActionType.RAISE, buyInAmount, minimumBet, this);
        var prev = getSupportFragmentManager().findFragmentByTag("raise_dialog");
        if (prev == null) {
            betRaisePokerGameDialog.show(getSupportFragmentManager(), "raise_dialog");
        } else {
            Log.d("DEBUG", "Dialog raise_dialog already visible!");
        }
    }

    private void onChatClick() {
        var builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.send_chat_message);

        var input = new android.widget.EditText(this);
        input.setHint(R.string.enter_message);
        builder.setView(input);

        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            var message = input.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendChatMessage(message);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void onMenuClick(View view) {
        var popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.game_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_leave_table) {
            onLeaveTable();
            return true;
        }
        return false;
    }

    private void onLeaveTable() {
        var listener = new FinishActivityOnClickListener(this);
        var dialog = AlertModalDialog.newInstance(AlertModalDialog.AlertModalType.CONFIRM,
                        getString(R.string.leave_table_confirm), listener);
        var prev = getSupportFragmentManager().findFragmentByTag("leave_table_modal");
        if (prev == null) {
            dialog.show(getSupportFragmentManager(), "leave_table_modal");
        } else {
            Log.d("DEBUG", "Dialog leave_table_modal already visible!");
        }
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
            stringBuilderList.add(getString(R.string.player_action_you));
        } else {
            stringBuilderList.add(user.getUsername());
        }
        stringBuilderList.add(playerAction.getActionType().toLowerCase().replace("_", " "));
        var amount = playerAction.getAmount();
        if (amount != null && amount > 0d) {
            stringBuilderList.add(getString(R.string.player_action_with));
            stringBuilderList.add(getString(R.string.currency_format, playerAction.getAmount()));
        }
        return String.join(" ", stringBuilderList);
    }

    private void handleErrorMessage(String message) {
        DialogHelper.dismiss(loadingSpinner);
        var clickListener = new FinishActivityOnClickListener(this);
        var alertModalDialog = AlertModalDialog
                .newInstance(AlertModalDialog.AlertModalType.ERROR, message, clickListener);
        var prev = getSupportFragmentManager().findFragmentByTag("error_modal");
        if (prev == null) {
            alertModalDialog.show(getSupportFragmentManager(), "error_modal");
        } else {
            Log.d("DEBUG", "Dialog error_modal already visible!");
        }
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
