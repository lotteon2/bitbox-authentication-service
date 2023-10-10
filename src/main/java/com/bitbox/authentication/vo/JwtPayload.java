package com.bitbox.authentication.vo;

import com.bitbox.authentication.enums.AuthorityType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtPayload {
    private String memberId;
    private Long classId;
    private String memberNickname;
    private AuthorityType memberAuthority;
}
