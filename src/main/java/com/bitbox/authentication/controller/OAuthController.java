package com.bitbox.authentication.controller;

import com.bitbox.authentication.client.OAuthKakaoFeignClient;
import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.request.KakaoTokenRequest;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import com.bitbox.authentication.dto.response.TokenResponse;
import com.bitbox.authentication.service.JwtService;
import com.bitbox.authentication.service.OAuthKakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthKakaoFeignClient oAuthKakaoFeignClient;
    private final OAuthKakaoService oAuthKakaoService;
    private final JwtService jwtService;

    @GetMapping("auth/kakao")
    public ResponseEntity<?> redirectToKakao() {
        return new ResponseEntity<>(
                oAuthKakaoService.redirect(oAuthKakaoService.kakaoRedirectURI()),
                HttpStatus.MOVED_PERMANENTLY
        );
    }

    @GetMapping("oauth/token")
    public ResponseEntity<?> getTokenFromKakaoAndAuth(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        // kakao에서 인가 코드 받아오는데 문제가 생겼다면 early return
        if(error != null && errorDescription != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDescription);
        }

        try {
            KakaoTokenRequest kakaoTokenRequest = oAuthKakaoService.createKakaoTokenRequest(code);

            // kakao와의 통신에서 문제가 발생할 경우 error handling
            KakaoTokenResponse kakaoTokenResponse =
                    oAuthKakaoFeignClient.getTokenFromKakao(kakaoTokenRequest.toString());

            // decoding 과정에서 문제가 발생할 경우 error handling
            KakaoIdTokenPayload kakaoIdTokenPayload = oAuthKakaoService.decodeKakaoIdToken(kakaoTokenResponse);

            JwtPayload jwtPayload = oAuthKakaoService.convertToJwtPayload(kakaoIdTokenPayload);

            TokenResponse tokenResponse = jwtService.generateTokens(jwtPayload);

            return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // TODO : handle exception
        } catch (FeignException e) {
            e.printStackTrace(); // TODO : handle exception
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
