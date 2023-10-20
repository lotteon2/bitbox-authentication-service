package com.bitbox.authentication.entity;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(indexes = @Index(name = "idx_email", columnList = "adminEmail"))
public class AuthAdmin {
    @Id
    private String adminId;

    @Column(nullable = false)
    private String adminEmail;

    @Column(nullable = false)
    private String adminPassword;

    @Column(nullable = false)
    private String adminName;

    @Enumerated(EnumType.STRING)
    private AuthorityType adminAuthority;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;
}
