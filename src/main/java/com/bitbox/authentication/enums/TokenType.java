package com.bitbox.authentication.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    ACCESS("Access"),
    REFRESH("Refresh");

    private final String value;
}
