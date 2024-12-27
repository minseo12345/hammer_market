package com.hammer.hammer.user.dto;

import com.hammer.hammer.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class UserFindDto {


    @NotNull(message = "Username cannot be null")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z가-힣]{2,30}$", message = "Username must be a valid name containing Korean or English letters")
    private String username;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "ctfNo cannot be null")
    private String ctfNo;
}
