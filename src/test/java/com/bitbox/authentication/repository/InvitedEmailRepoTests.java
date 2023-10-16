package com.bitbox.authentication.repository;

import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
public class InvitedEmailRepoTests {

    @Autowired
    InvitedEmailRepository invitedEmailRepository;

    @Test
    @DisplayName("redis에 생성한 값을 이메일로 찾을 수 있어야 한다")
    void RedisInputTest() {
        final String testEmail = "abc@naver.com";
        final Long testClassId = 1L;

        invitedEmailRepository.save(InvitedEmail.builder().email(testEmail).classId(testClassId).build());

        Optional<InvitedEmail> invitedEmail = invitedEmailRepository.findByEmail(testEmail);

        assert invitedEmail.isPresent();
        assert invitedEmail.get().getEmail().equals(testEmail);
        assert invitedEmail.get().getClassId().equals(testClassId);
    }
}
