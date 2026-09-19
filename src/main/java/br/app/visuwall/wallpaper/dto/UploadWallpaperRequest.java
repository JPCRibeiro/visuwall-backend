package br.app.visuwall.wallpaper.dto;

import br.app.visuwall.wallpaper.domain.Category;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadWallpaperRequest(
        @NotNull(message = "A imagem é obrigatória")
        MultipartFile file,

        @NotNull(message = "A categoria é obrigatória")
        Category category,

        List<String> tags
) {}