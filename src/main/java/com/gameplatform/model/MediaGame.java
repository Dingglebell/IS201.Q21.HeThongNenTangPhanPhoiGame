package com.gameplatform.model;

public record MediaGame(
        int mediaId,
        int gameId,
        String gameTitle,
        String mediaType,
        String fileMedia
) {
    public MediaGame {
        gameTitle = VietnameseText.repair(gameTitle);
        mediaType = VietnameseText.repair(mediaType);
        fileMedia = VietnameseText.repair(fileMedia);
    }
}



