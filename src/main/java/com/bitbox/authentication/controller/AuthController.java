package com.bitbox.authentication.controller;

import com.bitbox.authentication.dto.request.InvitedEmailRequest;
import com.bitbox.authentication.dto.request.LoginRequest;
import com.bitbox.authentication.dto.response.AdminLoginResponse;
import com.bitbox.authentication.dto.response.InvitedEmailResponse;
import com.bitbox.authentication.dto.response.LoginResponse;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.service.AuthService;
import com.bitbox.authentication.service.InvitedEmailService;
import com.bitbox.authentication.service.JwtService;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;
    private final InvitedEmailService invitedEmailService;

    // 교육생 초대 시 REST 요청? kafka?
    @PostMapping("/invitation")
    public ResponseEntity<Void> inviteMember(@Valid @RequestBody InvitedEmailRequest invitedEmailRequest) {
        invitedEmailService.save(invitedEmailRequest);
        return ResponseEntity.ok().build();
    }

    // 초대된 교육생 삭제
    @DeleteMapping("/invitation")
    public ResponseEntity<Void> deleteInviteMember(@Valid @RequestHeader String email) {
        invitedEmailService.delete(email);
        return ResponseEntity.ok().build();
    }

    // 초대된 교육생 전체 목록 조회
    @GetMapping("/invitation")
    public ResponseEntity<List<InvitedEmailResponse>> getinvitedEmails() {
        return ResponseEntity.status(HttpStatus.OK).body(invitedEmailService.findAll());
    }

    // 관리자 로그인
    @PostMapping("/admin")
    public ResponseEntity<AdminLoginResponse> localLogin(@Valid @RequestBody LoginRequest loginRequest) {
        AuthAdmin authAdmin = authService.findAuthAdmin(loginRequest.getEmail(), loginRequest.getPassword());

        JwtPayload jwtPayload = JwtPayload.builder()
                .memberAuthority(authAdmin.getAdminAuthority())
                .memberNickname(authAdmin.getAdminName())
                .memberProfileImg(authAdmin.getAdminProfileImg())
                .memberId(authAdmin.getAdminId())
                .classId(null)
                .build();

        Tokens tokens = jwtService.generateTokens(jwtPayload);
        AdminLoginResponse adminLoginResponse = AdminLoginResponse.builder()
                .accessToken(tokens.getAccessToken())
                .authority(jwtPayload.getMemberAuthority())
                .isFirstLogin(authService.isFirstLogin(authAdmin))
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,
                        jwtService.refreshTokenCookie(tokens.getRefreshToken(), TokenType.REFRESH.getValue()).toString())
                .body(adminLoginResponse);
    }

    // 리프레시 토큰 요청
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue String refreshToken,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        Claims refreshClaims = jwtService.parse(refreshToken);
        Claims accessClaims = jwtService.parse(accessToken);

        if(!jwtService.isValid(accessClaims, refreshClaims)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .header(HttpHeaders.SET_COOKIE, jwtService.refreshTokenCookie("delete", 0).toString())
                    .build();
        }

        JwtPayload refreshPayload = JwtPayload.builder()
                .memberAuthority(jwtService.getMemberAuthority(refreshClaims))
                .memberNickname(jwtService.getMemberNickname(refreshClaims))
                .memberId(jwtService.getMemberId(refreshClaims))
                .classId(jwtService.getClassId(refreshClaims))
                .build();

        Tokens tokens = jwtService.generateTokens(refreshPayload);

        LoginResponse loginResponse = LoginResponse.builder()
                .sessionToken(tokens.getSessionToken())
                .accessToken(tokens.getAccessToken())
                .authority(refreshPayload.getMemberAuthority())
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE,
                        jwtService.refreshTokenCookie(tokens.getRefreshToken(), TokenType.REFRESH.getValue()).toString())
                .body(loginResponse);
    }

    // 로그아웃 요청
    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtService.refreshTokenCookie("delete", 0).toString())
                .build();
    }
}
