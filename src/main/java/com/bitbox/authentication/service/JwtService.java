package com.bitbox.authentication.service;

import com.bitbox.authentication.dto.TokenResponse;
import com.bitbox.authentication.util.JwtProvider;
import com.bitbox.authentication.vo.JwtPayload;
import io.github.bitbox.bitbox.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProvider jwtProvider;
    public TokenResponse generateTokens(JwtPayload jwtPayload) {
        long regDate = System.currentTimeMillis();

        return TokenResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(regDate, TokenType.ACCESS, jwtPayload))
                .refreshToken(jwtProvider.generateRefreshToken(regDate, TokenType.REFRESH, jwtPayload))
                .build();
    }
}
