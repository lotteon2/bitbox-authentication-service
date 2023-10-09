package com.bitbox.authentication.client;

import com.bitbox.authentication.config.MemberFeignConfig;
import com.bitbox.authentication.dto.MemberRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "memberFeignClient",
        url = "localhost:9999", // TODO : Spring Cloud
        configuration = MemberFeignConfig.class
)
@Component
public interface MemberFeignClient {
    @PostMapping(value = "/member/signup")
    String createMember(@RequestBody MemberRequest memberRequest);

    // TODO : member에 권한 update 요청 ap
    @PatchMapping(value = "/member/authority")
    ResponseEntity<?> updateAuthority(
            @RequestHeader("Access-Token") String accessToken,
            @RequestHeader("Refresh-Token") String refreshToken
    );
}
