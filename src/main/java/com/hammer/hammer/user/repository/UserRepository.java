package com.hammer.hammer.user.repository;

import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(Long userId);
}
