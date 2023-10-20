package com.bitbox.authentication.service;

import com.bitbox.authentication.dto.response.InvitedEmailResponse;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class InvitedEmailServiceTests {

    @Autowired
    InvitedEmailService invitedEmailService;

    @Autowired
    InvitedEmailRepository invitedEmailRepository;

    @Test
    @DisplayName("response 변환 테스트")
    void convertTest() {
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

        List<InvitedEmailResponse> list = invitedEmailService.findAll();

        assert list.size() == 1;
        assert list.get(0).getEmail().equals(testEmail);
        assert list.get(0).getClassCode().equals(testClassCode);
        assert list.get(0).getClassName().equals(testClassName);
    }

}
