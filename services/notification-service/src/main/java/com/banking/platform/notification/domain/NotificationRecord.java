package com.banking.platform.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record NotificationRecord(
        UUID notificationId,
        String notificationKey,
        String recipientRef,
        String templateCode,
        String channel,
        String correlationId,
        NotificationStatus status,
        Instant createdAt,
        Instant updatedAt) {}
