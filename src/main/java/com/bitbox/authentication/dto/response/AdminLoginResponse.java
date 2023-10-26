package com.bitbox.authentication.dto.response;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    private AuthorityType authority;
    private String accessToken;
    private boolean isFirstLogin;
}
