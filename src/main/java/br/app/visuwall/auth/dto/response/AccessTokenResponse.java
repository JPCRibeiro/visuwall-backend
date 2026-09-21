package br.app.visuwall.auth.dto.response;

import br.app.visuwall.auth.domain.TokenPair;

public record AccessTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public static AccessTokenResponse from(TokenPair pair) {
        return new AccessTokenResponse(pair.accessToken(), "Bearer", pair.expiresInSeconds());
    }
}