package com.banking.platform.notification.application;

import com.banking.platform.notification.domain.NotificationAttempt;
import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationRequest;
import com.banking.platform.notification.integration.NotificationDelivery;
import com.banking.platform.notification.integration.NotificationDeliveryAdapter;
import com.banking.platform.notification.persistence.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final NotificationDeliveryAdapter adapter;

    public NotificationService(NotificationRepository repository,
                               NotificationDeliveryAdapter adapter) {
        this.repository = repository;
        this.adapter = adapter;
    }

    @Transactional
    public NotificationRecord request(NotificationRequest request) {
        validate(request);
        return repository.findByKey(request.notificationKey())
                .orElseGet(() -> repository.insert(request));
    }

    @Transactional
    public NotificationRecord deliver(UUID notificationId) {
        NotificationRecord notification = repository.find(notificationId);
        if (notification.status() != com.banking.platform.notification.domain.NotificationStatus.REQUESTED) {
            return notification;
        }
        NotificationDelivery delivery = adapter.deliver(notification);
        int attemptNumber = repository.findAttempts(notificationId).size() + 1;
        repository.insertAttempt(notificationId, attemptNumber, delivery.status(),
                delivery.providerReference(), delivery.errorCode());
        return repository.updateStatus(notificationId, delivery.status());
    }

    @Transactional(readOnly = true)
    public NotificationRecord get(UUID notificationId) {
        return repository.find(notificationId);
    }

    @Transactional(readOnly = true)
    public List<NotificationAttempt> attempts(UUID notificationId) {
        return repository.findAttempts(notificationId);
    }

    private void validate(NotificationRequest request) {
        if (request == null
                || request.notificationKey() == null || request.notificationKey().isBlank()
                || request.recipientRef() == null || request.recipientRef().isBlank()
                || request.templateCode() == null || request.templateCode().isBlank()
                || request.channel() == null || request.channel().isBlank()
                || request.correlationId() == null || request.correlationId().isBlank()) {
            throw new IllegalArgumentException("Notification request fields are required");
        }
    }
}
