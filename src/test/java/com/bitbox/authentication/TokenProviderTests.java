package com.bitbox.authentication;

import com.bitbox.authentication.util.JwtProvider;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.logging.Logger;

@SpringBootTest
@ActiveProfiles("dev")
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
                jwtProvider.generateToken(System.currentTimeMillis(), TokenType.ACCESS, jwtPayload);

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
                jwtProvider.generateToken(System.currentTimeMillis(), TokenType.REFRESH, jwtPayload);

        logger.info(refreshToken);

        assert !refreshToken.isBlank();
    }

    @Test
    @DisplayName("refresh token claims 확인")
    void parseRefreshTokenTest() {
        JwtPayload jwtPayload = JwtPayload.builder()
                .classId(null)
                .memberId("UUID")
                .memberAuthority(AuthorityType.MANAGER)
                .memberNickname("manager")
                .build();

        String refreshToken = jwtProvider.generateToken(System.currentTimeMillis(), TokenType.REFRESH, jwtPayload);

        Claims claims = jwtProvider.parse(refreshToken);

        assert claims.get("member_id", String.class).equals("UUID");
        assert AuthorityType.valueOf((String) claims.get("member_authority")).equals(AuthorityType.MANAGER);
        assert claims.get("class_id", Long.class) == null;
    }
}
