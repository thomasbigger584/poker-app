package com.twb.pokerapp.ui.activity.game.texas;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.twb.pokerapp.data.model.enumeration.ActionType;
import com.twb.pokerapp.data.websocket.WebSocketClient;
import com.twb.pokerapp.data.websocket.message.client.SendChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.client.SendPlayerActionDTO;
import com.twb.pokerapp.data.websocket.message.server.ServerMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.BettingRoundUpdatedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealerDeterminedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerActionedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerSubscribedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.RoundFinishedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ValidationDTO;
import com.twb.stomplib.dto.LifecycleEvent;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TexasGameViewModel extends ViewModel
        implements WebSocketClient.WebSocketListener, WebSocketClient.SendListener {

    private static final String TAG = TexasGameViewModel.class.getSimpleName();
    private final WebSocketClient webSocketClient;

    private final MutableLiveData<Throwable> _errors = new MutableLiveData<>();
    public final LiveData<Throwable> errors = _errors;

    private final MutableLiveData<PlayerSubscribedDTO> _playerSubscribed = new MutableLiveData<>();
    public final LiveData<PlayerSubscribedDTO> playerSubscribed = _playerSubscribed;

    private final MutableLiveData<PlayerConnectedDTO> _playerConnected = new MutableLiveData<>();
    public final LiveData<PlayerConnectedDTO> playerConnected = _playerConnected;

    private final MutableLiveData<DealerDeterminedDTO> _dealerDetermined = new MutableLiveData<>();
    public final LiveData<DealerDeterminedDTO> dealerDetermined = _dealerDetermined;

    private final MutableLiveData<DealPlayerCardDTO> _dealPlayerCard = new MutableLiveData<>();
    public final LiveData<DealPlayerCardDTO> dealPlayerCard = _dealPlayerCard;

    private final MutableLiveData<DealCommunityCardDTO> _dealCommunityCard = new MutableLiveData<>();
    public final LiveData<DealCommunityCardDTO> dealCommunityCard = _dealCommunityCard;

    private final MutableLiveData<PlayerTurnDTO> _playerTurn = new MutableLiveData<>();
    public final LiveData<PlayerTurnDTO> playerTurn = _playerTurn;

    private final MutableLiveData<PlayerActionedDTO> _playerActioned = new MutableLiveData<>();
    public final LiveData<PlayerActionedDTO> playerActioned = _playerActioned;

    private final MutableLiveData<BettingRoundUpdatedDTO> _bettingRoundUpdated = new MutableLiveData<>();
    public final LiveData<BettingRoundUpdatedDTO> bettingRoundUpdated = _bettingRoundUpdated;

    private final MutableLiveData<RoundFinishedDTO> _roundFinished = new MutableLiveData<>();
    public final LiveData<RoundFinishedDTO> roundFinished = _roundFinished;

    private final MutableLiveData<GameFinishedDTO> _gameFinished = new MutableLiveData<>();
    public final LiveData<GameFinishedDTO> gameFinished = _gameFinished;

    private final MutableLiveData<ChatMessageDTO> _chatMessage = new MutableLiveData<>();
    public final LiveData<ChatMessageDTO> chatMessage = _chatMessage;

    private final MutableLiveData<LogMessageDTO> _logMessage = new MutableLiveData<>();
    public final LiveData<LogMessageDTO> logMessage = _logMessage;

    private final MutableLiveData<ErrorMessageDTO> _errorMessage = new MutableLiveData<>();
    public final LiveData<ErrorMessageDTO> errorMessage = _errorMessage;

    private final MutableLiveData<ValidationDTO> _validationMessage = new MutableLiveData<>();
    public final LiveData<ValidationDTO> validationMessage = _validationMessage;

    private final MutableLiveData<PlayerDisconnectedDTO> _playerDisconnected = new MutableLiveData<>();
    public final LiveData<PlayerDisconnectedDTO> playerDisconnected = _playerDisconnected;

    private final MutableLiveData<Void> _closedConnection = new MutableLiveData<>();
    public final LiveData<Void> closedConnection = _closedConnection;

    private UUID tableId;

    @Inject
    public TexasGameViewModel(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    // ***************************************************************
    // WebSocket Lifecycle
    // ***************************************************************

    public void connect(UUID tableId, String connectionType, Double buyInAmount) {
        this.tableId = tableId;
        this.webSocketClient.connect(tableId, this, connectionType, buyInAmount);
    }

    @Override
    public void onOpened(LifecycleEvent event) {
        Log.i(TAG, "onOpened: Websocket Opened.");
    }

    @Override
    public void onMessage(ServerMessageDTO<?> message) {
        switch (message.getType()) {
            case PLAYER_SUBSCRIBED: {
                _playerSubscribed.setValue((PlayerSubscribedDTO) message.getPayload());
                break;
            }
            case PLAYER_CONNECTED: {
                _playerConnected.setValue((PlayerConnectedDTO) message.getPayload());
                break;
            }
            case DEALER_DETERMINED: {
                _dealerDetermined.setValue((DealerDeterminedDTO) message.getPayload());
                break;
            }
            case DEAL_INIT: {
                _dealPlayerCard.setValue((DealPlayerCardDTO) message.getPayload());
                break;
            }
            case DEAL_COMMUNITY: {
                _dealCommunityCard.setValue((DealCommunityCardDTO) message.getPayload());
                break;
            }
            case PLAYER_TURN: {
                _playerTurn.setValue((PlayerTurnDTO) message.getPayload());
                break;
            }
            case PLAYER_ACTIONED: {
                _playerActioned.setValue((PlayerActionedDTO) message.getPayload());
                break;
            }
            case BETTING_ROUND_UPDATED: {
                _bettingRoundUpdated.setValue((BettingRoundUpdatedDTO) message.getPayload());
                break;
            }
            case ROUND_FINISHED: {
                _roundFinished.setValue((RoundFinishedDTO) message.getPayload());
                break;
            }
            case GAME_FINISHED: {
                _gameFinished.setValue((GameFinishedDTO) message.getPayload());
                break;
            }
            case CHAT: {
                _chatMessage.setValue((ChatMessageDTO) message.getPayload());
                break;
            }
            case LOG: {
                _logMessage.setValue((LogMessageDTO) message.getPayload());
                break;
            }
            case ERROR: {
                _errorMessage.setValue((ErrorMessageDTO) message.getPayload());
                break;
            }
            case VALIDATION: {
                _validationMessage.setValue((ValidationDTO) message.getPayload());
                break;
            }
            case PLAYER_DISCONNECTED: {
                _playerDisconnected.setValue((PlayerDisconnectedDTO) message.getPayload());
                break;
            }
            default:
                Log.w(TAG, "Unexpected Game Event Value: " + message.getType());
        }
    }

    public void sendChatMessage(String message) {
        var dto = new SendChatMessageDTO();
        dto.setMessage(message);
        webSocketClient.sendChatMessage(tableId, dto, this);
    }

    public void onPlayerAction(ActionType actionType) {
        onPlayerAction(actionType, null);
    }

    public void onPlayerAction(ActionType actionType, Double amount) {
        var dto = new SendPlayerActionDTO();
        dto.setAction(actionType.name());
        dto.setAmount(amount);
        webSocketClient.sendPlayerAction(tableId, dto, this);
    }

    @Override
    public void onSendSuccess() {
        Log.i(TAG, "onSendSuccess: ");
    }

    @Override
    public void onClosed(LifecycleEvent event) {
        _closedConnection.setValue(null);
    }

    public void disconnect() {
        tableId = null;
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
        _errors.setValue(throwable);
    }

    @Override
    public void onSendFailure(Throwable throwable) {
        _errors.setValue(throwable);
    }

    // ***************************************************************
    // Helper Methods
    // ***************************************************************

    private void publishLifecycleEventError(LifecycleEvent event) {
        if (event.getException() == null) {
            _errors.setValue(new RuntimeException(event.getMessage()));
        } else {
            _errors.setValue(event.getException());
        }
    }
}
