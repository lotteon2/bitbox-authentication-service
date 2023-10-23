package com.bitbox.authentication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedEmailRequest {

    @NotEmpty
    private String email;

    @NotNull
    private Long classId;

    @NotEmpty
    private String classCode;

    @NotEmpty
    private String className;
}
