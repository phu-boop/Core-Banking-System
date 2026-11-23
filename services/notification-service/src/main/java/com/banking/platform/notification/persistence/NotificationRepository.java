package com.banking.platform.notification.persistence;

import com.banking.platform.notification.domain.NotificationAttempt;
import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Optional<NotificationRecord> findByKey(String notificationKey);
    NotificationRecord insert(NotificationRequest request);
    NotificationRecord find(UUID notificationId);
    NotificationRecord updateStatus(UUID notificationId,
                                    com.banking.platform.notification.domain.NotificationStatus status);
    NotificationAttempt insertAttempt(UUID notificationId, int attemptNumber,
                                      com.banking.platform.notification.domain.NotificationStatus status,
                                      String providerReference, String errorCode);
    List<NotificationAttempt> findAttempts(UUID notificationId);
}
