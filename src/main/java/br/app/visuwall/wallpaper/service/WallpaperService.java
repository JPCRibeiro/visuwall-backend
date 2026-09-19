package br.app.visuwall.wallpaper.service;

import br.app.visuwall.shared.config.storage.S3Service;
import br.app.visuwall.wallpaper.domain.Category;
import br.app.visuwall.wallpaper.domain.Wallpaper;
import br.app.visuwall.wallpaper.dto.WallpaperResponse;
import br.app.visuwall.wallpaper.dto.WallpaperSummaryResponse;
import br.app.visuwall.wallpaper.exception.DuplicateWallpaperException;
import br.app.visuwall.wallpaper.exception.InvalidImageException;
import br.app.visuwall.wallpaper.exception.ServerBusyException;
import br.app.visuwall.wallpaper.repository.WallpaperRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WallpaperService {
    private final WallpaperRepository wallpaperRepository;
    private final S3Service s3Service;

    private final Semaphore resizeSemaphore = new Semaphore(2);
    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

    @Value("${aws.s3.public-url}")
    private String publicUrl;

    public WallpaperResponse upload(MultipartFile file, UUID userId, Category category, List<String> tags) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler o arquivo", e);
        }

        String contentType = detectImageType(bytes);
        String extension = extensionOf(contentType);

        String fileHash = sha256Hex(bytes);
        if (wallpaperRepository.existsByFileHash(fileHash)) {
            throw new DuplicateWallpaperException();
        }

        String shortId = generateShortId();
        String originalKey = "wallpapers/" + shortId + extension;
        String thumbKey = "wallpapers/" + shortId + "-thumb.jpg";

        boolean got;
        try {
            got = resizeSemaphore.tryAcquire(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        if (!got) throw new ServerBusyException();

        int width, height;
        byte[] thumbBytes;
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) throw new InvalidImageException();

            width = image.getWidth();
            height = image.getHeight();

            var thumbOs = new ByteArrayOutputStream();
            Thumbnails.of(image)
                    .size(1024, 1024)
                    .imageType(BufferedImage.TYPE_INT_RGB)
                    .outputFormat("jpg")
                    .outputQuality(0.8)
                    .toOutputStream(thumbOs);
            thumbBytes = thumbOs.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao processar a imagem", e);
        } finally {
            resizeSemaphore.release();
        }

        s3Service.uploadBytes(originalKey, bytes, contentType);
        s3Service.uploadBytes(thumbKey, thumbBytes, "image/jpeg");

        Wallpaper wallpaper = Wallpaper.upload(userId, publicUrl + originalKey, shortId,
                category, width, height, fileHash, publicUrl + thumbKey, tags);

        try {
            Wallpaper saved = wallpaperRepository.save(wallpaper);
            return WallpaperResponse.from(saved, "NomeDoAutor");
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateWallpaperException();
        }
    }

    @Transactional(readOnly = true)
    public List<WallpaperSummaryResponse> getAll() {
        return wallpaperRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(WallpaperSummaryResponse::from)
                .toList();
    }

    private static String extensionOf(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            default -> throw new InvalidImageException();
        };
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível na JVM", e);
        }
    }

    public static String generateShortId() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHABET[ThreadLocalRandom.current().nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

    public static String detectImageType(byte[] bytes) {
        Tika tika = new Tika();

        String type = tika.detect(bytes);

        if (type == null || (!type.equals("image/jpeg") && !type.equals("image/png"))) {
            throw new InvalidImageException();
        }

        return type;
    }
}