package com.bitbox.authentication.util;

import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private final long accessExpire;

    private final long refreshExpire;

    private final JwtBuilder jwtBuilder;

    private final Key key;

    public JwtProvider(Environment env) {
        this.accessExpire = Long.parseLong(env.getProperty("jwt.access-expire"));
        this.refreshExpire = Long.parseLong(env.getProperty("jwt.refresh-expire"));
        this.key = new SecretKeySpec(
                DatatypeConverter.parseBase64Binary(env.getProperty("jwt.secret")),
                SignatureAlgorithm.HS256.getJcaName());
        this.jwtBuilder = Jwts.builder();
    }

    public String generateAccessToken(final long regDate, final TokenType tokenType, final JwtPayload jwtPayload) {
        return jwtBuilder
                .setHeader(createHeader(tokenType))
                .setSubject(TokenType.ACCESS.getValue())
                .setClaims(createClaims(jwtPayload))
                .setExpiration(createExpireDate(regDate, this.accessExpire))
                .signWith(SignatureAlgorithm.HS256, this.key)
                .compact();
    }

    public String generateRefreshToken(final long regDate, final TokenType tokenType, final JwtPayload jwtPayload) {
        return jwtBuilder
                .setHeader(createHeader(tokenType))
                .setSubject(TokenType.REFRESH.getValue())
                .setClaims(createClaims(jwtPayload))
                .setExpiration(createExpireDate(regDate, this.refreshExpire))
                .signWith(SignatureAlgorithm.HS256, this.key)
                .compact();
    }

    private static Map<String, Object> createHeader(TokenType tokenType) {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", tokenType);
        header.put("alg", "HS256");

        return header;
    }

    private static Map<String, Object> createClaims(final JwtPayload jwtPayload) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("member_id", jwtPayload.getMemberId());
        claims.put("class_id", jwtPayload.getClassId());
        claims.put("member_authority", jwtPayload.getMemberAuthority());
        claims.put("member_nickname", jwtPayload.getMemberNickname());

        return claims;
    }

    private static Date createExpireDate(final long regDate, final long expire) {
        return new Date(regDate + expire);
    }
}
