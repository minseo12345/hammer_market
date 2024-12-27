package com.hammer.hammer.user;

import com.hammer.hammer.global.jwt.auth.JwtProviderImpl;
import com.hammer.hammer.global.jwt.dto.JwtTokenLoginRequest;
import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.RoleRepository;
import com.hammer.hammer.user.repository.UserRepository;
import com.hammer.hammer.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtProviderImpl jwtProvider;

    @InjectMocks
    private UserService userService;

    private Role testRole;

    private User testUser;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .roleId(2L) // Role에 id 추가
                .roleName("ROLE_USER")
                .build();

        testUser = User.builder()
                .userId(1L) // userId 추가
                .email("test@test.com")
                .password(bCryptPasswordEncoder.encode("123"))
                .username("test")
                .phoneNumber("01012341234")
                .role(testRole)
                .build();
    }

    @Test
    void save_UserAlreadyExists_ReturnsZero() {
        // Given
        UserDto userDto = UserDto.builder()
                .email("test@test.com")
                .build();
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Long result = userService.save(userDto);

        // Then
        assertEquals(0L, result);
    }

    @Test
    void save_Success_ReturnsUserId() {
        // Given
        UserDto newUser = UserDto.builder()
                .email("new@test.com")
                .password("123") // 패스워드는 원래 값으로 주어야 bcrypt를 모킹할 때 헷갈리지 않음
                .username("test")
                .phoneNumber("01012341234")
                .build();

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(2L)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(bCryptPasswordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");

        // When
        Long result = userService.save(newUser);

        // Then
        assertEquals(testUser.getUserId(), result); // 2L로 수정하는 것 대신, testUser.getUserId()로 수정
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Given
        JwtTokenLoginRequest request = new JwtTokenLoginRequest("asd", "asd");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // Given
        JwtTokenLoginRequest request = new JwtTokenLoginRequest("test@example.com", "123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(bCryptPasswordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.login(request));
    }

}