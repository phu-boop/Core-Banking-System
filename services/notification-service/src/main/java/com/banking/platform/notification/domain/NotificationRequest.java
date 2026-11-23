package com.banking.platform.notification.domain;

public record NotificationRequest(
        String notificationKey,
        String recipientRef,
        String templateCode,
        String channel,
        String correlationId) {}
