package com.bitbox.authentication.controller;

import com.bitbox.authentication.dto.request.LoginRequest;
import com.bitbox.authentication.dto.response.AdminLoginResponse;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.service.AuthService;
import com.bitbox.authentication.service.JwtService;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/admin")
    public ResponseEntity<AdminLoginResponse> localLogin(@Valid @RequestBody LoginRequest loginRequest) {
        AuthAdmin authAdmin = authService.findAuthAdmin(loginRequest.getEmail(), loginRequest.getPassword());

        JwtPayload jwtPayload = JwtPayload.builder()
                .memberAuthority(authAdmin.getAdminAuthority())
                .memberNickname(authAdmin.getAdminName())
                .memberId(authAdmin.getAdminId())
                .classId(null)
                .build();

        Tokens tokens = jwtService.generateTokens(jwtPayload);

        AdminLoginResponse adminLoginResponse = new AdminLoginResponse(
                tokens.getAccessToken(),
                jwtPayload.getMemberAuthority(),
                authService.isFirstLogin(authAdmin)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtService.refreshTokenCookie(tokens.getRefreshToken()).toString())
                .body(adminLoginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@CookieValue String refreshToken) {
        Claims claims = jwtService.parse(refreshToken);

        JwtPayload jwtPayload = JwtPayload.builder()
                .memberAuthority(jwtService.getMemberAuthority(claims))
                .memberNickname(jwtService.getMemberNickname(claims))
                .memberId(jwtService.getMemberId(claims))
                .classId(jwtService.getClassId(claims))
                .build();

        Tokens tokens = jwtService.generateTokens(jwtPayload);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtService.refreshTokenCookie(tokens.getRefreshToken()).toString())
                .body(tokens.getAccessToken());
    }
}
