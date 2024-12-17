package com.hammer.hammer.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Users")
//User는 예약어가 있어서 users라고 해야함
@Getter
@Setter
public class User {

    @Id
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId; // 로그인 ID (Primary Key)

    //왜래키 설정 -> 수정해야함 (report)
    @Column(name = "report_id")
    private Integer reportId; // 신고 ID

    @Column(name = "username", nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(name = "email", nullable = false, length = 100)
    private String email; // 이메일

    @Column(name = "password", nullable = false, length = 255)
    private String password; // 비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // 사용자 역할 (admin, seller, buyer)

    @Column(name = "loginLock")
    private boolean loginLock = false; // 로그인 잠금 여부

    @Column(name = "loginCnt")
    private int loginCnt = 0; // 로그인 시도 횟수

    @Column(name = "active")
    private boolean active = true; // 활성화 상태

    @Column(name = "lastLogin")
    private LocalDateTime lastLogin; // 마지막 로그인 시간

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
    public enum Role {
        ADMIN, SELLER, BUYER
    }
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Item> items;

   
}

