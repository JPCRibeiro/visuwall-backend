package br.app.visuwall.wallpaper.exception;

public class DuplicateWallpaperException extends RuntimeException {
    public DuplicateWallpaperException() {
        super("Wallpaper já existe");
    }
}