package com.hammer.hammer.transaction.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.auction.entity.Item;
import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.repository.BidRepository;
import com.hammer.hammer.auction.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

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

    // 트랜잭션 삭제
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    // 거래 생성 로직 (즉시구매와 경매시간마감 공통)
    private void createTransaction(Item item) {
        // 낙찰자 조회 (가장 높은 bid_amount)
        Bid bid = bidRepository.findTopByItemIdOrderByBidAmountDesc(item.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 입찰을 찾을 수 없습니다. 아이템 ID: " + item.getItemId()));

        // 낙찰자와 판매자 정보 설정
        Transaction transaction = new Transaction();
        transaction.setItem(item);
        transaction.setBuyer(bid.getUser());
        transaction.setSeller(item.getUser());
        transaction.setFinalPrice(bid.getBidAmount());
        transaction.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));  // 거래 시점

        // 트랜잭션 생성
        transactionRepository.save(transaction);

        // 아이템 상태를 '낙찰'로 변경
        item.setStatus(Item.AuctionStatus.BIDDING_END);
        itemRepository.save(item);
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
        List<Item> ongoingItems = itemRepository.findByStatus(Item.AuctionStatus.ONGOING);

        // 각 아이템에 대해 경매 종료 시점을 확인
        for (Item item : ongoingItems) {
            if (item.getEndTime().isBefore(LocalDateTime.now())) {
                // 경매 종료 시점이 지나면 아이템 상태를 '낙찰'로 변경하고 거래 생성
                createTransactionForAuctionEnd(item.getItemId());
            }
        }
    }
}