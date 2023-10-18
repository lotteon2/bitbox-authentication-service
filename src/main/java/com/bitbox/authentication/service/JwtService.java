package com.bitbox.authentication.service;

import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.util.JwtProvider;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProvider jwtProvider;
    public Tokens generateTokens(JwtPayload jwtPayload) {
        long regDate = System.currentTimeMillis();

        return Tokens.builder()
                .accessToken(jwtProvider.generateToken(regDate, TokenType.ACCESS, jwtPayload))
                .refreshToken(jwtProvider.generateToken(regDate, TokenType.REFRESH, jwtPayload))
                .build();
    }

    public ResponseCookie refreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(TokenType.REFRESH.getValue())
                .path("/")
                .httpOnly(true)
                .sameSite("None")
//                    .secure(true) // TODO : secure(true)시 https에서만 작동.
                .build();
    }
}
