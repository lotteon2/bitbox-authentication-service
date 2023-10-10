package com.bitbox.authentication.service;

import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.KakaoTokenRequest;
import com.bitbox.authentication.dto.KakaoTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OAuthKakaoService {
    private final Environment env;
    private final ObjectMapper objectMapper;
    private final Base64.Decoder decoder;

    public HttpHeaders redirect(String redirectURI) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(redirectURI));
        return httpHeaders;
    }

    public String kakaoRedirectURI() {
        return new StringBuilder()
                .append("https://kauth.kakao.com/oauth/authorize?")
                .append("client_id=").append(env.getProperty("oauth.kakao.client-id"))
                .append("&")
                .append("redirect_uri=").append(env.getProperty("oauth.kakao.redirect-uri"))
                .append("&")
                .append("response_type=").append("code")
                .append("&")
                .append("scope=").append(env.getProperty("oauth.kakao.scope"))
                .toString();
    }

    public KakaoTokenRequest createKakaoTokenRequest(String code) {
        return KakaoTokenRequest.builder()
                .code(code)
                .clientId(env.getProperty("oauth.kakao.client-id"))
                .clientSecret(env.getProperty("oauth.kakao.client-secret"))
                .redirectUri(env.getProperty("oauth.kakao.redirect-uri"))
                .build();
    }

    public KakaoIdTokenPayload decodeKakaoIdToken(KakaoTokenResponse kakaoTokenResponse) throws JsonProcessingException {
        return objectMapper.readValue(new String(decoder.decode(kakaoTokenResponse.getIdToken().split("\\.")[1])),
                KakaoIdTokenPayload.class);
    }
}
