package com.hammer.hammer.notification.repository;

import com.sun.nio.sctp.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자의 모든 알림 조회
    List<Notification> findByUserId(Long userId);

    // 특정 사용자의 미확인 알림 조회
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}
