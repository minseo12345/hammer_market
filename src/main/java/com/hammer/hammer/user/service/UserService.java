package com.hammer.hammer.user.service;

import com.hammer.hammer.global.jwt.auth.AuthTokenImpl;
import com.hammer.hammer.global.jwt.auth.JwtProviderImpl;
import com.hammer.hammer.global.jwt.dto.JwtTokenDto;
import com.hammer.hammer.global.jwt.dto.JwtTokenLoginRequest;
import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProviderImpl jwtProvider; // JwtProviderImpl 추가

    public Long save(UserDto userDto) {
        Optional<User> userCheck = userRepository.findByEmail(userDto.getEmail());
        if (userCheck.isPresent()) {
            return 0L;
        }

        // 빌더 패턴을 활용하여 User 객체 생성
        return userRepository.save(User.builder()
                .email(userDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userDto.getPassword())) // 패스워드 저장시 시큐리티 설정에 등록한 빈을 사용해서 암호화 한 후 저장
                .userName(userDto.getUserName())
                .phoneNumber(userDto.getPhoneNumber())
                .role(Role.USER) // 유저 권한 추가 코드
                .build()).getUserId();
    }

    public JwtTokenDto login(JwtTokenLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

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

}
