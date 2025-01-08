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
public class UserDto {

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{8,20}$", message = "Password must include at least one letter, one number, and one special character")
    private String password;

    @NotNull(message = "Username cannot be null")
    @Pattern(regexp = "^[a-zA-Z가-힣]{2,30}$", message = "Username must be a valid name containing Korean or English letters")
    @Size(min = 2, max = 5, message = "Username must be between 2 and 30 characters")
    private String username;

    @NotNull(message = "Phone number cannot be null")
    @Pattern(regexp = "^010\\d{7,8}$", message = "Phone number must be in the format of 010-xxxx-xxxx, 010-xxx-xxxx")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be a valid phone number")
    private String phoneNumber;
    private Role role;
}
