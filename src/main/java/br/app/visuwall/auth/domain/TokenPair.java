package br.app.visuwall.auth.domain;

public record TokenPair(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {}
