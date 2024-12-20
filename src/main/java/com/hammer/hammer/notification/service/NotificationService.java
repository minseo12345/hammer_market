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

        // 거래포기 처리 (거래 삭제)
        transactionRepository.delete(transaction);

/*        // 알림 생성
        String cancelMessage = String.format("[%d] 거래가 취소되었습니다.", transactionId);
        Notification notification = new Notification(userId, transaction.getItem().getItemId(), cancelMessage, "거래 취소");
        notificationRepository.save(notification);*/
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

/*        // 알림 생성
        String completeMessage = String.format("[%d] 거래가 완료되었습니다.", transactionId);
        Notification notification = new Notification(userId, item.getItemId(), completeMessage, "거래 완료");
        notificationRepository.save(notification);*/
    }
}
