package com.hammer.hammer.bid.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bid")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    private int currentBidPrice;

    private LocalDateTime createAt;

//    @OneToOne(mappedBy = "bid")
//    private Auction auction;

//    @OneToOne(mappedBy = "bid")
//    private User user;

}
