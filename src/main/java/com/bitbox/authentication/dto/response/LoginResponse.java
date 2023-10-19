package com.bitbox.authentication.dto.response;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private AuthorityType authority;
}
