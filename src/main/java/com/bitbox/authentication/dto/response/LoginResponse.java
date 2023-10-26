package com.bitbox.authentication.dto.response;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String sessionToken;
    private AuthorityType authority;
    private Long classId;
    private boolean isInvited;
}
