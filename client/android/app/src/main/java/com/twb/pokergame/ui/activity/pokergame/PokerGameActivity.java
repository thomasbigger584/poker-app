package com.twb.pokergame.ui.activity.pokergame;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.PokerTable;
import com.twb.pokergame.data.model.dto.appuser.AppUserDTO;
import com.twb.pokergame.data.model.dto.playersession.PlayerSessionDTO;
import com.twb.pokergame.ui.activity.login.BaseAuthActivity;
import com.twb.pokergame.ui.activity.pokergame.chatbox.ChatBoxRecyclerAdapter;
import com.twb.pokergame.ui.dialog.AlertModalDialog;
import com.twb.pokergame.ui.dialog.DialogHelper;
import com.twb.pokergame.ui.dialog.FinishActivityOnClickListener;
import com.twb.pokergame.ui.layout.CardPairLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokerGameActivity extends BaseAuthActivity {
    private static final String TAG = PokerGameActivity.class.getSimpleName();
    private PokerGameViewModel viewModel;
    private PokerTable pokerTable;
    private AlertDialog loadingSpinner;
    private ChatBoxRecyclerAdapter chatBoxAdapter;
    private CardPairLayout[] cardPairLayouts = new CardPairLayout[6];

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

        cardPairLayouts[0] = findViewById(R.id.playerCardPairLayout);
        cardPairLayouts[1] = findViewById(R.id.tablePlayer1CardPairLayout);
        cardPairLayouts[2] = findViewById(R.id.tablePlayer2CardPairLayout);
        cardPairLayouts[3] = findViewById(R.id.tablePlayer3CardPairLayout);
        cardPairLayouts[4] = findViewById(R.id.tablePlayer4CardPairLayout);
        cardPairLayouts[5] = findViewById(R.id.tablePlayer5CardPairLayout);

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
        viewModel.playerConnected.observe(this, playerConnected -> {
            PlayerSessionDTO playerSession = playerConnected.getSession();
            AppUserDTO appUserDTO = playerSession.getUser();
            cardPairLayouts[0].updateDetails(playerSession);
            chatBoxAdapter.add("Connected: " + appUserDTO.getUsername());
            DialogHelper.dismiss(loadingSpinner);
        });
        viewModel.chatMessage.observe(this, chatMessage -> {
            chatBoxAdapter.add(chatMessage.getUsername() + ": " + chatMessage.getMessage());
        });
        viewModel.logMessage.observe(this, logMessage -> {
            chatBoxAdapter.add(logMessage.getMessage());
        });
        viewModel.playerDisconnected.observe(this, playerDisconnected -> {
            chatBoxAdapter.add("Disconnected: " + playerDisconnected.getUsername());
            //todo: remove player to from view, if player is current player then finish activity
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
