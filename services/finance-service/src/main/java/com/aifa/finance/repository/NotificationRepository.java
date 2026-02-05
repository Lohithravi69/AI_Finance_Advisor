package com.aifa.finance.repository;

import com.aifa.finance.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndNotificationType(Long userId, String notificationType);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdSince(Long userId, LocalDateTime since);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(Long userId);

    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime cutoffDate);
}
