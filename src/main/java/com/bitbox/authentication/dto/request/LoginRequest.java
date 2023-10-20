package com.bitbox.authentication.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
