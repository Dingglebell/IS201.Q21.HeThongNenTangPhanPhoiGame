package com.gameplatform.model;

public record TheLoaiGame(
        int categoryId,
        String name,
        String description
) {
    public TheLoaiGame {
        name = VietnameseText.repair(name);
        description = VietnameseText.repair(description);
    }
}


