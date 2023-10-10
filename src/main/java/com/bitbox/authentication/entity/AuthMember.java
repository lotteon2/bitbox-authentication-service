package com.bitbox.authentication.entity;

import com.bitbox.authentication.enums.AuthorityType;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(indexes = @Index(name = "idx_member_email", columnList = "memberEmail"))
public class AuthMember {
    @Id
    private String memberId;

    @Column
    private Long classId;

    @Column(nullable = false)
    private String memberEmail;

    @Column(nullable = false)
    private String memberNickname;

    @Enumerated(EnumType.STRING)
    private AuthorityType memberAuthority;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;
}
