package com.bitbox.authentication.service;

import com.bitbox.authentication.entity.AuthAdmin;
import com.bitbox.authentication.exception.NotFoundException;
import com.bitbox.authentication.repository.AuthAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthAdminRepository authAdminRepository;

    public AuthAdmin findAuthAdmin(String adminEmail, String adminPassword) {
        return authAdminRepository.findByAdminEmailAndAdminPasswordAndDeletedIsFalse(adminEmail, adminPassword).orElseThrow(NotFoundException::new);
    }
}
