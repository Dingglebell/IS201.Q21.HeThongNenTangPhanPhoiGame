package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GameCuaNhaPhatTrien(
        int gameId,
        String title,
        BigDecimal originalPrice,
        String status,
        LocalDate releaseDate,
        int purchases
) {
    public GameCuaNhaPhatTrien {
        title = VietnameseText.repair(title);
        status = VietnameseText.repair(status);
    }
}



