package com.hammer.hammer.user.entity;

import com.hammer.hammer.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "Users")
//User는 예약어가 있어서 users라고 해야함
@Getter
@Setter
@Builder
public class User {

    @Id
    @Column(name = "user_id", nullable = false, length = 50)
    private Long userId; // 로그인 ID (Primary Key)

    //왜래키 설정 -> 수정해야함 (report)
    @Column(name = "report_id")
    private Long reportId; // 신고 ID

    @Column(name = "username", nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(name = "email", nullable = false, length = 100)
    private String email; // 이메일

    @Column(name = "password", nullable = false, length = 255)
    private String password; // 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // 사용자 역할 (admin, seller, buyer)

    @Column(name = "phone_number")
    private String phonenumber; // 사용자 전화번호

    @Builder.Default
    @Column(name = "active")
    private boolean active = true; // 활성화 상태


    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    // Enum for Role

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Item> items;
}
