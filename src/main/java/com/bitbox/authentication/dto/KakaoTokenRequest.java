package com.bitbox.authentication.dto;

import lombok.*;

@Setter
@Getter
@Builder
public class KakaoTokenRequest {

    private final String GRANT_TYPE = "authorization_code";

    private String clientId;

    private String clientSecret;

    private String redirectUri;

    private String code;

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
