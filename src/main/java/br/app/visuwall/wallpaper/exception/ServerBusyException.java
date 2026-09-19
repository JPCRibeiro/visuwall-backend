package br.app.visuwall.wallpaper.exception;

public class ServerBusyException extends RuntimeException {
    public ServerBusyException() {
        super("Servidor ocupado, tente de novo em instantes");
    }
}