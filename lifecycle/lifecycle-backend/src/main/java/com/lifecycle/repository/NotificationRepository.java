package com.lifecycle.repository;
import com.lifecycle.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndMessage(Long userId, String message);
}
