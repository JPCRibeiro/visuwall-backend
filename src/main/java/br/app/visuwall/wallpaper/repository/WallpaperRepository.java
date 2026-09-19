package br.app.visuwall.wallpaper.repository;

import br.app.visuwall.wallpaper.domain.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WallpaperRepository extends JpaRepository<Wallpaper, UUID> {
    boolean existsByFileHash(String fileHash);

    List<Wallpaper> findAllByOrderByCreatedAtDesc();
}
