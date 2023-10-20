package com.bitbox.authentication.repository;

import com.bitbox.authentication.dto.response.InvitedEmailResponse;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        final String testClassCode = "TEST11";
        final String testClassName = "TEST_CLASS";
        final Long testClassId = 1L;

        invitedEmailRepository.save(InvitedEmail.builder()
                .email(testEmail)
                .classId(testClassId)
                .className(testClassName)
                .classCode(testClassCode)
                .build());

        Optional<InvitedEmail> invitedEmail = invitedEmailRepository.findByEmail(testEmail);

        assert invitedEmail.isPresent();
        assert invitedEmail.get().getEmail().equals(testEmail);
        assert invitedEmail.get().getClassName().equals(testClassName);
        assert invitedEmail.get().getClassCode().equals(testClassCode);
        assert invitedEmail.get().getClassId().equals(testClassId);
    }

    @Test
    @DisplayName("이메일, 반이름, 반코드 목록을 바로 조회할 수 있다")
    void ListRedisTest() {
        final String testEmail = "abc@naver.com";
        final String testClassCode = "TEST11";
        final String testClassName = "TEST_CLASS";
        final Long testClassId = 1L;

        invitedEmailRepository.save(InvitedEmail.builder()
                .email(testEmail)
                .classCode(testClassCode)
                .className(testClassName)
                .classId(testClassId)
                .build());

        List<InvitedEmail> list = invitedEmailRepository.findAll();

        assert list.size() == 1;
        assert list.get(0).getEmail().equals(testEmail);
        assert list.get(0).getClassCode().equals(testClassCode);
        assert list.get(0).getClassName().equals(testClassName);
    }
}
