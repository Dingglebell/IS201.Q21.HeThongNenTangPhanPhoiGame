package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ThongTinMaGiamGia(
        int discountCodeId,
        String code,
        BigDecimal discountAmount,
        int usageLimit,
        int usedCount,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal minimumTotal,
        String status,
        String description
) {
    public ThongTinMaGiamGia {
        code = VietnameseText.repair(code);
        status = VietnameseText.repair(status);
        description = VietnameseText.repair(description);
    }
}


