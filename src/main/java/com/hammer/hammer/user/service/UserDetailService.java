package com.hammer.hammer.user.service;

import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override // UserDetailsService 인터페이스를 구현
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // loadUserByUsername() 메서드를 오버라이딩하여 사용자 정보를 가져오는 로직 작성
        //return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException(email));
    }
}
