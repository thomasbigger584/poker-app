package com.twb.pokerapp.web.websocket.message.server;

import com.twb.pokerapp.web.websocket.message.server.payload.*;
import com.twb.pokerapp.web.websocket.message.server.payload.validation.ValidationDTO;

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

    /*
     * Used to notify all players of a players action
     */
    PLAYER_ACTIONED,

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
     * Error message sent from server which are validation
     * errors on events coming into server
     */
    VALIDATION,

    /*
     * Used to notify all players on the
     * table that a player has disconnected
     */
    PLAYER_DISCONNECTED;

    public Class<?> getPayloadClass() {
        return switch (this) {
            case PLAYER_SUBSCRIBED -> PlayerSubscribedDTO.class;
            case PLAYER_CONNECTED -> PlayerConnectedDTO.class;
            case DEALER_DETERMINED -> DealerDeterminedDTO.class;
            case DEAL_INIT -> DealPlayerCardDTO.class;
            case DEAL_COMMUNITY -> DealCommunityCardDTO.class;
            case PLAYER_TURN -> PlayerTurnDTO.class;
            case PLAYER_ACTIONED -> PlayerActionEventDTO.class;

            //todo: add more

            case ROUND_FINISHED -> RoundFinishedDTO.class;
            case GAME_FINISHED -> GameFinishedDTO.class;
            case CHAT -> ChatMessageDTO.class;
            case LOG -> LogMessageDTO.class;
            case ERROR -> ErrorMessageDTO.class;
            case VALIDATION -> ValidationDTO.class;
            case PLAYER_DISCONNECTED -> PlayerDisconnectedDTO.class;
        };
    }
}
