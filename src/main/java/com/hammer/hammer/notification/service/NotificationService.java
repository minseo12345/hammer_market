package com.hammer.hammer.notification.service;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.notification.entity.Notification;
import com.hammer.hammer.notification.repository.NotificationRepository;
import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final ItemRepository itemRepository;

    // 거래포기 이벤트 처리
    public void handleTransactionCancel(Long transactionId, Long userId) {
        // 거래 확인
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래를 찾을 수 없습니다. ID: " + transactionId));

        // 아이템 상태를 CANCELLED로 변경
        Item item = transaction.getItem();
        item.setStatus(Item.ItemStatus.CANCELLED);
        itemRepository.save(item);

        // 알림 생성
        String cancelMessage = String.format("[%d] 거래가 취소되었습니다.", transactionId);
        Notification notification = new Notification(userId, transaction.getItem().getItemId(), cancelMessage);
        notificationRepository.save(notification);
    }

    // 거래완료 이벤트 처리
    public void handleTransactionComplete(Long transactionId, Long userId) {
        // 거래 확인
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래를 찾을 수 없습니다. ID: " + transactionId));

        // 아이템 상태 업데이트 (거래완료)
        Item item = transaction.getItem();
        item.setStatus(Item.ItemStatus.COMPLETED);
        itemRepository.save(item);

        // 알림 생성
        String completeMessage = String.format("[%d] 거래가 완료되었습니다.", transactionId);
        Notification notification = new Notification(userId, item.getItemId(), completeMessage);
        notificationRepository.save(notification);
    }

    public void saveNotification(Notification notification) {
        if (notification != null) {
            // 알림 객체를 데이터베이스에 저장
            notificationRepository.save(notification);
        }
    }

    // 특정 사용자의 읽지 않은 알림 여부 확인
    @Transactional(readOnly = true)
    public boolean hasUnreadNotifications(Long userId) {
        return !notificationRepository.findByUserIdAndIsReadFalse(userId).isEmpty();
    }

    // 알림 읽음 상태 변경
    public void markNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    // 특정 사용자의 알림 목록 조회
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
}
