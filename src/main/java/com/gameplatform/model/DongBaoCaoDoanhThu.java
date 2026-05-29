package com.gameplatform.model;

import java.math.BigDecimal;

public record DongBaoCaoDoanhThu(
        int gameId,
        String tenGame,
        String tenNhaPhatTrien,
        int soLuongBan,
        BigDecimal doanhThuGoc,
        BigDecimal doanhThuNhaPhatTrien,
        BigDecimal doanhThuNenTang
) {
    public DongBaoCaoDoanhThu {
        tenGame = VietnameseText.repair(tenGame);
        tenNhaPhatTrien = VietnameseText.repair(tenNhaPhatTrien);
    }
}



