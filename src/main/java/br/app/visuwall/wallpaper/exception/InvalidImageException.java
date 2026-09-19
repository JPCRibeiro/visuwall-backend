package br.app.visuwall.wallpaper.exception;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException() {
        super("Arquivo enviado não é uma imagem válida (aceitos: JPG, PNG).");
    }
}