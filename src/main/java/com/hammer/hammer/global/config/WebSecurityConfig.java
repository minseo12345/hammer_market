package com.hammer.hammer.global.config;

import com.hammer.hammer.global.exception.AccessDeniedHandlerImpl;
import com.hammer.hammer.global.exception.AuthenticationEntryPointImpl;
import com.hammer.hammer.global.jwt.filter.JwtFilter;
import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.RoleRepository;
import com.hammer.hammer.user.repository.UserRepository;
import com.hammer.hammer.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
//import com.hammer.hammer.global.jwt.filter.JwtAuthenticationFilter;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl;
    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter; // JwtFilter 생성자 주입 코드 추가

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                 "/login", "/login/**", "/logout",
                                "/signup", "/user",
                                "/jwt-login",
                                "/chat","/api/**","/chat/**","/topic/messages","/ws/**","/app/chat","/ws",
                                "/error",
                                "/css/**", "/js/**", "/img/**"
                        ).permitAll()  // 로그인 API는 인증 없이 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout // 5. 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .deleteCookies(
                                "JSESSIONID", "refreshToken", "remember-me")
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(authenticationEntryPointImpl) // 인증 실패 처리
                        .accessDeniedHandler(accessDeniedHandlerImpl) // 권한 부족 처리
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // 8. JWT 필터 추가
                .rememberMe(rememberMe -> rememberMe
                        .key("hammer")
                        .userDetailsService(userDetailsService)
                        .tokenValiditySeconds(604800)
                )
                .build();
    }

    // 7. 인증 관리자 관련 설정 (스프링 시큐리티의 인증관리자 설정)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder
                = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // 테스트용 Role 생성
            Role adminRole = Role.builder()
                    .roleName("ROLE_ADMIN")
                    .build();

            Role userRole = Role.builder()
                    .roleName("ROLE_USER")
                    .build();

            // 테스트용 관리자 계정 admin
            User adminUser = User.builder()
                    .email("admin@test.com")
                    .password(bCryptPasswordEncoder().encode("123"))
                    .username("admin")
                    .phoneNumber("01012341234")
                    .role(adminRole)
                    .build();

            // 테스트용 사용자 계정 buyer
            User buyerUser = User.builder()
                    .email("buyer@test.com")
                    .password(bCryptPasswordEncoder().encode("123"))
                    .username("buyer")
                    .phoneNumber("01012341334")
                    .role(userRole)
                    .build();

            // 테스트용 사용자 계정 seller
            User sellerUser = User.builder()
                    .email("seller@test.com")
                    .password(bCryptPasswordEncoder().encode("123"))
                    .username("seller")
                    .phoneNumber("0101212341233")
                    .role(userRole)
                    .build();


            // 테스트용 사용자 계정 user
            User normalUser = User.builder()
                    .email("user@test.com")
                    .password(bCryptPasswordEncoder().encode("123"))
                    .username("user")
                    .phoneNumber("01012341233")
                    .role(userRole)
                    .build();


//            roleRepository.save(adminRole);
//            roleRepository.save(userRole);
//
//            // 테스트 user 생성
//            userRepository.save(adminUser);
//            userRepository.save(buyerUser);
//            userRepository.save(sellerUser);
//            userRepository.save(normalUser);
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();

    }
}