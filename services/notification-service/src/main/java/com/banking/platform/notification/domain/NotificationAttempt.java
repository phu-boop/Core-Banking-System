package com.banking.platform.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record NotificationAttempt(
        UUID attemptId,
        UUID notificationId,
        int attemptNumber,
        NotificationStatus status,
        String providerReference,
        String errorCode,
        Instant attemptedAt) {}
