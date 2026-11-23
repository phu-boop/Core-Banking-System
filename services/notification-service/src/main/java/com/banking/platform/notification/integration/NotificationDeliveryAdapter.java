package com.banking.platform.notification.integration;

import com.banking.platform.notification.domain.NotificationRecord;

public interface NotificationDeliveryAdapter {
    NotificationDelivery deliver(NotificationRecord notification);
}
