package com.bitbox.authentication.controller;

import com.bitbox.authentication.client.OAuthKakaoFeignClient;
import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.request.KakaoTokenRequest;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.service.JwtService;
import com.bitbox.authentication.service.OAuthKakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/oauth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthKakaoFeignClient oAuthKakaoFeignClient;
    private final OAuthKakaoService oAuthKakaoService;
    private final JwtService jwtService;

    @GetMapping("/kakao/token")
    public ResponseEntity<Map<String, String>> getTokenFromKakaoAndAuth(@RequestParam String code) {
        try {
            KakaoTokenRequest kakaoTokenRequest = oAuthKakaoService.createKakaoTokenRequest(code);

            // kakao와의 통신에서 문제가 발생할 경우 error handling
            KakaoTokenResponse kakaoTokenResponse =
                    oAuthKakaoFeignClient.getTokenFromKakao(kakaoTokenRequest.toString());

            // decoding 과정에서 문제가 발생할 경우 error handling
            KakaoIdTokenPayload kakaoIdTokenPayload = oAuthKakaoService.decodeKakaoIdToken(kakaoTokenResponse);

            JwtPayload jwtPayload = oAuthKakaoService.convertToJwtPayload(kakaoIdTokenPayload);

            Tokens tokens = jwtService.generateTokens(jwtPayload);

            // TODO : REFACTORING
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("authority", jwtPayload.getMemberAuthority().name());
            resultMap.put("accessToken", tokens.getAccessToken());

            ResponseCookie refreshTokenCookie = jwtService.refreshTokenCookie(tokens.getRefreshToken());

            return ResponseEntity.status(HttpStatus.OK)
                    .header("refreshToken", refreshTokenCookie.toString())
                    .body(resultMap);

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // TODO : handle exception
        } catch (FeignException e) {
            e.printStackTrace(); // TODO : handle exception
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
