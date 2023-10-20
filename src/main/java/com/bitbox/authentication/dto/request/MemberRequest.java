package com.bitbox.authentication.dto.request;

import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberRequest {
    private String memberNickname;
    private String memberEmail;
    private String memberProfileImg;
    private Long classId;
    private AuthorityType memberAuthority;
}
