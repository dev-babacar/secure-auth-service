package com.babacar.secureauthservice.adapter.out.persistence;

import com.babacar.secureauthservice.domain.port.out.AuditLogRepository;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    public AuditLogPersistenceAdapter(AuditLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void log(UUID userId, String event, String ipAddress) {
        jpaRepository.save(
                new AuditLogEntity(userId, event, ipAddress, Instant.now())
        );
    }
}
