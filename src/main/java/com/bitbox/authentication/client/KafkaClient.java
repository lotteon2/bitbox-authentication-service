package com.bitbox.authentication.client;

import io.github.bitbox.bitbox.dto.MemberAuthorityDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaClient {
    private final KafkaTemplate<String, MemberAuthorityDto> kafkaTemplate;

    @Value("${memberTopic}")
    private String memberTopic;
    public void createMemberAuthorityModifyEvent(String memberId, AuthorityType authority) {
        kafkaTemplate.send(memberTopic, MemberAuthorityDto.builder()
                .memberId(memberId)
                .memberAuthority(authority)
                .build());
    }
}
