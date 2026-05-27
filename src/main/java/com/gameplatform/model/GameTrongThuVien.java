package com.gameplatform.model;

import java.time.LocalDateTime;

public record GameTrongThuVien(
        int gameId,
        String title,
        String developerName,
        String genres,
        LocalDateTime ownedAt,
        int playHours,
        Integer rating,
        String fileVersion
) {
    public GameTrongThuVien {
        title = VietnameseText.repair(title);
        developerName = VietnameseText.repair(developerName);
        genres = VietnameseText.repair(genres);
    }
}


