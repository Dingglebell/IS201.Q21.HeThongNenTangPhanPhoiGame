package com.gameplatform.model;

import java.time.LocalDateTime;

public record ThongTinTicket(
        int ticketId,
        String type,
        String content,
        String playerName,
        String gameTitle,
        String transactionId,
        LocalDateTime createdAt,
        String status,
        String handledByName,
        String response,
        LocalDateTime handledAt
) {
    public ThongTinTicket {
        type = VietnameseText.repair(type);
        content = VietnameseText.repair(content);
        playerName = VietnameseText.repair(playerName);
        gameTitle = VietnameseText.repair(gameTitle);
        transactionId = VietnameseText.repair(transactionId);
        status = VietnameseText.repair(status);
        handledByName = VietnameseText.repair(handledByName);
        response = VietnameseText.repair(response);
    }
}



