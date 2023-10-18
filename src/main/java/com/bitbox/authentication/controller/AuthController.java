package com.bitbox.authentication.controller;

import com.bitbox.authentication.dto.request.LoginRequest;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.service.AuthService;
import com.bitbox.authentication.service.JwtService;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/admin")
    public ResponseEntity<Tokens> localLogin(@Valid @RequestBody LoginRequest loginRequest) {
        AuthAdmin authAdmin = authService.findAuthAdmin(loginRequest.getEmail(), loginRequest.getPassword());

        Tokens tokens = jwtService.generateTokens(JwtPayload.builder()
                .memberAuthority(authAdmin.getAdminAuthority())
                .memberNickname(authAdmin.getAdminName())
                .memberId(authAdmin.getAdminId())
                .classId(null)
                .build());


        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }
}
