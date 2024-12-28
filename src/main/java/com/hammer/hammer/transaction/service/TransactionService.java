package com.hammer.hammer.transaction.service;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.notification.repository.NotificationRepository;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.notification.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hammer.hammer.transaction.entity.Transaction;

import com.hammer.hammer.bid.repository.BidRepository;
import com.hammer.hammer.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 모든 트랜잭션 조회
    @Transactional(readOnly = true)
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    // ID로 트랜잭션 조회
    @Transactional(readOnly = true)
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    /*// 트랜잭션 삭제
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }*/

    private void createTransaction(Item item) {
        Bid bid = (Bid) bidRepository.findTopByItem_ItemIdOrderByBidAmountDesc(item.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 입찰을 찾을 수 없습니다. 아이템 ID: " + item.getItemId()));

        // 낙찰자 (입찰자) 정보
        User buyer = bid.getUser();

        // 낙찰 금액
        BigDecimal finalPrice = bid.getBidAmount();

        // 거래 생성
        Transaction transaction = new Transaction(buyer, item.getUser(),item);
        transaction.setFinalPrice(finalPrice);
        transaction.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime());  // 거래 시점
        transactionRepository.save(transaction);

        // 아이템 상태를 '낙찰'로 변경
        item.setStatus(Item.ItemStatus.BIDDING_END);
        itemRepository.save(item);

        // 판매자 알림 생성
        String sellerMessage = String.format("등록하신 %d상품이 %s원으로 판매되었습니다! 구매자ID : %s",
                item.getItemId(), transaction.getFinalPrice(), transaction.getBuyer().getUsername());
        Notification sellerNotification = new Notification(transaction.getSeller().getUserId(), item.getItemId(), sellerMessage);
        notificationRepository.save(sellerNotification);

        // WebSocket을 통해 판매자에게 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications", sellerNotification);

        // 구매자 알림 생성
        String buyerMessage = String.format("입찰하신 %d상품이 %s원으로 낙찰되었습니다! 판매자ID: %s",
                item.getItemId(), transaction.getFinalPrice(), transaction.getSeller().getUsername());
        Notification buyerNotification = new Notification(transaction.getBuyer().getUserId(), item.getItemId(), buyerMessage);
        notificationRepository.save(buyerNotification);

        // WebSocket을 통해 구매자에게 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications", buyerNotification);

    }

    // 즉시구매에 의한 낙찰
    @Transactional
    public void createTransactionForImmediatePurchase(Long itemId) {
        // item 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다. 아이템 ID: " + itemId));

        createTransaction(item);

    }

    // 경매시간마감에 의한 낙찰 (1분마다 스케줄러로 실행)
    @Transactional
    public void createTransactionForAuctionEnd(Long itemId) {
        // item 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다. 아이템 ID: " + itemId));

        // 경매 종료 시점 검증
        if (item.getEndTime().isBefore(LocalDateTime.now())) {
            // 경매 종료 후 거래 생성
            createTransaction(item);
        }
    }

    // 1분마다 경매 종료된 아이템을 확인하는 스케줄러
    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void checkAuctionEndAndUpdateStatus() {
        System.out.println("스케줄러 실행 시각: " + LocalDateTime.now());

        // 경매 진행 중인 아이템을 조회
        List<Item> ongoingItems = itemRepository.findByStatus(Item.ItemStatus.ONGOING);
        System.out.println("현재 진행 중인 아이템 수: " + ongoingItems.size());

        // 각 아이템에 대해 경매 종료 시점을 확인
        for (Item item : ongoingItems) {
            System.out.println("아이템 ID: " + item.getItemId() + ", 종료 시각: " + item.getEndTime());

            if (item.getEndTime().isBefore(LocalDateTime.now())) {
                System.out.println("아이템 ID: " + item.getItemId() + "는 경매 종료 시각을 초과했습니다. 상태를 변경하고 거래를 생성합니다.");
                try {
                    createTransactionForAuctionEnd(item.getItemId());
                    System.out.println("아이템 ID: " + item.getItemId() + "에 대한 거래 생성 성공");
                } catch (Exception e) {
                    System.err.println("아이템 ID: " + item.getItemId() + "에 대한 거래 생성 실패: " + e.getMessage());
                }
            } else {
                System.out.println("아이템 ID: " + item.getItemId() + "는 아직 경매 종료 시각이 되지 않았습니다.");
            }
        }
    }
}