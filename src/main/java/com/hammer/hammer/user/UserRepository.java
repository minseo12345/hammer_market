package com.hammer.hammer.user;



import org.springframework.data.jpa.repository.JpaRepository;
import com.hammer.hammer.domain.User;

public interface UserRepository extends JpaRepository<User, String> {


	
}
