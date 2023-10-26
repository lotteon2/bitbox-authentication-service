package com.bitbox.authentication.service;

import com.bitbox.authentication.client.MemberFeignClient;
import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.request.KakaoTokenRequest;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import com.bitbox.authentication.entity.AuthMember;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.exception.InternalException;
import com.bitbox.authentication.repository.AuthMemberRepository;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.bitbox.bitbox.dto.MemberRegisterDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64.Decoder;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthKakaoService {
    private final Environment env;
    private final ObjectMapper objectMapper;
    private final Decoder decoder;

    public KakaoTokenRequest createKakaoTokenRequest(String code) {
        return KakaoTokenRequest.builder()
                .code(code)
                .clientId(env.getProperty("oauth.kakao.client-id"))
                .clientSecret(env.getProperty("oauth.kakao.client-secret"))
                .redirectUri(env.getProperty("oauth.kakao.redirect-uri"))
                .build();
    }

    public KakaoIdTokenPayload decodeKakaoIdToken(KakaoTokenResponse kakaoTokenResponse) {
        try {
            return objectMapper.readValue(new String(decoder.decode(kakaoTokenResponse.getIdToken().split("\\.")[1])),
                    KakaoIdTokenPayload.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e);
        }
    }
}
