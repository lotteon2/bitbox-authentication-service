package com.bitbox.authentication.service;

import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.util.JwtProvider;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
                .domain("localhost") // TODO : env 등으로 변경?
                .httpOnly(true)
                .sameSite("None")
//                    .secure(true) // TODO : secure(true)시 https에서만 작동.
                .build();
    }

    public Claims parse(String refreshToken) {
        return jwtProvider.parse(refreshToken);
    }

    public String getMemberId(Claims claims) {
        return claims.get("member_id", String.class);
    }

    public String getMemberNickname(Claims claims) {
        return URLEncoder.encode(claims.get("member_nickname", String.class), StandardCharsets.UTF_8);
    }

    public Long getClassId(Claims claims) {
        return claims.get("class_id", Long.class);
    }

    public AuthorityType getMemberAuthority(Claims claims) {
        return AuthorityType.valueOf((String) claims.get("member_authority"));
    }
}
