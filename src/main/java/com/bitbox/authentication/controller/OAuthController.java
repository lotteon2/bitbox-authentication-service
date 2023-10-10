package com.bitbox.authentication.controller;

import com.bitbox.authentication.client.MemberFeignClient;
import com.bitbox.authentication.client.OAuthKakaoFeignClient;
import com.bitbox.authentication.dto.*;
import com.bitbox.authentication.entity.AuthMember;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.service.AuthMemberService;
import com.bitbox.authentication.service.InvitedEmailService;
import com.bitbox.authentication.service.JwtService;
import com.bitbox.authentication.service.OAuthKakaoService;
import com.bitbox.authentication.vo.JwtPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.bitbox.bitbox.enums.AuthorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthKakaoFeignClient oAuthKakaoFeignClient;
    private final MemberFeignClient memberFeignClient;
    private final OAuthKakaoService oAuthKakaoService;
    private final JwtService jwtService;
    private final AuthMemberService authMemberService; // TODO : BAD SMELL
    private final InvitedEmailService invitedEmailService; // TODO : BAD SMELL

    @GetMapping("auth/kakao")
    public ResponseEntity<?> redirectToKakao() {
        return new ResponseEntity<>(
                oAuthKakaoService.redirect(oAuthKakaoService.kakaoRedirectURI()),
                HttpStatus.MOVED_PERMANENTLY
        );
    }

    @GetMapping("oauth/token")
    public ResponseEntity<?> getTokenFromKakaoAndAuth(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        // kakao에서 인가 코드 받아오는데 문제가 생겼다면 early return
        if(!error.isBlank() && !errorDescription.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDescription);
        }

        try {
            // kakao와의 통신에서 문제가 발생할 경우 error handling
            KakaoTokenResponse kakaoTokenResponse =
                    oAuthKakaoFeignClient.getTokenFromKakao(KakaoTokenRequest.builder().code(code).build().toString());

            // decoding 과정에서 문제가 발생할 경우 error handling
            KakaoIdTokenPayload kakaoIdTokenPayload = oAuthKakaoService.decodeKakaoIdToken(kakaoTokenResponse);

            // TODO : 이하 서비스로
            Optional<AuthMember> authMember =
                    authMemberService.findAuthMemberByMemberEmailAndDeletedIsFalse(kakaoIdTokenPayload.getEmail());
            Optional<InvitedEmail> invitedEmail =
                    invitedEmailService.findInvitedEmailByEmail(kakaoIdTokenPayload.getEmail());

            TokenResponse tokenResponse = null;

            // member service와의 통신에서 문제가 발생할 경우 error handling
            if(invitedEmail.isPresent()) {
                if(authMember.isPresent()) { // UPDATE_MEMBER_AUTHORITY_TRAINEE
                    tokenResponse = jwtService.generateTokens(JwtPayload.builder()
                            .memberId(authMember.get().getMemberId())
                            .classId(authMember.get().getClassId())
                            .memberNickname(authMember.get().getMemberNickname())
                            .memberAuthority(AuthorityType.TRAINEE)
                            .build());

                    memberFeignClient.updateAuthority(
                            tokenResponse.getAccessToken(),
                            tokenResponse.getRefreshToken()
                    );
                } else { // CREATE_MEMBER_AUTHORITY_TRAINEE
                    memberFeignClient.createMember(MemberRequest.builder()
                            .memberEmail(kakaoIdTokenPayload.getEmail())
                            .memberNickName(kakaoIdTokenPayload.getNickname())
                            .memberAuthority(AuthorityType.TRAINEE)
                            .memberProfileImg(kakaoIdTokenPayload.getPicture())
                            .build());
                }
            } else {
                if(authMember.isEmpty()) { // CREATE_MEMBER_AUTHORITY_GENERAL
                    memberFeignClient.createMember(MemberRequest.builder()
                            .memberEmail(kakaoIdTokenPayload.getEmail())
                            .memberNickName(kakaoIdTokenPayload.getNickname())
                            .memberAuthority(AuthorityType.GENERAL)
                            .memberProfileImg(kakaoIdTokenPayload.getPicture())
                            .build());
                } else { // LOG_IN
                    tokenResponse = jwtService.generateTokens(JwtPayload.builder()
                            .memberId(authMember.get().getMemberId())
                            .classId(authMember.get().getClassId())
                            .memberNickname(authMember.get().getMemberNickname())
                            .memberAuthority(authMember.get().getMemberAuthority())
                            .build());
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);

        } catch (JsonProcessingException e) {
            e.printStackTrace(); // TODO : handle exception
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
