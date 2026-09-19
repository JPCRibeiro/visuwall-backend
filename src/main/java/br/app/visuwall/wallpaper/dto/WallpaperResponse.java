package br.app.visuwall.wallpaper.dto;

import br.app.visuwall.wallpaper.domain.Category;
import br.app.visuwall.wallpaper.domain.Wallpaper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WallpaperResponse(
        UUID id,
        String shortId,
        String originalUrl,
        String thumbUrl,
        Category category,
        List<String> tags,
        int width,
        int height,
        UUID userId,
        String authorName,
        Instant createdAt
) {
    public static WallpaperResponse from(Wallpaper w, String authorName) {
        return new WallpaperResponse(
                w.getId(),
                w.getShortId(),
                w.getOriginalUrl(),
                w.getThumbUrl(),
                w.getCategory(),
                w.getTags(),
                w.getWidth(),
                w.getHeight(),
                w.getUserId(),
                authorName,
                w.getCreatedAt()
        );
    }
}
