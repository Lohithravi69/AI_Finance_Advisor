package com.aifa.finance.service;

import com.aifa.finance.domain.Notification;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.NotificationRequest;
import com.aifa.finance.dto.NotificationResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.NotificationRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationResponse createNotification(Long userId, NotificationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
            .user(user)
            .notificationType(Notification.NotificationType.valueOf(request.notificationType()))
            .title(request.title())
            .message(request.message())
            .priority(Notification.NotificationPriority.valueOf(request.priority()))
            .referenceId(request.referenceId())
            .referenceType(request.referenceType())
            .actionUrl(request.actionUrl())
            .isRead(false)
            .isSent(false)
            .build();

        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
            .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getNotificationType().name(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getIsRead(),
            notification.getIsSent(),
            notification.getPriority().name(),
            notification.getReferenceId(),
            notification.getReferenceType(),
            notification.getActionUrl(),
            notification.getCreatedAt(),
            notification.getReadAt(),
            notification.getSentAt()
        );
    }
}
