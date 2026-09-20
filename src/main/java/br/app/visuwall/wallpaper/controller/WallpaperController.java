package br.app.visuwall.wallpaper.controller;

import br.app.visuwall.wallpaper.dto.WallpaperSummaryResponse;
import br.app.visuwall.wallpaper.service.WallpaperService;
import br.app.visuwall.wallpaper.dto.UploadWallpaperRequest;
import br.app.visuwall.wallpaper.dto.WallpaperResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallpapers")
@RequiredArgsConstructor
public class WallpaperController {
    private final WallpaperService wallpaperService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WallpaperResponse upload(@Valid @ModelAttribute UploadWallpaperRequest req) {
        UUID fakeUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        return wallpaperService.upload(req.file(), fakeUserId, req.category(), req.tags());
    }

    @GetMapping
    public List<WallpaperSummaryResponse> getAll() {
        return wallpaperService.getAll();
    }

    @GetMapping("/{shortId}")
    public WallpaperResponse getByShortId(@PathVariable String shortId) {
        return wallpaperService.getByShortId(shortId);
    }
}