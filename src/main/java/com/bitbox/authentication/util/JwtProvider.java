package com.bitbox.authentication.util;

import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;

    private final Key key;

    public JwtProvider(Environment env) {
        this.key = new SecretKeySpec(
                DatatypeConverter.parseBase64Binary(env.getProperty("jwt.secret")),
                SignatureAlgorithm.HS256.getJcaName());
        this.jwtBuilder = Jwts.builder();
        this.jwtParser = Jwts.parser();
    }

    public String generateToken(final long regDate, final TokenType tokenType, final JwtPayload jwtPayload) {
        return jwtBuilder
                .setHeader(createHeader(tokenType))
                .setSubject(tokenType.name())
                .setClaims(createClaims(jwtPayload))
                .setExpiration(createExpireDate(regDate, tokenType))
                .signWith(SignatureAlgorithm.HS256, this.key)
                .compact();
    }

    public Claims parse(String jwt) {
        return jwtParser.setSigningKey(key).parseClaimsJws(jwt).getBody();
    }

    private static Map<String, Object> createHeader(TokenType tokenType) {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", tokenType.name());
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

    private static Date createExpireDate(final long regDate, final TokenType tokenType) {
        return new Date(regDate + tokenType.getValue());
    }
}
