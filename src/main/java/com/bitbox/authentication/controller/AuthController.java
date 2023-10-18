package com.bitbox.authentication.controller;

import com.bitbox.authentication.dto.request.LoginRequest;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.service.AuthService;
import com.bitbox.authentication.service.JwtService;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/admin")
    public ResponseEntity<Map<String, String>> localLogin(@Valid @RequestBody LoginRequest loginRequest) {
        AuthAdmin authAdmin = authService.findAuthAdmin(loginRequest.getEmail(), loginRequest.getPassword());

        JwtPayload jwtPayload = JwtPayload.builder()
                .memberAuthority(authAdmin.getAdminAuthority())
                .memberNickname(authAdmin.getAdminName())
                .memberId(authAdmin.getAdminId())
                .classId(null)
                .build();

        // TODO : REFACTORING
        Tokens tokens = jwtService.generateTokens(jwtPayload);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("authority", jwtPayload.getMemberAuthority().name());
        resultMap.put("accessToken", tokens.getAccessToken());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie",
                        jwtService.refreshTokenCookie(tokens.getRefreshToken()).toString())
                .body(resultMap);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Refreshtoken") String refreshToken) {
        Claims claims = jwtService.parse(refreshToken);

        JwtPayload jwtPayload = JwtPayload.builder()
                .memberAuthority(jwtService.getMemberAuthority(claims))
                .memberNickname(jwtService.getMemberNickname(claims))
                .memberId(jwtService.getMemberId(claims))
                .classId(jwtService.getClassId(claims))
                .build();

        // TODO : REFACTORING
        Tokens tokens = jwtService.generateTokens(jwtPayload);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("authority", jwtPayload.getMemberAuthority().name());
        resultMap.put("accessToken", tokens.getAccessToken());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie",
                        jwtService.refreshTokenCookie(tokens.getRefreshToken()).toString())
                .body(resultMap);
    }
}
