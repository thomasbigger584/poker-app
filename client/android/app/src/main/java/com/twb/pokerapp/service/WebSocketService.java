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
    public static final String EXTRA_TABLE_ID = "EXTRA_TABLE_ID";
    public static final String EXTRA_CONNECTION_TYPE = "EXTRA_CONNECTION_TYPE";
    public static final String EXTRA_BUY_IN_AMOUNT = "EXTRA_BUY_IN_AMOUNT";

    @Inject
    WebSocketClient webSocketClient;

    @Inject
    WebSocketRepository repository;

    @Inject
    AuthService authService;

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
                showTurnNotification();
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
        var tableId = (UUID) intent.getSerializableExtra(EXTRA_TABLE_ID);
        var connectionType = intent.getStringExtra(EXTRA_CONNECTION_TYPE);
        var buyInAmount = intent.getDoubleExtra(EXTRA_BUY_IN_AMOUNT, 0);

        startForeground(NOTIFICATION_ID, createNotification());
        repository.setTableId(tableId);

        webSocketClient.connect(tableId, this, connectionType, buyInAmount);
    }

    private void onStopAction() {
        stopForeground(true);
        stopSelf();
    }

    private void showTurnNotification() {
        var notificationIntent = new Intent(this, TexasGameActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        var pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        var notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your Turn!")
                .setContentText("It's your turn to act in the poker game.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();

        var manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(2, notification);
        }
    }
}
