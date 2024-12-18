package com.hammer.hammer.bid.sevice;

import com.hammer.hammer.bid.Repository.BidRepository;
import com.hammer.hammer.bid.Repository.ItemRepository;
import com.hammer.hammer.bid.Repository.UserRepository;
import com.hammer.hammer.bid.domain.Bid;
import com.hammer.hammer.bid.domain.Item;
import com.hammer.hammer.bid.domain.User;
import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.dto.ResponseBidByItemDto;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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
    }

    /**
     *  사용자 별 입찰 조회
     */
    @Transactional(readOnly = true)
    public Page<ResponseBidByUserDto> getBidsByUser(String userId, Pageable pageable) {

       Page<Bid> bids = bidRepository.findByUserIdOrderByBidAmountDesc(userId,pageable).orElseThrow(
                () -> new IllegalStateException("입찰 데이터를 찾을 수 없습니다.")
        );

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        return bids.map(bid -> {

            BigDecimal currentPrice = bidRepository.findHighestBidByItemId(bid.getItem().getId())
                    .orElse(BigDecimal.ZERO);


            String formattedMyPrice = decimalFormat.format(bid.getBidAmount()) + " 원";
            String formattedCurrentPrice = decimalFormat.format(currentPrice) + " 원";

            return ResponseBidByUserDto.builder()
                    .itemId(bid.getItem().getId())
//                    .itemName(bid.getItem().getItemName())
//                    .img(bid.getItem().getImg())
                    .myPrice(formattedMyPrice)
                    .currentPrice(formattedCurrentPrice)
                    .build();
        });
    }

    /**
     *  상품 별 입찰 조회
     */
    @Transactional(readOnly = true)
    public Page<ResponseBidByItemDto> getBidsByItem(Long itemId, Pageable pageable) {

        Page<Bid> bids = bidRepository.findByItemIdOrderByBidAmountDesc(itemId, pageable).orElseThrow(
                () -> new IllegalStateException("상품 데이터를 찾을 수 없습니다.")
        );

        return bids.map(bid -> ResponseBidByItemDto.builder()
                .userId(bid.getUser().getId())
                .bidAmount(bid.getBidAmount())
                .build());
    }

}
