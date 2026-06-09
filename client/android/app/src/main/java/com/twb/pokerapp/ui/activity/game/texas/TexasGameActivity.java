package com.twb.pokerapp.ui.activity.game.texas;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.repository.RepositoryCallback;
import com.twb.pokerapp.proto.ActionType;
import com.twb.pokerapp.proto.AppUserDTO;
import com.twb.pokerapp.proto.BettingRoundUpdatedDTO;
import com.twb.pokerapp.proto.ChatMessageDTO;
import com.twb.pokerapp.proto.DealCommunityCardDTO;
import com.twb.pokerapp.proto.DealPlayerCardDTO;
import com.twb.pokerapp.proto.DealerDeterminedDTO;
import com.twb.pokerapp.proto.GameFinishedDTO;
import com.twb.pokerapp.proto.LogMessageDTO;
import com.twb.pokerapp.proto.PlayerActionedDTO;
import com.twb.pokerapp.proto.PlayerConnectedDTO;
import com.twb.pokerapp.proto.PlayerDisconnectedDTO;
import com.twb.pokerapp.proto.PlayerSessionDTO;
import com.twb.pokerapp.proto.PlayerSubscribedDTO;
import com.twb.pokerapp.proto.PlayerTurnDTO;
import com.twb.pokerapp.proto.RoundFinishedDTO;
import com.twb.pokerapp.proto.RoundStateDTO;
import com.twb.pokerapp.proto.ServerMessageDTO;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.proto.ValidationDTO;
import com.twb.pokerapp.databinding.ActivityGameTexasBinding;
import com.twb.pokerapp.service.WebSocketService;
import com.twb.pokerapp.ui.activity.base.BaseAuthActivity;
import com.twb.pokerapp.ui.activity.game.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokerapp.ui.activity.game.texas.controller.ControlsController;
import com.twb.pokerapp.ui.activity.game.texas.controller.TableController;
import com.twb.pokerapp.ui.dialog.AlertModalDialog;
import com.twb.pokerapp.ui.dialog.DialogHelper;
import com.twb.pokerapp.ui.dialog.FinishActivityOnClickListener;
import com.twb.pokerapp.ui.dialog.game.BaseGameDialog;
import com.twb.pokerapp.ui.dialog.game.BetRaiseGameDialog;
import com.twb.pokerapp.ui.dialog.game.BotPickerDialog;
import com.twb.pokerapp.ui.util.HapticUtil;
import com.twb.pokerapp.util.Protos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TexasGameActivity extends BaseAuthActivity implements BetRaiseGameDialog.BetRaiseClickListener {
    private static final String TAG = TexasGameActivity.class.getSimpleName();
    private static final String KEY_TABLE = "TABLE";
    private static final String KEY_CONNECTION_TYPE = "CONNECTION_TYPE";
    private static final String KEY_BUY_IN_AMOUNT = "BUY_IN_AMOUNT";
    private static final String KEY_RECONNECT = "RECONNECT";

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
    private boolean reconnect;

    private long lastRenderedTimestamp = 0L;
    private boolean hasEverConnected = false;
    private boolean connectionLostShown = false;
    // Identifies the turn we last vibrated for, so re-rendering the same turn (onResume,
    // reconnect snapshot) doesn't buzz the player repeatedly.
    private String lastVibratedTurnKey = null;

    public static void startActivity(Activity activity, TableDTO table, String connectionType, Double buyInAmount) {
        startActivity(activity, table, connectionType, buyInAmount, false);
    }

    /**
     * @param reconnect true when resuming an existing seat (e.g. the "Reconnect" button on the
     *                  table list). The server resumes the seat as-is; if the grace window already
     *                  expired it returns an error instead of buying the player back in.
     */
    public static void startActivity(Activity activity, TableDTO table, String connectionType, Double buyInAmount, boolean reconnect) {
        var intent = new Intent(activity, TexasGameActivity.class);
        intent.putExtra(KEY_TABLE, table.toByteArray());
        intent.putExtra(KEY_CONNECTION_TYPE, connectionType);
        intent.putExtra(KEY_BUY_IN_AMOUNT, buyInAmount);
        intent.putExtra(KEY_RECONNECT, reconnect);
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
        enableImmersiveMode();

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
        viewModel.setTableId(UUID.fromString(table.getId()));
        viewModel.messages.observe(this, this::onMessagesReceived);
        viewModel.connected.observe(this, connected -> {
            var isConnected = Boolean.TRUE.equals(connected);
            if (isConnected) {
                DialogHelper.dismiss(loadingSpinner);
                if (connectionLostShown) {
                    chatBoxAdapter.add(getString(R.string.reconnected));
                }
                hasEverConnected = true;
                connectionLostShown = false;
            } else if (hasEverConnected && !connectionLostShown) {
                chatBoxAdapter.add(getString(R.string.connection_lost_reconnecting));
                connectionLostShown = true;
            }
        });
        viewModel.errors.observe(this, throwable -> {
            if (throwable != null) {
                handleErrorMessage(throwable.getMessage());
            }
        });
    }

    /**
     * Hides the status and navigation bars for a fully immersive table. The bars stay hidden and
     * only slide in transiently on an edge swipe (then auto-hide), so the back button / task
     * switcher remain reachable without cluttering the table.
     */
    private void enableImmersiveMode() {
        var window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        var controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Re-hide the bars after losing focus (dialogs, notification shade, app switch) brings
        // them back.
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        var turn = viewModel.getCurrentPlayerTurn();
        var timestamp = viewModel.getCurrentPlayerTurnTimestamp();
        if (turn != null) {
            handlePlayerTurn(turn, timestamp);
        }
    }

    private void onMessagesReceived(List<ServerMessageDTO> messages) {
        if (messages == null) return;
        for (var message : messages) {
            if (message.getTimestamp() > lastRenderedTimestamp) {
                dispatchMessage(message);
                lastRenderedTimestamp = message.getTimestamp();
            }
        }
    }

    private void dispatchMessage(ServerMessageDTO message) {
        switch (message.getPayloadCase()) {
            case PLAYER_SUBSCRIBED:
                handlePlayerSubscribed(message.getPlayerSubscribed());
                break;
            case PLAYER_CONNECTED:
                handlePlayerConnected(message.getPlayerConnected());
                break;
            case DEALER_DETERMINED:
                handleDealerDetermined(message.getDealerDetermined());
                break;
            case DEAL_INIT:
                handleDealPlayerCard(message.getDealInit());
                break;
            case DEAL_COMMUNITY:
                handleDealCommunityCard(message.getDealCommunity());
                break;
            case PLAYER_TURN:
                handlePlayerTurn(message.getPlayerTurn(), message.getTimestamp());
                break;
            case PLAYER_ACTIONED:
                handlePlayerActioned(message.getPlayerActioned());
                break;
            case BETTING_ROUND_UPDATED:
                handleBettingRoundUpdated(message.getBettingRoundUpdated());
                break;
            case ROUND_FINISHED:
                handleRoundFinished(message.getRoundFinished());
                break;
            case GAME_FINISHED:
                handleGameFinished(message.getGameFinished());
                break;
            case CHAT:
                handleChatMessage(message.getChat());
                break;
            case LOG:
                handleLogMessage(message.getLog());
                break;
            case ERROR:
                handleErrorMessage(message.getError().getMessage());
                break;
            case VALIDATION:
                handleValidationMessage(message.getValidation());
                break;
            case PLAYER_DISCONNECTED:
                handlePlayerDisconnected(message.getPlayerDisconnected());
                break;
            default:
                break;
        }
    }

    private void handlePlayerSubscribed(PlayerSubscribedDTO playerSubscribed) {
        clearCurrentPlayerTurn();
        var currentUsername = authService.getCurrentUser();
        var currentPlayerSession = Protos.currentPlayerSession(playerSubscribed, currentUsername);
        tableController.connectCurrentPlayer(currentPlayerSession);
        for (var playerSession : playerSubscribed.getPlayerSessionsList()) {
            if (!playerSession.getUser().getUsername().equals(currentUsername)) {
                tableController.connectOtherPlayer(playerSession);
            }
        }
        chatBoxAdapter.add(getString(R.string.connected_format, currentUsername));
        dismissDialogs();

        // Resume an in-progress hand (reconnect / app restart mid-round) where it left off.
        if (playerSubscribed.hasRoundState()) {
            renderRoundState(playerSubscribed.getRoundState());
        }
    }

    private void renderRoundState(RoundStateDTO roundState) {
        // Clear transient state first so re-applying a snapshot (e.g. a reconnect while the table
        // was still rendered) doesn't stack duplicate board cards or leave stale turn highlights.
        tableController.resetCommunityCards();
        tableController.hidePlayerTurns();
        controlsController.hide();

        if (roundState.hasDealer()) {
            tableController.dealerDetermined(roundState.getDealer());
        }

        // Hole cards: own cards face-up, opponents' face-down (same rule as live dealing).
        for (var dealtCard : roundState.getPlayerCardsList()) {
            var playerSession = dealtCard.getPlayerSession();
            if (authService.isCurrentUser(playerSession.getUser())) {
                tableController.dealCurrentPlayerCard(dealtCard);
            } else {
                tableController.dealOtherPlayerCard(dealtCard);
            }
        }

        for (var communityCard : roundState.getCommunityCardsList()) {
            var dealCommunityCard = DealCommunityCardDTO.newBuilder()
                    .setCard(communityCard)
                    .build();
            tableController.dealCommunityCard(dealCommunityCard);
        }

        if (roundState.hasBettingRound() || !roundState.getRoundPotsList().isEmpty()) {
            var bettingRoundUpdated = BettingRoundUpdatedDTO.newBuilder()
                    .setRound(roundState.getRound())
                    .setBettingRound(roundState.getBettingRound())
                    .addAllRoundPots(roundState.getRoundPotsList())
                    .build();
            tableController.updateBettingRound(bettingRoundUpdated);
        }

        for (var foldedPlayer : roundState.getFoldedPlayersList()) {
            tableController.foldPlayer(foldedPlayer);
        }

        // Render the live turn last so action buttons / countdown sit on top of the restored table.
        if (roundState.hasCurrentTurn()) {
            handlePlayerTurn(roundState.getCurrentTurn(), System.currentTimeMillis());
        }
    }

    private void handlePlayerConnected(PlayerConnectedDTO playerConnected) {
        var playerSession = playerConnected.getPlayerSession();
        if (!authService.isCurrentUser(playerSession.getUser())) {
            tableController.connectOtherPlayer(playerSession);
            chatBoxAdapter.add(getString(R.string.connected_format, playerSession.getUser().getUsername()));
        }
    }

    private void handleDealerDetermined(DealerDeterminedDTO dealerDetermined) {
        dismissDialogs();
        clearCurrentPlayerTurn();
        tableController.dealerDetermined(dealerDetermined.getPlayerSession());
    }

    private void handleDealPlayerCard(DealPlayerCardDTO dealPlayerCard) {
        dismissDialogs();
        clearCurrentPlayerTurn();
        tableController.hidePlayerTurns();
        controlsController.hide();
        var playerSession = dealPlayerCard.getPlayerSession();
        if (authService.isCurrentUser(playerSession.getUser())) {
            tableController.dealCurrentPlayerCard(dealPlayerCard);
        } else {
            tableController.dealOtherPlayerCard(dealPlayerCard);
        }
    }

    private void handlePlayerTurn(PlayerTurnDTO playerTurn, long messageTimestamp) {
        var playerSession = playerTurn.getPlayerSession();
        tableController.updatePlayerTurn(playerSession);
        if (authService.isCurrentUser(playerSession.getUser())) {
            controlsController.show(playerTurn, messageTimestamp);
            buzzForOwnTurn(playerSession, messageTimestamp);
        } else {
            controlsController.hide();
        }
    }

    /**
     * Vibrate to alert the player it's their turn — but only once per turn. The same turn can be
     * re-delivered (onResume, reconnect snapshot), so we key on the session + timestamp and skip
     * repeats.
     */
    private void buzzForOwnTurn(PlayerSessionDTO playerSession, long messageTimestamp) {
        var turnKey = playerSession.getId() + "@" + messageTimestamp;
        if (turnKey.equals(lastVibratedTurnKey)) {
            return;
        }
        lastVibratedTurnKey = turnKey;
        HapticUtil.yourTurn(this);
    }

    private void handlePlayerActioned(PlayerActionedDTO playerActioned) {
        dismissDialogs();
        clearCurrentPlayerTurn();
        var action = playerActioned.getAction();
        var playerSession = action.getPlayerSession();
        tableController.updateDetails(playerSession);
        if (action.getActionType() == ActionType.ACTION_TYPE_FOLD) {
            tableController.foldPlayer(playerSession);
        }
        tableController.hidePlayerTurns();
        controlsController.hide();
        chatBoxAdapter.add(getPlayerActionedMessage(playerActioned));
    }

    private void handleBettingRoundUpdated(BettingRoundUpdatedDTO bettingRoundUpdated) {
        tableController.updateBettingRound(bettingRoundUpdated);
    }

    private void handleDealCommunityCard(DealCommunityCardDTO dealCommunityCard) {
        dismissDialogs();
        clearCurrentPlayerTurn();
        controlsController.hide();
        tableController.dealCommunityCard(dealCommunityCard);
    }

    private void handleRoundFinished(RoundFinishedDTO roundFinished) {
        dismissDialogs();
        clearCurrentPlayerTurn();
        tableController.hidePlayerTurns();
        controlsController.hide();
        tableController.update(roundFinished);
    }

    private void handleGameFinished(GameFinishedDTO gameFinished) {
        clearCurrentPlayerTurn();
        var clickListener = new FinishActivityOnClickListener(this);
        var alertModalDialog = AlertModalDialog.newInstance(AlertModalDialog.AlertModalType.INFO, getString(R.string.game_finished), clickListener);
        alertModalDialog.show(getSupportFragmentManager(), "game_finished_modal");
        chatBoxAdapter.add(getString(R.string.game_finished));
    }

    private void handleChatMessage(ChatMessageDTO chatMessage) {
        var user = chatMessage.getUsername();
        if (chatMessage.getUsername().equals(authService.getCurrentUser())) {
            user = "You";
        }
        chatBoxAdapter.add(getString(R.string.chat_message_format, user, chatMessage.getMessage()));
    }

    private void handleLogMessage(LogMessageDTO logMessage) {
        chatBoxAdapter.add(logMessage.getMessage());
    }

    private void handleValidationMessage(ValidationDTO validation) {
        for (var field : validation.getFieldsList()) {
            chatBoxAdapter.add(field.getMessage());
        }
    }

    private void handlePlayerDisconnected(PlayerDisconnectedDTO playerDisconnected) {
        var username = playerDisconnected.getUsername();
        chatBoxAdapter.add(getString(R.string.disconnected_format, username));
        if (username.equals(authService.getCurrentUser())) {
            finish();
        } else {
            tableController.disconnectOtherPlayer(username);
        }
    }

    private void initClickListeners() {
        binding.foldButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.ACTION_TYPE_FOLD);
        });
        binding.checkButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.ACTION_TYPE_CHECK);
        });
        binding.betButton.setOnClickListener(v -> onBetClick());
        binding.callButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.ACTION_TYPE_CALL);
        });
        binding.raiseButton.setOnClickListener(v -> onRaiseClick());
        binding.allInButton.setOnClickListener(v -> {
            dismissDialogs();
            viewModel.onPlayerAction(ActionType.ACTION_TYPE_ALL_IN);
        });
        binding.chatBoxRecyclerView.setOnClickListener(v -> onChatClick());
        binding.chatButton.setOnClickListener(v -> onChatClick());
        binding.menuButton.setOnClickListener(this::onMenuClick);
    }

    @Override
    protected void onAuthorized() {
        var serviceIntent = new Intent(this, WebSocketService.class);
        serviceIntent.setAction(WebSocketService.ACTION_START);
        serviceIntent.putExtra(WebSocketService.EXTRA_TABLE_ID, UUID.fromString(table.getId()));
        serviceIntent.putExtra(WebSocketService.EXTRA_CONNECTION_TYPE, connectionType);
        serviceIntent.putExtra(WebSocketService.EXTRA_BUY_IN_AMOUNT, buyInAmount);
        serviceIntent.putExtra(WebSocketService.EXTRA_RECONNECT, reconnect);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onNotAuthorized(String message, @Nullable Throwable throwable) {
        handleErrorMessage(message);
    }

    @Override
    protected void onDestroy() {
        // Only tear the background connection down when the user is genuinely leaving the table.
        // A non-finishing destroy (config change, or the OS reclaiming the activity while the app
        // is backgrounded) must keep the foreground service alive so we keep receiving updates.
        if (isFinishing()) {
            var serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction(WebSocketService.ACTION_STOP);
            startService(serviceIntent);
        }
        super.onDestroy();
    }

    /*
     * Button onClick Event Methods
     * ****************************************************************************
     */

    private void onBetClick() {
        dismissDialogs();
        var maximumBet = Protos.money(tableController.getPlayerCardPairLayout().getPlayerSession().getFunds());
        var minimumBet = Math.min(10d, maximumBet);
        betRaisePokerGameDialog = BetRaiseGameDialog.newInstance(ActionType.ACTION_TYPE_BET, maximumBet, minimumBet, this);
        var prev = getSupportFragmentManager().findFragmentByTag("bet_dialog");
        if (prev == null) {
            betRaisePokerGameDialog.show(getSupportFragmentManager(), "bet_dialog");
        } else {
            Log.d("DEBUG", "Dialog bet_dialog already visible!");
        }
    }

    private void onRaiseClick() {
        dismissDialogs();
        var maximumBet = Protos.money(tableController.getPlayerCardPairLayout().getPlayerSession().getFunds());
        var minimumBet = Math.min(getRaiseMinimumBet(), maximumBet);
        betRaisePokerGameDialog = BetRaiseGameDialog.newInstance(ActionType.ACTION_TYPE_RAISE, maximumBet, minimumBet, this);
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
        var itemId = item.getItemId();
        if (itemId == R.id.action_leave_table) {
            onLeaveTable();
            return true;
        } else if (itemId == R.id.action_add_bot) {
            onAddBotClick();
            return true;
        } else if (itemId == R.id.action_show_current_width) {
            var width = getResources().getConfiguration().screenWidthDp;
            Toast.makeText(this, "Width: " + width + "dp", Toast.LENGTH_SHORT).show();
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

    private void onAddBotClick() {
        viewModel.getBots(new RepositoryCallback<>() {
            @Override
            public void onSuccess(List<AppUserDTO> bots) {
                var availableBots = filterSeatedBots(bots);
                if (availableBots.isEmpty()) {
                    Toast.makeText(TexasGameActivity.this, R.string.no_bots_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                showBotPickerDialog(availableBots);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(TexasGameActivity.this, R.string.failed_to_load_bots, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<AppUserDTO> filterSeatedBots(List<AppUserDTO> bots) {
        var availableBots = new ArrayList<AppUserDTO>();
        if (bots == null) {
            return availableBots;
        }
        var seatedUsernames = tableController.getSeatedUsernames();
        for (var bot : bots) {
            if (!seatedUsernames.contains(bot.getUsername())) {
                availableBots.add(bot);
            }
        }
        return availableBots;
    }

    private void showBotPickerDialog(List<AppUserDTO> bots) {
        if (getSupportFragmentManager().findFragmentByTag("bot_picker") != null) {
            return;
        }
        var dialog = BotPickerDialog.newInstance(bots, bot -> {
            viewModel.sendBotConnection(bot.getId(), getBotBuyInAmount());
            chatBoxAdapter.add(getString(R.string.bot_added_format, bot.getUsername()));
        });
        dialog.show(getSupportFragmentManager(), "bot_picker");
    }

    // todo: this could potentially be moved to server as a default if provided null
    private double getBotBuyInAmount() {
        var min = Protos.money(table.getMinBuyin());
        var max = Protos.money(table.getMaxBuyin());
        var amount = (buyInAmount != null) ? buyInAmount : 0d;
        if (amount < min) {
            amount = min;
        }
        if (amount > max) {
            amount = max;
        }
        return amount;
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
        var tableBytes = intent.getByteArrayExtra(KEY_TABLE);
        if (tableBytes == null) {
            Toast.makeText(this, "Table extra is null", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            table = TableDTO.parseFrom(tableBytes);
        } catch (InvalidProtocolBufferException e) {
            Toast.makeText(this, "Failed to parse table", Toast.LENGTH_SHORT).show();
            return false;
        }
        connectionType = intent.getStringExtra(KEY_CONNECTION_TYPE);
        buyInAmount = intent.getDoubleExtra(KEY_BUY_IN_AMOUNT, 0d);
        reconnect = intent.getBooleanExtra(KEY_RECONNECT, false);
        return true;
    }

    private double getRaiseMinimumBet() {
        var turn = viewModel.getCurrentPlayerTurn();
        if (turn != null) {
            var amountToCall = turn.getAmountToCall();
            if (!amountToCall.isEmpty()) {
                return Protos.money(amountToCall) + 0.01;
            }
        }
        return 10d;
    }

    private void clearCurrentPlayerTurn() {
        // Handled by Repository
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
        stringBuilderList.add(Protos.shortName(playerAction.getActionType()).toLowerCase().replace("_", " "));
        var amount = Protos.money(playerAction.getAmount());
        if (amount > 0d) {
            stringBuilderList.add(getString(R.string.player_action_with));
            stringBuilderList.add(getString(R.string.currency_format, amount));
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
