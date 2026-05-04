package com.twb.pokerapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.twb.pokerapp.PokerApplication;
import com.twb.pokerapp.R;
import com.twb.pokerapp.data.auth.AuthService;
import com.twb.pokerapp.data.repository.WebSocketRepository;
import com.twb.pokerapp.data.websocket.WebSocketClient;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.enumeration.ServerMessageType;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;
import com.twb.pokerapp.ui.activity.game.texas.TexasGameActivity;
import com.twb.stomplib.dto.LifecycleEvent;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WebSocketService extends Service implements WebSocketClient.WebSocketListener {
    private static final String TAG = WebSocketService.class.getSimpleName();
    private static final String CHANNEL_ID = "WebSocketServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_PLAYER_ACTION = "ACTION_PLAYER_ACTION";

    public static final String EXTRA_TABLE_ID = "EXTRA_TABLE_ID";
    public static final String EXTRA_CONNECTION_TYPE = "EXTRA_CONNECTION_TYPE";
    public static final String EXTRA_BUY_IN_AMOUNT = "EXTRA_BUY_IN_AMOUNT";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final String EXTRA_AMOUNT = "EXTRA_AMOUNT";

    @Inject
    WebSocketClient webSocketClient;

    @Inject
    WebSocketRepository repository;

    @Inject
    AuthService authService;

    private UUID tableId;

    // ***************************************************************
    // Service Lifecycle
    // ***************************************************************

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            var action = intent.getAction();
            if (ACTION_START.equals(action)) {
                onStartAction(intent);
            } else if (ACTION_STOP.equals(action)) {
                onStopAction();
            } else if (ACTION_PLAYER_ACTION.equals(action)) {
                onPlayerAction(intent);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        webSocketClient.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ***************************************************************
    // WebSocketListener Implementation
    // ***************************************************************

    @Override
    public void onOpened(LifecycleEvent event) {
        Log.i(TAG, "WebSocket Opened");
        repository.setConnected(true);
    }

    @Override
    public void onConnectError(LifecycleEvent event) {
        Log.e(TAG, "Connect Error: " + event.getMessage());
        if (event.getException() != null) {
            repository.handleError(event.getException());
        } else {
            repository.handleError(new RuntimeException(event.getMessage()));
        }
    }

    @Override
    public void onClosed(LifecycleEvent event) {
        Log.i(TAG, "WebSocket Closed");
        repository.setConnected(false);
    }

    @Override
    public void onFailedServerHeartbeat(LifecycleEvent event) {
        Log.e(TAG, "Heartbeat Failed");
    }

    @Override
    public void onMessage(ServerMessageDTO<?> message) {
        repository.handleNewMessage(message);

        if (message.getType() == ServerMessageType.PLAYER_TURN) {
            var turn = (PlayerTurnDTO) message.getPayload();
            var username = turn.getPlayerSession().getUser().getUsername();
            if (authService.isCurrentUser(username) && !PokerApplication.isAppInForeground()) {
                showTurnNotification(turn);
            }
        }
    }

    @Override
    public void onSubscribeError(Throwable throwable) {
        Log.e(TAG, "Subscribe Error", throwable);
        repository.handleError(throwable);
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var serviceChannel = new NotificationChannel(CHANNEL_ID, "WebSocket Service Channel", NotificationManager.IMPORTANCE_LOW);
            var manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification createNotification() {
        var notificationIntent = new Intent(this, TexasGameActivity.class);
        var pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Poker Game Connected")
                .setContentText("Maintaining background connection...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void onStartAction(Intent intent) {
        this.tableId = (UUID) intent.getSerializableExtra(EXTRA_TABLE_ID);
        var connectionType = intent.getStringExtra(EXTRA_CONNECTION_TYPE);
        var buyInAmount = intent.getDoubleExtra(EXTRA_BUY_IN_AMOUNT, 0);

        startForeground(NOTIFICATION_ID, createNotification());
        repository.setTableId(tableId);

        webSocketClient.connect(tableId, this, connectionType, buyInAmount);
    }

    private void onPlayerAction(Intent intent) {
        var action = intent.getStringExtra(EXTRA_ACTION);
        var amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0);

        if (action != null && tableId != null) {
            var actionDto = new com.twb.pokerapp.data.websocket.message.client.SendPlayerActionDTO();
            actionDto.setAction(action);
            actionDto.setAmount(amount);

            webSocketClient.sendPlayerAction(tableId, actionDto, new WebSocketClient.SendListener() {
                @Override
                public void onSendSuccess() {
                    Log.i(TAG, "Player action sent: " + action);
                    var manager = getSystemService(NotificationManager.class);
                    if (manager != null) {
                        manager.cancel(2);
                    }
                }

                @Override
                public void onSendFailure(Throwable throwable) {
                    Log.e(TAG, "Failed to send player action", throwable);
                }
            });
        }
    }

    private void onStopAction() {
        repository.setTableId(null);
        stopForeground(true);
        stopSelf();
    }

    private boolean isDirectAction(String action) {
        return "CALL".equalsIgnoreCase(action) ||
                "CHECK".equalsIgnoreCase(action) ||
                "FOLD".equalsIgnoreCase(action);
    }

    private void showTurnNotification(PlayerTurnDTO turn) {
        var notificationIntent = new Intent(this, TexasGameActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        var pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        var contentText = "Betting Round: " + turn.getBettingRound().getType();
        if (turn.getAmountToCall() > 0) {
            contentText += " - Call: $" + turn.getAmountToCall();
        }

        var builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your Turn!")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        for (var action : turn.getNextActions()) {
            PendingIntent actionPendingIntent;
            if (isDirectAction(action)) {
                var actionIntent = new Intent(this, WebSocketService.class);
                actionIntent.setAction(ACTION_PLAYER_ACTION);
                actionIntent.putExtra(EXTRA_ACTION, action);
                if ("CALL".equalsIgnoreCase(action)) {
                    actionIntent.putExtra(EXTRA_AMOUNT, turn.getAmountToCall());
                }
                actionPendingIntent = PendingIntent.getService(this, action.hashCode(), actionIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                var actionIntent = new Intent(this, TexasGameActivity.class);
                actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                actionPendingIntent = PendingIntent.getActivity(this, action.hashCode(), actionIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            }

            builder.addAction(new NotificationCompat.Action(0, action, actionPendingIntent));
        }

        var manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(2, builder.build());
        }
    }
}
