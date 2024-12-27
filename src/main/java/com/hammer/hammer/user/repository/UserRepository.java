package com.hammer.hammer.user.repository;

import com.hammer.hammer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Optional<User> findByUsernameAndPhoneNumber(String userName, String phoneNumber);
    Optional<User> findByUsernameAndEmail(String userName, String email);
    boolean existsByEmail(String email);

}
