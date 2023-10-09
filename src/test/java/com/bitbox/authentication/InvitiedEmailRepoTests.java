package com.bitbox.authentication;

import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class InvitiedEmailRepoTests {

    @Autowired
    InvitedEmailRepository invitedEmailRepository;

    @Test
    @DisplayName("redis에 생성한 값을 이메일로 찾을 수 있어야 한다")
    void RedisInputTest() {
        final String testEmail = "abc@naver.com";

        invitedEmailRepository.save(InvitedEmail.builder().email(testEmail).build());

        Optional<InvitedEmail> invitedEmail = invitedEmailRepository.findInvitedEmailByEmail(testEmail);

        assert invitedEmail.isPresent();
        assert invitedEmail.get().getEmail().equals(testEmail);
    }
}
