package com.gameplatform.model;

import java.util.Arrays;

public enum VaiTroNhanVien {
    QUAN_LY_NEN_TANG("Quản lý nền tảng"),
    KIEM_DUYET_VIEN("Kiểm duyệt viên"),
    MARKETING("Marketing"),
    CSKH("CSKH");

    private final String dbValue;

    VaiTroNhanVien(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static VaiTroNhanVien fromDbValue(String value) {
        return Arrays.stream(values())
                .filter(role -> VietnameseText.equalsDbText(role.dbValue, value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Vai trò nhân viên không hợp lệ: " + value));
    }
}



