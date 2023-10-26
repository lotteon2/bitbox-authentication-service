package com.bitbox.authentication.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tokens {
    private String accessToken;
    private String sessionToken;
    private String refreshToken;
}
