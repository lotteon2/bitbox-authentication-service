package com.bitbox.authentication.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
public class OAuthKakaoFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("Content-Type", "application/x-www-form-urlencoded");
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
