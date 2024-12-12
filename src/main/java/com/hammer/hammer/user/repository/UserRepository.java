package com.hammer.hammer.user.repository;

import com.hammer.hammer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
