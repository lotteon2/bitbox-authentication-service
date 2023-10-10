package com.bitbox.authentication;

import com.bitbox.authentication.util.JwtProvider;
import com.bitbox.authentication.vo.JwtPayload;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.enums.TokenType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

@SpringBootTest
public class TokenProviderTests {

    Logger logger = Logger.getLogger("TokenProvider Logger");

    @Autowired
    JwtProvider jwtProvider;

    @Test
    @DisplayName("access token 생성 확인")
    void generateAccessTokenTest() {
        JwtPayload jwtPayload = JwtPayload.builder()
                .classId(null)
                .memberId("UUID")
                .memberAuthority(AuthorityType.MANAGER)
                .memberNickname("manager")
                .build();

        String accessToken =
                jwtProvider.generateAccessToken(System.currentTimeMillis(), TokenType.ACCESS, jwtPayload);

        logger.info(accessToken);

        assert !accessToken.isBlank();
    }

    @Test
    @DisplayName("refresh token 생성 확인")
    void generateRefreshTokenTest() {
        JwtPayload jwtPayload = JwtPayload.builder()
                .classId(null)
                .memberId("UUID")
                .memberAuthority(AuthorityType.MANAGER)
                .memberNickname("manager")
                .build();

        String refreshToken =
                jwtProvider.generateRefreshToken(System.currentTimeMillis(), TokenType.REFRESH, jwtPayload);

        logger.info(refreshToken);

        assert !refreshToken.isBlank();
    }
}
