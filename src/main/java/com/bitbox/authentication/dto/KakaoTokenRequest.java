package com.bitbox.authentication.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class KakaoTokenRequest {
    private final String GRANT_TYPE = "authorization_code";

    @Value("${oauth.kakao.client-id}")
    private final String clientId;

    @Value("${oauth.kakao.client-secret}")
    private final String clientSecret;

    private String code;

    @Value("${oauth.kakao.redirect-uri}")
    private final String redirectUri;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("grant_type=").append(GRANT_TYPE)
                .append("&")
                .append("client_id=").append(clientId)
                .append("&")
                .append("client_secret=").append(clientSecret)
                .append("&")
                .append("code=").append(code)
                .append("&")
                .append("redirect_uri=").append(redirectUri)
                .toString();
    }
}
