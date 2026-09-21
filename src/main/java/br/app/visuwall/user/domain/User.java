package br.app.visuwall.user.domain;

import br.app.visuwall.shared.domain.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditableEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name="password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String username;

    private User(String email, String passwordHash, String username) {
        assignId();
        this.email = email;
        this.passwordHash = passwordHash;
        this.username = username;
    }

    public static User register(String email, String passwordHash, String username) {
        return new User(email, passwordHash, username);
    }
}
