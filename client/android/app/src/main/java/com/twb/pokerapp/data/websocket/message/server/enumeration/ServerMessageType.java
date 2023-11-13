package com.twb.pokerapp.data.websocket.message.server.enumeration;

import com.twb.pokerapp.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.DealerDeterminedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.ErrorMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.GameFinishedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerSubscribedDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.PlayerTurnDTO;
import com.twb.pokerapp.data.websocket.message.server.payload.RoundFinishedDTO;

public enum ServerMessageType {
    /*
     * Used when a user connects via a subscription
     * and will initialise their current game state.
     */
    PLAYER_SUBSCRIBED,

    /*
     * Used to notify all players on the
     * table that a player has connected.
     */
    PLAYER_CONNECTED,

    /*
     * Used to notify all players on the
     * table that a dealer has been determined for this round
     */
    DEALER_DETERMINED,


    /*
     * Used to notify all players the cards for the first
     * cards being dealt held in the players hands
     */
    DEAL_INIT,

    /*
     * Used to notify all players of the cards
     * coming out during community cards
     */
    DEAL_COMMUNITY,

    /*
     * Used to notify all players of the current player turn
     */
    PLAYER_TURN,

    // todo: add more

    /*
     * Used to notify the client that the round has finished
     * so should reset the table
     */
    ROUND_FINISHED,

    /*
     * Used to notify the client that the game has finished
     * so should quit from the table
     */
    GAME_FINISHED,

    /*
     * Chat message sent from a client to be forwarded
     * to all other clients and added to chatbox
     */
    CHAT,

    /*
     * Generic log message sent from server
     * to be displayed in chatbox
     */
    LOG,

    /*
     * Error message sent from server which can either be sent
     * to a specific user or all users subscribed to the table
     */
    ERROR,

    /*
     * Used to notify all players on the
     * table that a player has disconnected
     */
    PLAYER_DISCONNECTED;

    public Class<?> getPayloadClass() {
        switch (this) {
            case PLAYER_SUBSCRIBED:
                return PlayerSubscribedDTO.class;
            case PLAYER_CONNECTED:
                return PlayerConnectedDTO.class;
            case DEALER_DETERMINED:
                return DealerDeterminedDTO.class;
            case DEAL_INIT:
                return DealPlayerCardDTO.class;
            case DEAL_COMMUNITY:
                return DealCommunityCardDTO.class;
            case PLAYER_TURN:
                return PlayerTurnDTO.class;

            //todo: add more

            case ROUND_FINISHED:
                return RoundFinishedDTO.class;
            case GAME_FINISHED:
                return GameFinishedDTO.class;
            case CHAT:
                return ChatMessageDTO.class;
            case LOG:
                return LogMessageDTO.class;
            case ERROR:
                return ErrorMessageDTO.class;
            case PLAYER_DISCONNECTED:
                return PlayerDisconnectedDTO.class;
            default:
                throw new IllegalStateException("Unknown Server Message Type: " + this);
        }
    }
}
