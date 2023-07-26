package com.twb.pokergame.data.websocket.message.server.enumeration;

import com.twb.pokergame.data.websocket.message.server.payload.ChatMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealCommunityCardDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealPlayerCardDTO;
import com.twb.pokergame.data.websocket.message.server.payload.DealerDeterminedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.LogMessageDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerConnectedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerDisconnectedDTO;
import com.twb.pokergame.data.websocket.message.server.payload.PlayerSubscribedDTO;

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


    // todo: add more


    /*
     * Chat message sent from a client to be forwarded
     * to all other clients and added to chatbox
     */
    CHAT,

    /*
     * Generic log message sent from server t
     * o be displayed in chatbox
     */
    LOG,

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
                //todo: add more

            case CHAT:
                return ChatMessageDTO.class;
            case LOG:
                return LogMessageDTO.class;
            case PLAYER_DISCONNECTED:
                return PlayerDisconnectedDTO.class;
            default:
                throw new IllegalStateException("Unknown Server Message Type: " + this);
        }
    }
}
