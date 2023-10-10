package com.bitbox.authentication.dto;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRequest {
    private String memberNickName;
    private String memberEmail;
    private String memberProfileImg;
    private AuthorityType memberAuthority;
}
