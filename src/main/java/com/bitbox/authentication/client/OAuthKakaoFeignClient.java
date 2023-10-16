package com.bitbox.authentication.client;

import com.bitbox.authentication.config.OAuthKakaoFeignConfig;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "oauthKakaoFeignClient",
        url = "https://kauth.kakao.com",
        configuration = OAuthKakaoFeignConfig.class
)
@Component
public interface OAuthKakaoFeignClient {
    @PostMapping(value = "/oauth/token")
    KakaoTokenResponse getTokenFromKakao(@RequestBody String kakaoTokenRequest);
}
