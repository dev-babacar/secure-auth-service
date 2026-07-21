package com.babacar.secureauthservice.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private UUID family;

    protected RefreshTokenEntity() {}

    public RefreshTokenEntity(UUID id, String tokenHash, UUID userId,
                              Instant expiresAt, boolean revoked, UUID family) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.family = family;
    }

    public UUID getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public UUID getUserId() { return userId; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public UUID getFamily() { return family; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
