package com.babacar.secureauthservice.adapter.out.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String event;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuditLogEntity() {}

    public AuditLogEntity(UUID userId, String event,
                          String ipAddress, Instant createdAt) {
        this.userId = userId;
        this.event = event;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }
}
