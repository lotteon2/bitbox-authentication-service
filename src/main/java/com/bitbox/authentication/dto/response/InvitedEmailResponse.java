package com.bitbox.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedEmailResponse {
    private String email;
    private String classCode;
    private String className;
}
