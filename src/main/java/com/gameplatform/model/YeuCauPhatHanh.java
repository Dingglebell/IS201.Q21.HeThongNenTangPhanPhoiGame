package com.gameplatform.model;

import java.time.LocalDateTime;

public record YeuCauPhatHanh(
        int requestId,
        int developerId,
        String developerName,
        int gameId,
        String gameTitle,
        Integer versionId,
        String versionName,
        LocalDateTime requestedAt,
        String status,
        Integer handledByEmployeeId,
        String handledByName,
        String rejectReason,
        LocalDateTime handledAt
) {
    public YeuCauPhatHanh {
        developerName = VietnameseText.repair(developerName);
        gameTitle = VietnameseText.repair(gameTitle);
        versionName = VietnameseText.repair(versionName);
        status = VietnameseText.repair(status);
        handledByName = VietnameseText.repair(handledByName);
        rejectReason = VietnameseText.repair(rejectReason);
    }
}


