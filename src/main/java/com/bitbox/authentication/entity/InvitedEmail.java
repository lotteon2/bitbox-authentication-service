package com.bitbox.authentication.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@Builder
@Getter
@RedisHash(value = "invited_email")
public class InvitedEmail {
    @Id
    private String id;

    @Indexed
    private String email;

    private Long classId;
}
