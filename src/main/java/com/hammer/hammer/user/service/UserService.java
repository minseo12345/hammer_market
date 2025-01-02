package com.hammer.hammer.user.service;

import com.hammer.hammer.global.jwt.auth.AuthTokenImpl;
import com.hammer.hammer.global.jwt.auth.JwtProviderImpl;
import com.hammer.hammer.global.jwt.dto.JwtTokenDto;
import com.hammer.hammer.global.jwt.dto.JwtTokenLoginRequest;
import com.hammer.hammer.profile.dto.ProfileUpdateRequestDto;
import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.repository.RoleRepository;
import com.hammer.hammer.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProviderImpl jwtProvider; // JwtProviderImpl 추가

    public Long save(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 Email 입니다.");
        }

        Role userRole = roleRepository.findById(2L).
                orElseThrow(() -> new IllegalArgumentException("Role not found for 2"));;

        // 빌더 패턴을 활용하여 User 객체 생성
        return userRepository.save(User.builder()
                .email(userDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword())) // 패스워드 저장시 시큐리티 설정에 등록한 빈을 사용해서 암호화 한 후 저장
                .username(userDto.getUsername())
                .phoneNumber(userDto.getPhoneNumber())
                .role(userRole) // 유저 권한 추가 코드
                .build()).getUserId();
    }

    public JwtTokenDto login(JwtTokenLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userEmail", user.getEmail());
        claims.put("username", user.getName());

        AuthTokenImpl accessToken = jwtProvider.createAccessToken(
                user.getUserId().toString(),
                user.getRole(),
                claims
        );

        AuthTokenImpl refreshToken = jwtProvider.createRefreshToken(
                user.getUserId().toString(),
                user.getRole(),
                claims
        );

        return JwtTokenDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
    }

    public Optional<String> findByUsernameAndPhoneNumber(UserDto userDto) {
        return userRepository
                .findByUsernameAndPhoneNumber(userDto.getUsername(), userDto.getPhoneNumber())
                .map(User::getEmail);
    }

    public Optional<User> findByNameAndEmail(String userName, String email) {
        return userRepository.findByUsernameAndEmail(userName, email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequestDto request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setUsername(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());

        // 비밀번호 변경이 요청된 경우에만 처리
        if (StringUtils.hasText(request.getPassword())) {
            if (!request.getPassword().equals(request.getPasswordConfirm())) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }
            user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 연관된 데이터 처리 (필요한 경우)
        // ex) user.getBids().clear();

        userRepository.delete(user);
    }
//    public void updatePassword(User user, String tempPassword) {
//        user.setPassword(encodePassword(tempPassword)); // 비밀번호 암호화
//        userRepository.save(user);
//    }
}
