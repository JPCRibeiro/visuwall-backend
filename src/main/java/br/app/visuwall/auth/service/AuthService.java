package br.app.visuwall.auth.service;

import br.app.visuwall.auth.domain.RefreshToken;
import br.app.visuwall.auth.domain.TokenPair;
import br.app.visuwall.auth.dto.request.LoginRequest;
import br.app.visuwall.auth.dto.request.RegisterRequest;
import br.app.visuwall.auth.exception.InvalidCredentialsException;
import br.app.visuwall.auth.exception.InvalidRefreshTokenException;
import br.app.visuwall.auth.exception.RefreshTokenReuseException;
import br.app.visuwall.auth.repository.RefreshTokenRepository;
import br.app.visuwall.user.domain.User;
import br.app.visuwall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${jwt.refresh-token-ttl}")
    private Duration refreshTtl;

    @Value("${jwt.refresh-reuse-grace}")
    private Duration reuseGrace;

    @Transactional
    public TokenPair register(RegisterRequest req) {
        User user = userService.register(req.email(), req.password(), req.username());
        return issueTokens(user);
    }

    @Transactional
    public TokenPair login(LoginRequest req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return issueTokens(user);
    }

    private TokenPair issueTokens(User user) {
        String rawRefresh = tokenService.generateOpaqueToken();
        refreshTokenRepository.save(RefreshToken.openFamily(
                user.getId(), tokenService.sha256(rawRefresh), refreshTtl));
        return new TokenPair(tokenService.generateAccessToken(user), rawRefresh,
                tokenService.getExpiresInSeconds());
    }

    @Transactional(noRollbackFor = RefreshTokenReuseException.class)
    public TokenPair refresh(String rawRefreshToken) {
        RefreshToken current = refreshTokenRepository
                .findByTokenHash(tokenService.sha256(rawRefreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        boolean networkRetry = false;

        if (current.wasAlreadyRotated()) {
            if (!current.isWithinGracePeriod(reuseGrace)) {
                refreshTokenRepository.revokeFamily(current.getFamilyId(), Instant.now());
                throw new RefreshTokenReuseException();
            }

            refreshTokenRepository.findById(current.getReplacedBy())
                    .ifPresent(RefreshToken::revoke);

            networkRetry = true;
        } else if (!current.isUsable()) {
            throw new InvalidRefreshTokenException();
        }

        User user = userService.findById(current.getUserId())
                .orElseThrow(InvalidRefreshTokenException::new);

        String rawRefresh = tokenService.generateOpaqueToken();
        RefreshToken next = RefreshToken.continueFamily(
                user.getId(), current.getFamilyId(), tokenService.sha256(rawRefresh), refreshTtl
        );

        refreshTokenRepository.save(next);

        if (!networkRetry) {
            current.rotateTo(next.getId());
        }

        return new TokenPair(tokenService.generateAccessToken(user), rawRefresh, tokenService.getExpiresInSeconds());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(tokenService.sha256(rawRefreshToken))
                .ifPresent(rt -> refreshTokenRepository.revokeFamily(rt.getFamilyId(), Instant.now()));
    }

    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenRepository.revokeAllByUser(userId, Instant.now());
    }
}