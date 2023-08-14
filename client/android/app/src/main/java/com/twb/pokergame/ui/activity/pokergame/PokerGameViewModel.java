package com.twb.pokergame.ui.activity.pokergame;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokergame.data.websocket.WebSocketClient;
import com.twb.pokergame.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokergame.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealerDeterminedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerSubscribedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.RoundFinishedDTO;
import com.twb.stomplib.dto.LifecycleEvent;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PokerGameViewModel extends ViewModel
        implements WebSocketClient.WebSocketListener, WebSocketClient.SendListener {

    private static final String TAG = PokerGameViewModel.class.getSimpleName();
    private final WebSocketClient webSocketClient;

    public MutableLiveData<Throwable> errors = new MutableLiveData<>();
    public MutableLiveData<PlayerSubscribedDTO> playerSubscribed = new MutableLiveData<>();
    public MutableLiveData<PlayerConnectedDTO> playerConnected = new MutableLiveData<>();
    public MutableLiveData<DealerDeterminedDTO> dealerDetermined = new MutableLiveData<>();
    public MutableLiveData<DealPlayerCardDTO> dealPlayerCard = new MutableLiveData<>();
    public MutableLiveData<DealCommunityCardDTO> dealCommunityCard = new MutableLiveData<>();
    public MutableLiveData<RoundFinishedDTO> roundFinished = new MutableLiveData<>();
    public MutableLiveData<GameFinishedDTO> gameFinished = new MutableLiveData<>();

    //todo: add more

    public MutableLiveData<ChatMessageDTO> chatMessage = new MutableLiveData<>();
    public MutableLiveData<LogMessageDTO> logMessage = new MutableLiveData<>();
    public MutableLiveData<ErrorMessageDTO> errorMessage = new MutableLiveData<>();
    public MutableLiveData<PlayerDisconnectedDTO> playerDisconnected = new MutableLiveData<>();
    public MutableLiveData<Void> closedConnection = new MutableLiveData<>();

    private UUID pokerTableId;

    @Inject
    public PokerGameViewModel(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    // ***************************************************************
    // WebSocket Lifecycle
    // ***************************************************************

    public void connect(UUID pokerTableId) {
        this.pokerTableId = pokerTableId;
        webSocketClient.connect(pokerTableId, this);
    }

    @Override
    public void onOpened(LifecycleEvent event) {
        Log.i(TAG, "onOpened: Websocket Opened.");
    }

    @Override
    public void onMessage(ServerMessageDTO<?> message) {
        switch (message.getType()) {
            case PLAYER_SUBSCRIBED: {
                playerSubscribed.setValue((PlayerSubscribedDTO) message.getPayload());
                break;
            }
            case PLAYER_CONNECTED: {
                playerConnected.setValue((PlayerConnectedDTO) message.getPayload());
                break;
            }
            case DEALER_DETERMINED: {
                dealerDetermined.setValue((DealerDeterminedDTO) message.getPayload());
                break;
            }
            case DEAL_INIT: {
                dealPlayerCard.setValue((DealPlayerCardDTO) message.getPayload());
                break;
            }
            case DEAL_COMMUNITY: {
                dealCommunityCard.setValue((DealCommunityCardDTO) message.getPayload());
                break;
            }
            case ROUND_FINISHED: {
                roundFinished.setValue((RoundFinishedDTO) message.getPayload());
                break;
            }
            case GAME_FINISHED: {
                gameFinished.setValue((GameFinishedDTO) message.getPayload());
                break;
            }

            //todo: add more

            case CHAT: {
                chatMessage.setValue((ChatMessageDTO) message.getPayload());
                break;
            }
            case LOG: {
                logMessage.setValue((LogMessageDTO) message.getPayload());
                break;
            }
            case ERROR: {
                errorMessage.setValue((ErrorMessageDTO) message.getPayload());
                break;
            }
            case PLAYER_DISCONNECTED: {
                playerDisconnected.setValue((PlayerDisconnectedDTO) message.getPayload());
                break;
            }
            default:
                throw new IllegalStateException("Unexpected Game Event Value: " + message.getType());
        }
    }

    public void sendChatMessage(String message) {
        SendChatMessageDTO dto = new SendChatMessageDTO();
        dto.setMessage(message);
        webSocketClient.send(pokerTableId, dto, this);
    }

    @Override
    public void onSendSuccess() {
        Log.i(TAG, "onSendSuccess: ");
    }

    @Override
    public void onClosed(LifecycleEvent event) {
        closedConnection.setValue(null);
    }

    public void disconnect() {
        webSocketClient.disconnect();
    }

    // ***************************************************************
    // Error Lifecycle
    // ***************************************************************

    @Override
    public void onConnectError(LifecycleEvent event) {
        publishLifecycleEventError(event);
    }

    @Override
    public void onFailedServerHeartbeat(LifecycleEvent event) {
        publishLifecycleEventError(event);
    }

    @Override
    public void onSubscribeError(Throwable throwable) {
        errors.setValue(throwable);
    }

    @Override
    public void onSendFailure(Throwable throwable) {
        errors.setValue(throwable);
    }

    // Helper Methods
    // ----------------------------------------------------------------

    private void publishLifecycleEventError(LifecycleEvent event) {
        if (event.getException() == null) {
            errors.setValue(new RuntimeException(event.getMessage()));
        } else {
            errors.setValue(event.getException());
        }
    }
}
