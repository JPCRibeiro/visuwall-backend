package br.app.visuwall.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Sessão inválida");
    }
}
