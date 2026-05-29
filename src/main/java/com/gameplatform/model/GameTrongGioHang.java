package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GameTrongGioHang(
        int gameId,
        String title,
        String developerName,
        BigDecimal originalPrice,
        BigDecimal salePrice,
        LocalDateTime addedAt
) {
    public GameTrongGioHang {
        title = VietnameseText.repair(title);
        developerName = VietnameseText.repair(developerName);
    }
}



