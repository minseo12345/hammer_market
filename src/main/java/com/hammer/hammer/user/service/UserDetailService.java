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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return (UserDetails) userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException(email));
    }
}
