package br.app.visuwall.wallpaper.dto;

import br.app.visuwall.wallpaper.domain.Wallpaper;

public record WallpaperSummaryResponse(
        String shortId,
        String thumbUrl,
        int width,
        int height
) {
    public static WallpaperSummaryResponse from(Wallpaper w) {
        return new WallpaperSummaryResponse(
                w.getShortId(),
                w.getThumbUrl(),
                w.getWidth(),
                w.getHeight()
        );
    }
}