package com.hammer.hammer.security;
//security 보안 관련만 정리

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .inMemoryAuthentication()
                   .withUser("admin")
                   .password(passwordEncoder().encode("admin123")) // 암호화된 비밀번호
                   .roles("ADMIN")
                   .and()
                   .withUser("user")
                   .password(passwordEncoder().encode("user123")) // 암호화된 비밀번호
                   .roles("USER")
                   .and()
                   .and()
                   .build();
    }
}
