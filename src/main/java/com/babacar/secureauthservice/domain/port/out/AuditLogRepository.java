package com.babacar.secureauthservice.domain.port.out;

import java.util.UUID;

public interface AuditLogRepository {
    void log(UUID userId, String event, String ipAddress);
}
