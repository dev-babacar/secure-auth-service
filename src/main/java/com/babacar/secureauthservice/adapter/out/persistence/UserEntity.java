package com.babacar.secureauthservice.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEntity role;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "mfa_verified", nullable = false)
    private boolean mfaVerified;

    protected UserEntity() {}

    public UserEntity(UUID id, String email, String password,
                      RoleEntity role, boolean mfaEnabled, Instant createdAt ,String mfaSecret ,boolean mfaVerified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.mfaEnabled = mfaEnabled;
        this.createdAt = createdAt;
        this.mfaSecret = mfaSecret;
        this.mfaVerified = mfaVerified;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public RoleEntity getRole() { return role; }
    public boolean isMfaEnabled() { return mfaEnabled; }
    public Instant getCreatedAt() { return createdAt; }
    public String getMfaSecret() { return mfaSecret; }
    public boolean isMfaVerified() { return mfaVerified; }

    public void setMfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; }
    public void setMfaVerified(boolean mfaVerified) { this.mfaVerified = mfaVerified; }
}
