package com.hammer.hammer.bid.sevice;

import com.hammer.hammer.bid.Repository.BidRepository;
import com.hammer.hammer.bid.Repository.ItemRepository;
import com.hammer.hammer.bid.Repository.UserRepository;
import com.hammer.hammer.bid.domain.Bid;
import com.hammer.hammer.bid.domain.Item;
import com.hammer.hammer.bid.domain.User;
import com.hammer.hammer.bid.dto.RequestBidDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * 입찰 등록
     */
    @Transactional
    public void saveBid(RequestBidDto requestBidDto){

        User user = userRepository.findById(requestBidDto.getUserId()).orElseThrow(
                ()-> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );
        Item item = itemRepository.findById(requestBidDto.getItemId()).orElseThrow(
                ()-> new IllegalStateException("상품을 찾을 수 없습니다.")
        );
        // 100원 단위 검증 로직
        if (requestBidDto.getBidAmount().remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("입찰 금액은 100원 단위로 입력해야 합니다.");
        }

        BigDecimal currentHighestBid = bidRepository.findHighestBidByItemId(requestBidDto.getItemId())
                .orElse(BigDecimal.ZERO);

        if (requestBidDto.getBidAmount().compareTo(currentHighestBid) <= 0) {
            throw new IllegalArgumentException("입찰 금액은 현재 최고 입찰가보다 커야 합니다.");
        }

            Bid newBid = Bid.builder()
                    .user(user)
                    .item(item)
                    .bidAmount(requestBidDto.getBidAmount())
                    .bidTime(requestBidDto.getBidDate())
                    .build();


        bidRepository.save(newBid);
        updateFirstPageCache(requestBidDto.getItemId());

    }

    /**
     *  사용자 별 입찰 조회
     */
    @Cacheable(value = "bidsByUser", key = "#userId + '_' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<Bid> getBidsByUser(Long userId, Pageable pageable) {

       Page<Bid> bids = bidRepository.findByUserIdOrderByBidAmountDesc(userId,pageable).orElseThrow(
                () -> new IllegalStateException("입찰 데이터를 찾을 수 없습니다.")
        );

        return bids;
    }

    /**
     *  상품 별 입찰 조회
     */
    @Cacheable(value = "bidsByItem", key = "#itemId + '_0' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<Bid> getBidsByItem(Long itemId,Pageable pageable) {

        Page<Bid> bids = bidRepository.findByItemIdOrderByBidAmountDesc(itemId, pageable).orElseThrow(
                () -> new IllegalStateException("상품 데이터를 찾을 수 없습니다.")
        );

        return bids;
    }

    @CachePut(value = "bidsByItem", key = "#itemId + '_0'")
    public Page<Bid> updateFirstPageCache(Long itemId) {
        Pageable firstPage = Pageable.ofSize(10).withPage(0);
        return bidRepository.findByItemIdOrderByBidAmountDesc(itemId, firstPage)
                .orElseThrow(() -> new IllegalStateException("첫 페이지 데이터를 찾을 수 없습니다."));
    }
}
