package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ThongTinGame(
        int gameId,
        String title,
        String developerName,
        String genres,
        int ageRating,
        BigDecimal originalPrice,
        BigDecimal salePrice,
        int discountPercent,
        String status,
        LocalDate releaseDate,
        int purchases,
        String description,
        String coverMedia
) {
    public ThongTinGame {
        title = VietnameseText.repair(title);
        developerName = VietnameseText.repair(developerName);
        genres = VietnameseText.repair(genres);
        status = VietnameseText.repair(status);
        description = VietnameseText.repair(description);
        coverMedia = VietnameseText.repair(coverMedia);
    }
}



