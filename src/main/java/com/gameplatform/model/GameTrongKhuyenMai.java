package com.gameplatform.model;

import java.math.BigDecimal;

public record GameTrongKhuyenMai(
        int promotionId,
        int gameId,
        String promotionName,
        String gameTitle,
        BigDecimal discountPercent
) {
    public GameTrongKhuyenMai {
        promotionName = VietnameseText.repair(promotionName);
        gameTitle = VietnameseText.repair(gameTitle);
    }
}


