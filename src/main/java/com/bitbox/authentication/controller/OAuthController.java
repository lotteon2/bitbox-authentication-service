package com.bitbox.authentication.controller;

import com.bitbox.authentication.client.OAuthKakaoFeignClient;
import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.request.KakaoTokenRequest;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import com.bitbox.authentication.dto.response.LoginResponse;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.service.JwtService;
import com.bitbox.authentication.service.OAuthKakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthKakaoFeignClient oAuthKakaoFeignClient;
    private final OAuthKakaoService oAuthKakaoService;
    private final JwtService jwtService;

    // 소셜 로그인
    @GetMapping("/kakao/token")
    public ResponseEntity<LoginResponse> getTokenFromKakaoAndAuth(@RequestParam String code) {
            KakaoTokenRequest kakaoTokenRequest = oAuthKakaoService.createKakaoTokenRequest(code);

            KakaoTokenResponse kakaoTokenResponse =
                    oAuthKakaoFeignClient.getTokenFromKakao(kakaoTokenRequest.toString());

            KakaoIdTokenPayload kakaoIdTokenPayload = oAuthKakaoService.decodeKakaoIdToken(kakaoTokenResponse);

            JwtPayload jwtPayload = oAuthKakaoService.convertToJwtPayload(kakaoIdTokenPayload);

            // TODO : REFACTORING
            Tokens tokens = jwtService.generateTokens(jwtPayload);

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(tokens.getAccessToken())
                    .authority(jwtPayload.getMemberAuthority())
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtService.refreshTokenCookie(tokens.getRefreshToken(), TokenType.REFRESH.getValue()).toString())
                    .body(loginResponse);
    }
}
