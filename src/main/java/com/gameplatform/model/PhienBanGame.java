package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PhienBanGame(
        int versionId,
        int gameId,
        String gameTitle,
        String versionName,
        String content,
        String fileVersion,
        BigDecimal sizeMb,
        LocalDate createdAt,
        String status
) {
    public PhienBanGame {
        gameTitle = VietnameseText.repair(gameTitle);
        versionName = VietnameseText.repair(versionName);
        content = VietnameseText.repair(content);
        fileVersion = VietnameseText.repair(fileVersion);
        status = VietnameseText.repair(status);
    }
}



