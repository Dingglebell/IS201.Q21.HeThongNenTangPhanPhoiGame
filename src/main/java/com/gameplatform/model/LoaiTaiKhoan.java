package com.gameplatform.model;

import java.util.Arrays;

public enum LoaiTaiKhoan {
    NGUOI_CHOI("Người chơi"),
    NHA_PHAT_TRIEN("Nhà phát triển"),
    NHAN_VIEN("Nhân viên");

    private final String dbValue;

    LoaiTaiKhoan(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static LoaiTaiKhoan fromDbValue(String value) {
        return Arrays.stream(values())
                .filter(type -> VietnameseText.equalsDbText(type.dbValue, value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Loại tài khoản không hợp lệ: " + value));
    }
}



