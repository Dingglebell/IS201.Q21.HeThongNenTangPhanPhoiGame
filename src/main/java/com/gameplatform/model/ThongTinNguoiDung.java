package com.gameplatform.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ThongTinNguoiDung(
        int accountId,
        int profileId,
        String username,
        String accountType,
        String accountStatus,
        String displayName,
        String roleOrType,
        String email,
        String phone,
        String location,
        BigDecimal revenueShare,
        LocalDate createdAt
) {
    public ThongTinNguoiDung {
        username = VietnameseText.repair(username);
        accountType = VietnameseText.repair(accountType);
        accountStatus = VietnameseText.repair(accountStatus);
        displayName = VietnameseText.repair(displayName);
        roleOrType = VietnameseText.repair(roleOrType);
        email = VietnameseText.repair(email);
        phone = VietnameseText.repair(phone);
        location = VietnameseText.repair(location);
    }
}



