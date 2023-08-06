package com.twb.pokergame.web.websocket.message.server;

import com.twb.pokergame.web.websocket.message.server.payload.*;

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
     * Used to notify the client that the round has finished
     * so should reset the table
     */
    ROUND_FINISHED,

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
            case ROUND_FINISHED:
                return RoundFinishedDTO.class;

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
