package com.bitbox.authentication.service;

import com.bitbox.authentication.dto.request.InvitedEmailRequest;
import com.bitbox.authentication.dto.response.InvitedEmailResponse;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.exception.CustomNotFoundException;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitedEmailService {
    private final InvitedEmailRepository invitedEmailRepository;
    private final ObjectMapper objectMapper;

    public void save(InvitedEmailRequest invitedEmailRequest) {
        invitedEmailRepository.save(objectMapper.convertValue(invitedEmailRequest, InvitedEmail.class));
    }

    public void delete(String email) {
        InvitedEmail invitedEmail = invitedEmailRepository.findByEmail(email).orElseThrow(() -> new CustomNotFoundException("초대된 교육생이 존재하지 않습니다"));
        invitedEmailRepository.delete(invitedEmail);
    }

    public Optional<InvitedEmail> findInvitedEmail(String email) {
        return invitedEmailRepository.findByEmail(email);
    }

    public List<InvitedEmailResponse> findAll() {
        List<InvitedEmail> invitedEmails = invitedEmailRepository.findAll();

        List<InvitedEmailResponse> result = new ArrayList<>();
        for(InvitedEmail invitedEmail: invitedEmails) {
            result.add(objectMapper.convertValue(invitedEmail, InvitedEmailResponse.class));
        }
        return result;
    }
}
