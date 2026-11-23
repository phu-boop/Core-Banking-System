package com.banking.platform.notification.web;

import com.banking.platform.notification.application.NotificationService;
import com.banking.platform.notification.domain.NotificationAttempt;
import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public NotificationRecord request(@RequestBody NotificationRequest request) {
        return service.request(request);
    }

    @PostMapping("/{notificationId}/deliver")
    public NotificationRecord deliver(@PathVariable UUID notificationId) {
        return service.deliver(notificationId);
    }

    @GetMapping("/{notificationId}")
    public NotificationRecord get(@PathVariable UUID notificationId) {
        return service.get(notificationId);
    }

    @GetMapping("/{notificationId}/attempts")
    public List<NotificationAttempt> attempts(@PathVariable UUID notificationId) {
        return service.attempts(notificationId);
    }
}
