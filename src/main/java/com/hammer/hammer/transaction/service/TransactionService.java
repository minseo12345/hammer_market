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
import java.text.DecimalFormat;


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

    private void createTransaction(Item item) {
        Bid bid = (Bid) bidRepository.findTopByItem_ItemIdOrderByBidAmountDesc(item.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 입찰을 찾을 수 없습니다. 아이템 ID: " + item.getItemId()));

        /*//유찰 시 처리
        if (bid == null) {
            // 입찰자가 없는 경우 아이템을 CANCELLED로 처리
            item.setStatus(Item.ItemStatus.CANCELLED);
            itemRepository.save(item);

            // 판매자에게 유찰 알림 생성
            String sellerMessage = String.format("등록하신 %d 상품이 유찰(CANCELLED)되었습니다.", item.getItemId());
            Notification sellerNotification = new Notification(item.getUser().getUserId(), item, sellerMessage);
            notificationRepository.save(sellerNotification);

            // WebSocket을 통해 유찰 알림 전송
            messagingTemplate.convertAndSend("/topic/notifications", sellerNotification);
            return;
        }*/

        // 낙찰자 (입찰자) 정보
        User buyer = bid.getUser();

        // 낙찰 금액
        BigDecimal finalPrice = bid.getBidAmount();

        // 거래 생성
        Transaction transaction = new Transaction(buyer, item.getUser(),item);
        transaction.setFinalPrice(finalPrice);
        transaction.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime());  // 거래 시점
        transactionRepository.save(transaction);
        System.out.println("아이템 ID: " + item.getItemId() + "에 대한 거래 생성 성공");
        // 아이템 상태를 '낙찰'로 변경
        item.setStatus(Item.ItemStatus.BIDDING_END);
        itemRepository.save(item);

        // 판매자 알림 생성
        DecimalFormat df = new DecimalFormat("#");
        String sellerMessage = String.format(
                "등록하신 '%s' 상품이 %s원으로 판매되었습니다! 구매자ID : %s",
                item.getTitle(),
                df.format(transaction.getFinalPrice()),
                transaction.getBuyer().getUsername()
        );
        Notification sellerNotification = new Notification(transaction.getSeller().getUserId(), item, sellerMessage);
        notificationRepository.save(sellerNotification);

        // WebSocket을 통해 판매자에게 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications", sellerNotification);
        System.out.println("아이템 ID: " + item.getItemId() + "에 대한 거래 생성 알림 발송");

        // 구매자 알림 생성
        String buyerMessage = String.format(
                "입찰하신 '%s' 상품이 %s원으로 낙찰되었습니다! 판매자ID: %s",
                item.getTitle(), // 상품 이름으로 변경
                df.format(transaction.getFinalPrice()),
                transaction.getSeller().getUsername()
        );
        Notification buyerNotification = new Notification(transaction.getBuyer().getUserId(), item, buyerMessage);
        notificationRepository.save(buyerNotification);

// WebSocket을 통해 구매자에게 알림 전송
        messagingTemplate.convertAndSend("/topic/notifications", buyerNotification);
        System.out.println("아이템 이름: " + item.getTitle() + "에 대한 거래 생성 알림 발송");
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
        // 경매 진행 중인 아이템을 조회
        List<Item> ongoingItems = itemRepository.findByStatus(Item.ItemStatus.ONGOING);
        System.out.println("현재 진행 중인 아이템 수: " + ongoingItems.size());

        //상태가 경매중인 아이템에 대해서 조회
        for (Item item : ongoingItems) {
            createTransactionForAuctionEnd(item.getItemId());
        }
    }
}