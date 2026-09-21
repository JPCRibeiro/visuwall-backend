package br.app.visuwall.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email ou senha inválidos");
    }
}