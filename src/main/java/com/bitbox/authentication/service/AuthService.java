package com.bitbox.authentication.service;

import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.exception.CustomNotFoundException;
import com.bitbox.authentication.repository.AuthAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthAdminRepository authAdminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthAdmin findAuthAdmin(String adminEmail, String adminPassword) {
        Optional<AuthAdmin> authAdmin = authAdminRepository.findByAdminEmailAndDeletedIsFalse(adminEmail);

        if(authAdmin.isEmpty() || !bCryptPasswordEncoder.matches(adminPassword, authAdmin.get().getAdminPassword()))
            throw new CustomNotFoundException("존재하지 않는 계정입니다");

        return authAdmin.get();
    }

    public boolean isFirstLogin(AuthAdmin authAdmin) {
        return authAdmin.getCreatedAt().isEqual(authAdmin.getUpdatedAt());
    }
}
