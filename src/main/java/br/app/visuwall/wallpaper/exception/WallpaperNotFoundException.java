package br.app.visuwall.wallpaper.exception;

public class WallpaperNotFoundException extends RuntimeException {
    public WallpaperNotFoundException() {
        super("Wallpaper não encontrado");
    }
}