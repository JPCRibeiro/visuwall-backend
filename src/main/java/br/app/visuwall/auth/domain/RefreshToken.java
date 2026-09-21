package br.app.visuwall.auth.domain;

import br.app.visuwall.shared.domain.BaseEntity;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_refresh_family", columnList = "family_id"),
        @Index(name = "idx_refresh_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
    @Column(name = "token_hash", nullable = false, updatable = false, length = 64)
    private String tokenHash;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "family_id", nullable = false, updatable = false)
    private UUID familyId;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "rotated_at")
    private Instant rotatedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by")
    private UUID replacedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    private RefreshToken(UUID userId, UUID familyId, String tokenHash, Duration ttl) {
        assignId();
        this.userId = userId;
        this.familyId = familyId;
        this.tokenHash = tokenHash;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plus(ttl);
    }

    public static RefreshToken openFamily(UUID userId, String tokenHash, Duration ttl) {
        return new RefreshToken(userId, UuidCreator.getTimeOrderedEpoch(), tokenHash, ttl);
    }

    public static RefreshToken continueFamily(UUID userId, UUID familyId, String tokenHash, Duration ttl) {
        return new RefreshToken(userId, familyId, tokenHash, ttl);
    }

    public boolean isUsable() {
        return revokedAt == null
                && replacedBy == null
                && Instant.now().isBefore(expiresAt);
    }

    public boolean wasAlreadyRotated() {
        return replacedBy != null;
    }

    public boolean isWithinGracePeriod(Duration grace) {
        return revokedAt == null
                && rotatedAt != null
                && Instant.now().isBefore(rotatedAt.plus(grace));
    }

    public void rotateTo(UUID newTokenId) {
        this.replacedBy = newTokenId;
        this.rotatedAt = Instant.now();
    }

    public void revoke() {
        if (this.revokedAt == null) {
            this.revokedAt = Instant.now();
        }
    }
}