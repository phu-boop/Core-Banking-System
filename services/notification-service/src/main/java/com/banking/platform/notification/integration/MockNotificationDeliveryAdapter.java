package com.banking.platform.notification.integration;

import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationStatus;
import org.springframework.stereotype.Component;

@Component
public class MockNotificationDeliveryAdapter implements NotificationDeliveryAdapter {
    @Override
    public NotificationDelivery deliver(NotificationRecord notification) {
        if ("FAIL".equalsIgnoreCase(notification.templateCode())) {
            return new NotificationDelivery(
                    NotificationStatus.FAILED, null, "MOCK_DELIVERY_FAILED");
        }
        return new NotificationDelivery(
                NotificationStatus.DELIVERED,
                "mock-" + notification.notificationId(),
                null);
    }
}
