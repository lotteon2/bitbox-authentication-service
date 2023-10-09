package com.bitbox.authentication.service;

import com.bitbox.authentication.repository.AuthAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthAdminRepository authAdminRepository;
}
