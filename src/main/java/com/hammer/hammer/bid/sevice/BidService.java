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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @CachePut(value = "bid",key = "#requestBidDto.itemId")
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

            Bid newBide = Bid.builder()
                    .user(user)
                    .item(item)
                    .bidAmount(requestBidDto.getBidAmount())
                    .bidTime(requestBidDto.getBidDate())
                    .build();

        List<Bid> bids = bidRepository.findByItemId(requestBidDto.getItemId()).orElseThrow(
                ()-> new IllegalStateException("입찰 목록을 찾을 수 없습니다.")
        );

        bids.add(newBide);

        bidRepository.save(newBide);
    }

    /**
     * 입찰 조회
     */
    @Cacheable(value = "bid", key = "#userId")
    public List<Bid> getBidsByItemId(Long userId) {
       List<Bid> bids = bidRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalStateException("입찰 데이터를 찾을 수 없습니다.")
        );

        return bids;
    }
}
