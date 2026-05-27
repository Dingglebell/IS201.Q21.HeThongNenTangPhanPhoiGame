package com.gameplatform.model;

public record ThongTinHoSo(
        String displayName,
        String email,
        String phone,
        String extraLabel,
        String extraValue
) {
    public ThongTinHoSo {
        displayName = VietnameseText.repair(displayName);
        email = VietnameseText.repair(email);
        phone = VietnameseText.repair(phone);
        extraLabel = VietnameseText.repair(extraLabel);
        extraValue = VietnameseText.repair(extraValue);
    }
}


