package com.twb.pokerapp.data.websocket.message.client;

import java.util.UUID;

/**
 * Mirror of the server-side {@code com.twb.pokerapp.web.websocket.message.client.CreateBotConnectionDTO}.
 * Sent to {@code /app/pokerTable.{tableId}.sendBotConnected} to connect a bot player to the table.
 */
public class SendBotConnectedDTO {
    private UUID botUserId;
    private Double buyInAmount;

    public UUID getBotUserId() {
        return botUserId;
    }

    public void setBotUserId(UUID botUserId) {
        this.botUserId = botUserId;
    }

    public Double getBuyInAmount() {
        return buyInAmount;
    }

    public void setBuyInAmount(Double buyInAmount) {
        this.buyInAmount = buyInAmount;
    }
}
