package com.gameplatform.model;

public record TaiKhoanDangNhap(
        int accountId,
        String username,
        LoaiTaiKhoan accountType,
        String status,
        Integer profileId,
        String displayName,
        VaiTroNhanVien employeeRole
) {
    public TaiKhoanDangNhap {
        username = VietnameseText.repair(username);
        status = VietnameseText.repair(status);
        displayName = VietnameseText.repair(displayName);
    }

    public boolean isVaiTroNhanVien(VaiTroNhanVien role) {
        return accountType == LoaiTaiKhoan.NHAN_VIEN && employeeRole == role;
    }
}




