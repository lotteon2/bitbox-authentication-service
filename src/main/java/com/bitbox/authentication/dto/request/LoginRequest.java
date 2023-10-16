package com.bitbox.authentication.dto.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
public class LoginRequest {
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
