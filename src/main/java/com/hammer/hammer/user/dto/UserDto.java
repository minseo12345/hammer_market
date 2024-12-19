package com.hammer.hammer.user.dto;

import com.hammer.hammer.user.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class UserDto {
    private String email;
    private String password;
    private String username;
    private String phoneNumber;
    private Role role;
}
