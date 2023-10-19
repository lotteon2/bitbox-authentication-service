package com.bitbox.authentication.service;

import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.exception.NotFoundException;
import com.bitbox.authentication.repository.AuthAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthAdminRepository authAdminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthAdmin findAuthAdmin(String adminEmail, String adminPassword) {
        return authAdminRepository.findByAdminEmailAndAdminPasswordAndDeletedIsFalse(adminEmail, bCryptPasswordEncoder.encode(adminPassword))
                .orElseThrow(NotFoundException::new); // TODO Exception handling
    }

    public boolean isFirstLogin(AuthAdmin authAdmin) {
        return !authAdmin.getCreatedAt().isEqual(authAdmin.getUpdatedAt());
    }
}
