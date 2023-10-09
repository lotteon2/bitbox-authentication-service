package com.bitbox.authentication.service;

import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitedEmailService {
    private final InvitedEmailRepository invitedEmailRepository;

    public Optional<InvitedEmail> findInvitedEmailByEmail(String email) {
        return invitedEmailRepository.findInvitedEmailByEmail(email);
    }
}
