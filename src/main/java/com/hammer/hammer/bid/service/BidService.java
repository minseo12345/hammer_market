package com.hammer.hammer.bid.service;


import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.dto.ResponseBidByItemDto;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.exception.BidAmountTooLowException;
import com.hammer.hammer.bid.repository.BidRepository;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.transaction.service.TransactionService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final TransactionService transactionService;

    /**
     * 입찰 등록
     */
    @Transactional
    public void saveBid(RequestBidDto requestBidDto, UserDetails userDetails) {

        if (!requestBidDto.getUserId().toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        User user = userRepository.findById(requestBidDto.getUserId()).orElseThrow(
                ()-> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        Item item = itemRepository.findById(requestBidDto.getItemId()).orElseThrow(
                ()-> new IllegalStateException("상품을 찾을 수 없습니다.")
        );

        if (item.getStatus() != Item.ItemStatus.ONGOING) {
            throw new IllegalStateException("종료된 경매입니다.");
        }


        if(item.getUser().getUserId().equals(requestBidDto.getUserId())) {
            throw new IllegalStateException("판매자는 입찰을 등록할 수 없습니다.");
        }

        if (requestBidDto.getBidAmount().compareTo(user.getCurrentPoint()) > 0) {
            throw new IllegalArgumentException("사용자의 포인트가 부족합니다.");
        }


        BigDecimal currentHighestBid = bidRepository.findHighestBidByItemId(requestBidDto.getItemId())
                .orElse(BigDecimal.ZERO);

        if(requestBidDto.getBidAmount().compareTo(item.getBuyNowPrice())>0){
            throw new IllegalArgumentException("입찰 금액이 즉시구매가보다 클 수 없습니다.");
        }

        if(item.getStartingBid().compareTo(requestBidDto.getBidAmount())>0){
            throw new IllegalArgumentException("입찰 금액이 시작가보다 작을 수 없습니다.");
        }

        if (requestBidDto.getBidAmount().compareTo(currentHighestBid) <= 0) {
            throw new BidAmountTooLowException("입찰 금액이 현재 최고 입찰가보다 작습니다.");
        }

            Bid newBid = Bid.builder()
                    .user(user)
                    .item(item)
                    .bidAmount(requestBidDto.getBidAmount())
                    .bidTime(LocalDateTime.now())
                    .build();


        bidRepository.save(newBid);

        if(item.getBuyNowPrice().compareTo(requestBidDto.getBidAmount()) == 0){
            transactionService.createTransactionForImmediatePurchase(item.getItemId());
        }
    }


    /**
     * 입찰 등록
     */
    @Transactional
    public void saveBid(RequestBidDto requestBidDto, UserDetails userDetails, String buyNow) {

        if (!requestBidDto.getUserId().toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        User user = userRepository.findById(requestBidDto.getUserId()).orElseThrow(
                ()-> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        Item item = itemRepository.findById(requestBidDto.getItemId()).orElseThrow(
                ()-> new IllegalStateException("상품을 찾을 수 없습니다.")
        );

        if (item.getStatus() != Item.ItemStatus.ONGOING) {
            throw new IllegalStateException("종료된 경매입니다.");
        }


        if(item.getUser().getUserId().equals(requestBidDto.getUserId())) {
            throw new IllegalStateException("판매자는 입찰을 등록할 수 없습니다.");
        }

        if (requestBidDto.getBidAmount().compareTo(user.getCurrentPoint()) > 0) {
            throw new IllegalArgumentException("사용자의 포인트가 부족합니다.");
        }

        BigDecimal currentHighestBid = bidRepository.findHighestBidByItemId(requestBidDto.getItemId())
                .orElse(BigDecimal.ZERO);

        if(requestBidDto.getBidAmount().compareTo(item.getBuyNowPrice())>0){
            throw new IllegalArgumentException("입찰 금액이 즉시구매가보다 클 수 없습니다.");
        }

        if(item.getStartingBid().compareTo(requestBidDto.getBidAmount())>0){
            throw new IllegalArgumentException("입찰 금액이 시작가보다 작을 수 없습니다.");
        }

        if (requestBidDto.getBidAmount().compareTo(currentHighestBid) <= 0) {
            throw new BidAmountTooLowException("입찰 금액이 현재 최고 입찰가보다 작습니다.");
        }

        Bid newBid = Bid.builder()
                .user(user)
                .item(item)
                .bidAmount(requestBidDto.getBidAmount())
                .bidTime(LocalDateTime.now())
                .build();


        bidRepository.save(newBid);
    }

    /**
     *  사용자 별 입찰 조회
     */
    @Transactional(readOnly = true)
    public Page<ResponseBidByUserDto> getBidsByUser(Long userId,
                                                    Pageable pageable,
                                                    String sort,
                                                    String itemName,
                                                    UserDetails userDetails) {

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        Sort sortOrder = getSortOrder(sort);

        Pageable sortedPageable = getSortedPageable(pageable, sortOrder);

        Page<Bid> bids = findBidsByUserWithItemSearch(userId, sortedPageable, itemName);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");


        return bids.map(bid -> {
            BigDecimal currentPrice = bidRepository.findHighestBidByItemId(bid.getItem().getItemId())
                    .orElse(BigDecimal.ZERO);

            String formattedMyPrice = decimalFormat.format(bid.getBidAmount()) + "원";
            String formattedCurrentPrice = decimalFormat.format(currentPrice) + "원";

            return ResponseBidByUserDto.builder()
                    .itemId(bid.getItem().getItemId())
                    .itemName(bid.getItem().getTitle())
                    .img(bid.getItem().getFileUrl())
                    .myPrice(formattedMyPrice)
                    .currentPrice(formattedCurrentPrice)
                    .build();
        });
    }



    /**
     *  상품 별 입찰 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseBidByItemDto> getBidsByItem(Long itemId) {

        List<Bid> bids = bidRepository.findByItem_ItemIdOrderByBidAmountDesc(itemId)
                .orElseThrow(() -> new IllegalStateException("입찰 데이터를 찾을 수 없습니다."));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalStateException("상품을 찾을 수 없습니다."));

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        return bids.stream().map(bid -> {
            String formattedBidAmount = decimalFormat.format(bid.getBidAmount()) + "원";
            return ResponseBidByItemDto.builder()
                    .userId(bid.getUser().getUserId())
                    .bidAmount(formattedBidAmount)
                    .title(bid.getItem().getTitle())
                    .description(bid.getItem().getDescription())
                    .itemName(bid.getItem().getTitle())
                    .createAt(bid.getItem().getCreatedAt())
                    .username(bid.getUser().getUsername())
                    .imageUrl(item.getFileUrl())
                    .build();
        }).collect(Collectors.toList());
    }


    /**
     * 정렬 조건 반환 메서드
     */
    public Sort getSortOrder(String sort) {
        if ("price_asc".equals(sort)) {
            return Sort.by(Sort.Order.asc("bidAmount"));
        } else if ("price_desc".equals(sort)) {
            return Sort.by(Sort.Order.desc("bidAmount"));
        }
        return Sort.by(Sort.Order.desc("bidAmount"));
    }


    /**
     * 페이지네이션 처리 메서드
     */
    private Pageable getSortedPageable(Pageable pageable, Sort sortOrder) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOrder);
    }

    //최고 입찰금액 조회
    public BigDecimal getHighestBidAmount(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(()-> new IllegalStateException("상품을 찾을 수 없습니다."));
        return bidRepository.findHighestBidByItemId(itemId).orElse(item.getStartingBid());
    }

    /**
     * 검색 기능 메서드
     */
    private Page<Bid> findBidsByUserWithItemSearch(Long userId, Pageable pageable, String itemName) {
        if (!itemName.isEmpty()) {
            return bidRepository.findByItem_TitleContainingIgnoreCase(itemName, pageable)
                    .orElseThrow(() -> new IllegalStateException("상품을 찾을 수 없습니다."));
        }
        return bidRepository.findByUser_UserId(userId, pageable)
                .orElseThrow(() -> new IllegalStateException("입찰 데이터를 찾을 수 없습니다."));
    }
}
