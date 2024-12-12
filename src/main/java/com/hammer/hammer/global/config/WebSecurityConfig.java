package com.hammer.hammer.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth //인증, 인가 설정
//                        .requestMatchers(
//                                "/login", "/signup"
//                        ).permitAll()
//                        .anyRequest().authenticated() // 그 외 모든 요청은 인증된 사용자만 접근 가능
//                )
//                .formLogin(form -> form // 폼 기반 로그인 설정
//                        .loginPage("/login") // 커스텀 로그인 페이지
//                        .defaultSuccessUrl("/") // 로그인 성공시 URL
//                        .permitAll() // 모든 사용자 - 로그인 페이지 접근 허용
//                )
//                .logout(logout -> logout // 로그아웃 설정
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                        .permitAll()
//                )
//                .csrf(csrf -> csrf.disable()) // 6. CSRF 비활성화
//                .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("ADMIN", "USER")
                )
                .formLogin(login -> login
                        .defaultSuccessUrl("/admin",true) //로그인 성공 후 "/admin"이동
                        .permitAll())

                .logout(logout -> logout.permitAll());
        return http.build();
    }



    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
