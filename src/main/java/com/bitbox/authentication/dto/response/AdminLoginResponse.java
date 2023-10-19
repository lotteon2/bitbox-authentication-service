package com.bitbox.authentication.dto.response;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdminLoginResponse extends LoginResponse {
    private boolean isFirstLogin;
    public AdminLoginResponse(String accessToken, AuthorityType authority, boolean isFirstLogin) {
        super(accessToken, authority);
        this.isFirstLogin = isFirstLogin;
    }
}
