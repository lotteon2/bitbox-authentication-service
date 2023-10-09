package com.bitbox.authentication.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Builder
@Getter
@RedisHash(value = "invited_email")
public class InvitedEmail {
    @Id
    private String id;

    @Indexed // 명시적으로 Set 자료구조로 선언
    private String email;
}
