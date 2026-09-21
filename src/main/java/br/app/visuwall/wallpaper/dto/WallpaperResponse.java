package br.app.visuwall.wallpaper.dto;

import br.app.visuwall.wallpaper.domain.Category;
import br.app.visuwall.wallpaper.domain.Wallpaper;

import java.time.Instant;
import java.util.List;

public record WallpaperResponse(
        String shortId,
        String originalUrl,
        String thumbUrl,
        Category category,
        List<String> tags,
        int width,
        int height,
        String authorName,
        Instant createdAt
) {
    public static WallpaperResponse from(Wallpaper w, String authorName) {
        return new WallpaperResponse(
                w.getShortId(),
                w.getOriginalUrl(),
                w.getThumbUrl(),
                w.getCategory(),
                w.getTags(),
                w.getWidth(),
                w.getHeight(),
                authorName,
                w.getCreatedAt()
        );
    }
}
