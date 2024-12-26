package com.hammer.hammer.user.entity;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.transaction.entity.Transaction;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "Users")
//User는 예약어가 있어서 users라고 해야함
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", length = 50)
    private Long userId; // 로그인 ID (Primary Key)

    //왜래키 설정 -> 수정해야함 (report)
    @Column(name = "report_id")
    private Long reportId; // 신고 ID

    @Column(name = "username", nullable = false, length = 50)
    private String username; // 사용자 이름

    @Column(name = "email", nullable = false, length = 100)
    private String email; // 이메일

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactionsAsBuyer; // 구매자로 참조된 거래

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactionsAsSeller; // 판매자로 참조된 거래

    @Column(name = "password", nullable = false, length = 255)
    private String password; // 비밀번호

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

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Item> items;

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
        /*
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        */
        /*
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        */
    }

    @Override // 사용자의 패스워드를 반환
    public String getPassword() {
        return this.password;
    }
    
    public String getName() { // 만약 다른 이름이 필요하다면 추가 가능
        return username;
    }

    @Override // 사용자의 id를 반환(고유 값)
    public String getUsername() {
        return this.email;
    }

    @Override // 계정 만료 여부
    public boolean isAccountNonExpired() {
        return true; // UserDetails.super.isAccountNonExpired();
    }

    @Override // 계정 잠금 여부
    public boolean isAccountNonLocked() {
        return true; // UserDetails.super.isAccountNonLocked();
    }

    @Override // 패스워드 만료 여부
    public boolean isCredentialsNonExpired() {
        return true; // UserDetails.super.isCredentialsNonExpired();
    }

    @Override // 계정 활성화 여부
    public boolean isEnabled() {
        return true; // UserDetails.super.isEnabled();
    }
}
