package com.bitbox.authentication.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthorityType {
    ADMIN,
    MANAGER,
    TEACHER,
    GRADUATE,
    TRAINEE,
    GENERAL
}
