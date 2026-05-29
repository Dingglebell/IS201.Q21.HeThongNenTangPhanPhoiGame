package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ThongTinGiaoDich(
        int transactionId,
        String games,
        BigDecimal totalOriginal,
        BigDecimal totalDiscount,
        BigDecimal totalPaid,
        String paymentMethod,
        LocalDateTime createdAt,
        String status
) {
    public ThongTinGiaoDich {
        games = VietnameseText.repair(games);
        paymentMethod = VietnameseText.repair(paymentMethod);
        status = VietnameseText.repair(status);
    }
}



