package com.twb.pokergame.web.websocket.message.server;

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
    PLAYER_DISCONNECTED
}
