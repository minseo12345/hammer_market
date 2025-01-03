package com.hammer.hammer.notification.service;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.notification.entity.Notification;
import com.hammer.hammer.notification.repository.NotificationRepository;
import com.hammer.hammer.point.service.PointService;
import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final PointService pointService;

    // 거래 확인 이벤트 처리
    public void handleTransactionComplete(Long transactionId, Long userId) {
        // 거래 확인
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래를 찾을 수 없습니다. ID: " + transactionId));

        Item item = transaction.getItem();

        // 현재 상태 확인
        if (item.getStatus() == Item.ItemStatus.CANCELLED) {
            throw new IllegalStateException("취소된 거래는 완료할 수 없습니다.");
        }

        if (item.getStatus() == Item.ItemStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 거래입니다.");
        }

        // 거래 완료 상태 변경
        if (transaction.getModifiedBy().contains(userId)) {
            throw new IllegalStateException("이미 거래 완료 요청을 한 사용자입니다.");
        }

        // 1명만 거래확인 선택시
        if (item.getStatus() == Item.ItemStatus.BIDDING_END) {
            item.setStatus(Item.ItemStatus.WAITING_FOR_OTHER_APPROVAL);
            transaction.addModifiedBy(userId); // 변경한 사용자 추가

        } else if (item.getStatus() == Item.ItemStatus.WAITING_FOR_OTHER_APPROVAL ||
                item.getStatus() == Item.ItemStatus.WAITING_FOR_MY_APPROVAL) {
            // 상대방의 상태 확인
            if (item.getStatus() == Item.ItemStatus.WAITING_FOR_OTHER_APPROVAL && !transaction.getModifiedBy().contains(userId)) {
                item.setStatus(Item.ItemStatus.COMPLETED);
            } else if (item.getStatus() == Item.ItemStatus.WAITING_FOR_MY_APPROVAL && !transaction.getModifiedBy().contains(userId)) {
                item.setStatus(Item.ItemStatus.COMPLETED);
            } else {
                throw new IllegalStateException("이미 거래 완료 요청을 한 사용자입니다.");
            }
            transaction.addModifiedBy(userId); // 변경한 사용자 추가

            // 포인트 처리
            pointService.processTransactionPoints(transaction);

            // 최종 거래 완료 알림 생성
            String completeMessage = String.format("%d상품의 거래가 완료되었습니다.", transactionId);
            Notification notification = new Notification(userId, item, completeMessage);
//            notificationRepository.save(notification);

            // WebSocket으로 실시간 알림 전송 ( 거래 완료 )
            messagingTemplate.convertAndSend("/topic/notifications", notification);
        }
        itemRepository.save(item);
    }

    // 거래 포기 이벤트 처리
    public void handleTransactionCancel(Long transactionId, Long userId) {
        // 거래 확인
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래를 찾을 수 없습니다. ID: " + transactionId));

        // 아이템 상태를 CANCELLED로 변경
        Item item = transaction.getItem();
        item.setStatus(Item.ItemStatus.CANCELLED);
        itemRepository.save(item);

        // 거래 포기 알림 생성
        String cancelMessage = String.format("[%d]상품의 거래가 취소되었습니다.", transactionId);
        Notification notification = new Notification(userId, item, cancelMessage);
        //notificationRepository.save(notification);

        // WebSocket으로 실시간 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    // 현재 사용자의 모든 알림 목록 조회
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    // WebSocket으로 알림 전송
    public void sendNotificationsToClient(Long userId) {
        List<Notification> notifications = getNotificationsByUserId(userId);
        messagingTemplate.convertAndSend("/topic/notifications", notifications);
    }


    public void updateReadStatus(Long id, boolean isRead) {
        System.out.println("Updating notification with ID: " + id + " to isRead: " + isRead);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        notification.setRead(isRead);
        notificationRepository.save(notification);
    }

}
