package com.hammer.hammer.user.service;

import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

}
