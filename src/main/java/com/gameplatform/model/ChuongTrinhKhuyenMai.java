package com.gameplatform.model;

import java.time.LocalDate;

public record ChuongTrinhKhuyenMai(
        int promotionId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String content,
        int gameCount
) {
    public ChuongTrinhKhuyenMai {
        name = VietnameseText.repair(name);
        status = VietnameseText.repair(status);
        content = VietnameseText.repair(content);
    }
}



