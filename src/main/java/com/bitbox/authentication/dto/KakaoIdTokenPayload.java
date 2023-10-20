package com.bitbox.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class KakaoIdTokenPayload {
    private String iss;
    private String aud;
    private String sub;
    private Integer iat;
    private Integer exp;

    @JsonProperty("auth_time")
    private Integer authTime;
    private String nonce;
    private String nickname;
    private String email;

    @Nullable
    private String picture;
}
