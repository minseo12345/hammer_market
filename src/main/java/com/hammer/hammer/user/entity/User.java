package com.hammer.hammer.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;  // 유저 ID, 기본키

    @Column(nullable = false)
    private String password;  // 비밀번호 (암호화된 값)

    @Column(nullable = false)
    private String userName;  // 유저 이름

    @Column(nullable = false, unique = true)
    private String phoneNumber;  // 전화번호 (유일해야 함)

    @Column(nullable = false, unique = true)
    private String email;  // 이메일 (유일해야 함)

    private Instant createdAt;  // 가입일자

    private Instant updatedAt;  // 수정일자

    @Column(nullable = false)
    @Builder.Default
    private boolean loginLock = false;  // 로그인 잠금 여부

    @Column(nullable = false)
    @Builder.Default
    private int loginCnt = 0;       // 로그인 시도 횟수

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;  // 유저 활성화 상태

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
    @Builder.Default
    private Role role = Role.USER;  // 권한

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getValue()));
    }

    @Override // 사용자의 패스워드를 반환
    public String getPassword() {
        return this.password;
    }

    @Override // 사용자의 id를 반환(고유 값)
    public String getUsername() {
        return this.email;
    }

}
