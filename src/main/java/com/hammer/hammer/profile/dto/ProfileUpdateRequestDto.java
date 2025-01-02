package com.hammer.hammer.profile.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileUpdateRequestDto {
    private String name;
    private String phoneNumber;
    private String password;
    private String passwordConfirm;
}
