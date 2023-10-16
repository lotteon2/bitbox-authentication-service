package com.bitbox.authentication.client;

import com.bitbox.authentication.config.MemberFeignConfig;
import com.bitbox.authentication.dto.request.MemberRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "memberFeignClient",
        url = "localhost:8000/user-service",
        configuration = MemberFeignConfig.class
)
@Component
public interface MemberFeignClient {
    @PostMapping(value = "/member/signup")
    ResponseEntity<String> createMember(@RequestBody MemberRequest memberRequest);
}
