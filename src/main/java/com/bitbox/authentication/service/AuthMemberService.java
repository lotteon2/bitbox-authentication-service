package com.bitbox.authentication.service;

import com.bitbox.authentication.entity.AuthMember;
import com.bitbox.authentication.repository.AuthMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthMemberService {

    private final AuthMemberRepository authMemberRepository;

    public Optional<AuthMember> findAuthMember(String email) {
        return authMemberRepository.findByMemberEmailAndDeletedIsFalse(email);
    }
}
