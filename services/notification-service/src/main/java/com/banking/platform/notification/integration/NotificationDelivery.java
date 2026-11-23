package com.banking.platform.notification.integration;

import com.banking.platform.notification.domain.NotificationStatus;

public record NotificationDelivery(
        NotificationStatus status,
        String providerReference,
        String errorCode) {}
