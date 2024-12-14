package com.hammer.hammer.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.auction.entity.Item;
import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.repository.BidRepository;
import com.hammer.hammer.auction.repository.ItemRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;

    public TransactionService(TransactionRepository transactionRepository, BidRepository bidRepository, ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.bidRepository = bidRepository;
        this.itemRepository = itemRepository;
    }

    // 모든 트랜젝션 조회
    @Transactional(readOnly = true)
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    // ID로 트랜젝션 조회
    @Transactional(readOnly = true)
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // 트랜젝션 삭제
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    // 거래 생성 (경매 종료 시점)
    @Transactional
    public void createTransactionForAuctionEnd(Long itemId) {
        // item 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다. 아이템 ID: " + itemId));

        // 경매 종료 시점 검증
        if (item.getEndTime().isBefore(LocalDateTime.now())) {
            // 낙찰자 조회 (가장 높은 bid_amount)
            Bid bid = bidRepository.findTopByItemIdOrderByBidAmountDesc(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 입찰을 찾을 수 없습니다. 아이템 ID: " + itemId));

            // 낙찰자와 판매자 정보 설정
            Transaction transaction = new Transaction();
            transaction.setItem(item);
            transaction.setBuyer(bid.getUser());
            transaction.setSeller(auction.getUser());
            transaction.setFinalPrice(bid.getBidAmount());
            transaction.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));  // 거래 시점

            // 트랜잭션 생성
            transactionRepository.save(transaction);

            // 아이템 상태를 '낙찰'로 변경
            item.setStatus(Item.AuctionStatus.BIDDING_END);
            itemRepository.save(item);
        }
    }
}

