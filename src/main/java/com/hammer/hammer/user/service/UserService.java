package com.hammer.hammer.user.service;

import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository
                     , BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Long save(User user) {
        // 빌더 패턴을 활용하여 User 객체 생성
        return userRepository.save(User.builder()
                .email(user.getEmail())
                .password(bCryptPasswordEncoder.encode(user.getPassword())) // 패스워드 저장시 시큐리티 설정에 등록한 빈을 사용해서 암호화 한 후 저장
                .userName(user.getUserName())
                .phoneNumber(user.getPhoneNumber())
                .role(Role.USER) // 유저 권한 추가 코드
                .build()).getUserId();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
