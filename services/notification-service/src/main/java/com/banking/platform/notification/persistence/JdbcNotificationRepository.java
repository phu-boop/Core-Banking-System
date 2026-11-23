package com.banking.platform.notification.persistence;

import com.banking.platform.notification.domain.NotificationAttempt;
import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationRequest;
import com.banking.platform.notification.domain.NotificationStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcNotificationRepository implements NotificationRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<NotificationRecord> notificationMapper = (rs, row) ->
            new NotificationRecord(
                    rs.getObject("notification_id", UUID.class),
                    rs.getString("notification_key"),
                    rs.getString("recipient_ref"),
                    rs.getString("template_code"),
                    rs.getString("channel"),
                    rs.getString("correlation_id"),
                    NotificationStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("updated_at").toInstant());

    private final RowMapper<NotificationAttempt> attemptMapper = (rs, row) ->
            new NotificationAttempt(
                    rs.getObject("attempt_id", UUID.class),
                    rs.getObject("notification_id", UUID.class),
                    rs.getInt("attempt_number"),
                    NotificationStatus.valueOf(rs.getString("status")),
                    rs.getString("provider_reference"),
                    rs.getString("error_code"),
                    rs.getTimestamp("attempted_at").toInstant());

    public JdbcNotificationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<NotificationRecord> findByKey(String notificationKey) {
        return jdbc.query("""
                SELECT * FROM notification_schema.notifications
                WHERE notification_key = ?
                """, notificationMapper, notificationKey).stream().findFirst();
    }

    @Override
    public NotificationRecord insert(NotificationRequest request) {
        UUID id = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO notification_schema.notifications
                (notification_id, notification_key, recipient_ref, template_code,
                 channel, correlation_id, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, id, request.notificationKey(), request.recipientRef(),
                request.templateCode(), request.channel(), request.correlationId(),
                NotificationStatus.REQUESTED.name());
        return find(id);
    }

    @Override
    public NotificationRecord find(UUID notificationId) {
        return jdbc.query("""
                SELECT * FROM notification_schema.notifications
                WHERE notification_id = ?
                """, notificationMapper, notificationId).stream()
                .findFirst()
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }

    @Override
    public NotificationRecord updateStatus(UUID notificationId, NotificationStatus status) {
        jdbc.update("""
                UPDATE notification_schema.notifications
                SET status = ?, updated_at = ?
                WHERE notification_id = ?
                """, status.name(), Timestamp.from(Instant.now()), notificationId);
        return find(notificationId);
    }

    @Override
    public NotificationAttempt insertAttempt(UUID notificationId, int attemptNumber,
                                             NotificationStatus status,
                                             String providerReference, String errorCode) {
        UUID attemptId = UUID.randomUUID();
        jdbc.update("""
                INSERT INTO notification_schema.notification_attempts
                (attempt_id, notification_id, attempt_number, status,
                 provider_reference, error_code)
                VALUES (?, ?, ?, ?, ?, ?)
                """, attemptId, notificationId, attemptNumber, status.name(),
                providerReference, errorCode);
        return new NotificationAttempt(attemptId, notificationId, attemptNumber,
                status, providerReference, errorCode, Instant.now());
    }

    @Override
    public List<NotificationAttempt> findAttempts(UUID notificationId) {
        return jdbc.query("""
                SELECT * FROM notification_schema.notification_attempts
                WHERE notification_id = ?
                ORDER BY attempt_number
                """, attemptMapper, notificationId);
    }

    public static class NotificationNotFoundException extends RuntimeException {
        public NotificationNotFoundException(UUID id) {
            super("Notification not found: " + id);
        }
    }
}
