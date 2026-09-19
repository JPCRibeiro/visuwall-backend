package br.app.visuwall.wallpaper.domain;

import br.app.visuwall.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallpapers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallpaper extends AuditableEntity {
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "short_id", unique = true, nullable = false, updatable = false)
    private String shortId;

    @Column(name = "file_hash", nullable = false, unique = true, length = 64)
    private String fileHash;

    @Column(name = "original_url", nullable = false, length = 512)
    private String originalUrl;

    @Column(name = "thumb_url", nullable = false, length = 512)
    private String thumbUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> tags = new ArrayList<>();

    private Wallpaper(UUID userId, String originalUrl, String shortId, Category category,
                      int width, int height, String fileHash, String thumbUrl, List<String> tags) {
        assignId();
        this.userId = userId;
        this.originalUrl = originalUrl;
        this.shortId = shortId;
        this.category = category;
        this.width = width;
        this.height = height;
        this.fileHash = fileHash;
        this.thumbUrl = thumbUrl;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public static Wallpaper upload(UUID userId, String originalUrl, String shortId,
                                   Category category, int width, int height,
                                   String fileHash, String thumbUrl, List<String> tags) {
        return new Wallpaper(userId, originalUrl, shortId, category, width, height, fileHash, thumbUrl, tags);
    }
}
