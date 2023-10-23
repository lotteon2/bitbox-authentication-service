package com.bitbox.authentication.exception.advice;

import com.bitbox.authentication.exception.ErrorResponse;
import com.bitbox.authentication.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notFoundException(NotFoundException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse feignException(FeignException e) {
        return ErrorResponse.builder()
                .message("외부 서버와의 통신에 실패했습니다") // 다른 서비스가 UNAVAILABLE인 경우
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse jsonProcessingException(JsonProcessingException e) {
        return ErrorResponse.builder()
                .message("잠시 후 다시 시도해주세요") //
                .build();
    }
}
