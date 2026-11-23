package com.banking.platform.notification.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.notification.domain.NotificationAttempt;
import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationRequest;
import com.banking.platform.notification.domain.NotificationStatus;
import com.banking.platform.notification.integration.NotificationDelivery;
import com.banking.platform.notification.integration.NotificationDeliveryAdapter;
import com.banking.platform.notification.persistence.NotificationRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationServiceTests {
    private NotificationRepository repository;
    private NotificationDeliveryAdapter adapter;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationRepository.class);
        adapter = mock(NotificationDeliveryAdapter.class);
        service = new NotificationService(repository, adapter);
    }

    @Test
    void duplicateRequestReplaysExistingNotification() {
        NotificationRequest request = request();
        NotificationRecord existing = record(NotificationStatus.REQUESTED);
        when(repository.findByKey(request.notificationKey()))
                .thenReturn(Optional.of(existing));

        NotificationRecord result = service.request(request);

        assertEquals(existing, result);
        verify(repository, never()).insert(request);
    }

    @Test
    void terminalDeliveryIsNotAttemptedAgain() {
        NotificationRecord delivered = record(NotificationStatus.DELIVERED);
        when(repository.find(delivered.notificationId())).thenReturn(delivered);

        NotificationRecord result = service.deliver(delivered.notificationId());

        assertEquals(delivered, result);
        verify(adapter, never()).deliver(delivered);
    }

    @Test
    void failedDeliveryRecordsAttemptWithoutChangingFinancialState() {
        NotificationRecord requested = record(NotificationStatus.REQUESTED);
        when(repository.find(requested.notificationId())).thenReturn(requested);
        when(repository.findAttempts(requested.notificationId())).thenReturn(List.of());
        when(adapter.deliver(requested)).thenReturn(
                new NotificationDelivery(NotificationStatus.FAILED, null, "MOCK_FAILED"));
        when(repository.updateStatus(requested.notificationId(), NotificationStatus.FAILED))
                .thenReturn(record(NotificationStatus.FAILED));

        NotificationRecord result = service.deliver(requested.notificationId());

        assertEquals(NotificationStatus.FAILED, result.status());
        verify(repository).insertAttempt(requested.notificationId(), 1,
                NotificationStatus.FAILED, null, "MOCK_FAILED");
    }

    @Test
    void requiredFieldsAreValidated() {
        assertThrows(IllegalArgumentException.class,
                () -> service.request(new NotificationRequest(
                        "", "recipient", "template", "EMAIL", "")));
    }

    private NotificationRequest request() {
        return new NotificationRequest(
                "notify-1", "recipient-1", "TRANSFER_COMPLETED",
                "EMAIL", "corr-1");
    }

    private NotificationRecord record(NotificationStatus status) {
        Instant now = Instant.now();
        return new NotificationRecord(UUID.randomUUID(), "notify-1",
                "recipient-1", "TRANSFER_COMPLETED", "EMAIL",
                "corr-1", status, now, now);
    }
}
