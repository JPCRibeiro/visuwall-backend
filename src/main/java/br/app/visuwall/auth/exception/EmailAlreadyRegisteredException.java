package br.app.visuwall.auth.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException() {
        super("Email já cadastrado");
    }
}