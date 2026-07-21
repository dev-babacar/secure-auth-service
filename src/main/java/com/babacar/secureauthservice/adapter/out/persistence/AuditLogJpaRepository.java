package com.babacar.secureauthservice.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogJpaRepository
        extends JpaRepository<AuditLogEntity, UUID> {
}
