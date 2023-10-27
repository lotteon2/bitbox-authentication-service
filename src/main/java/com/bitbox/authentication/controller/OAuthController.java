package com.bitbox.authentication.controller;

import com.bitbox.authentication.client.MemberFeignClient;
import com.bitbox.authentication.client.OAuthKakaoFeignClient;
import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.request.KakaoTokenRequest;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import com.bitbox.authentication.dto.response.LoginResponse;
import com.bitbox.authentication.dto.response.Tokens;
import com.bitbox.authentication.entity.AuthMember;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.service.AuthMemberService;
import com.bitbox.authentication.service.InvitedEmailService;
import com.bitbox.authentication.service.JwtService;
import com.bitbox.authentication.service.OAuthKakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import io.github.bitbox.bitbox.dto.MemberRegisterDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.enums.TokenType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthKakaoFeignClient oAuthKakaoFeignClient;
    private final MemberFeignClient memberFeignClient;
    private final AuthMemberService authMemberService;
    private final InvitedEmailService invitedEmailService;
    private final OAuthKakaoService oAuthKakaoService;
    private final JwtService jwtService;

    @Value("${domain.bitbox}")
    private String domain;

    // TODO : REFACTORING
    // 소셜 로그인
    @GetMapping("/kakao/token")
    public ResponseEntity<LoginResponse> getTokenFromKakaoAndAuth(@RequestParam String code) {
            KakaoTokenRequest kakaoTokenRequest = oAuthKakaoService.createKakaoTokenRequest(code);

            KakaoTokenResponse kakaoTokenResponse = oAuthKakaoFeignClient.getTokenFromKakao(kakaoTokenRequest.toString());

            KakaoIdTokenPayload kakaoIdTokenPayload = oAuthKakaoService.decodeKakaoIdToken(kakaoTokenResponse);

            Optional<AuthMember> authMember = authMemberService.findAuthMember(kakaoIdTokenPayload.getEmail());
            Optional<InvitedEmail> invitedEmail = invitedEmailService.findInvitedEmail(kakaoIdTokenPayload.getEmail());

            Long classId = null;
            JwtPayload jwtPayload = null;
            boolean isInvited = false;
            if(invitedEmail.isPresent() && authMember.isPresent()) { // UPDATE_MEMBER_AUTHORITY_TRAINEE
                jwtPayload = JwtPayload.builder()
                        .memberId(authMember.get().getMemberId())
                        .classId(authMember.get().getClassId())
                        .memberProfileImg(authMember.get().getMemberProfileImg())
                        .memberNickname(authMember.get().getMemberNickname())
                        .memberAuthority(AuthorityType.TRAINEE)
                        .build();

                // 초대됐는데 본명이 없으면 본명 설정해야 한다.
                if(authMember.get().getMemberName().isBlank()) {
                    isInvited = true;
                } else { // 이름이 있는 회원인데 초대 이메일이 남아있는 경우 쓰레기값이므로 삭제.
                    invitedEmailService.delete(authMember.get().getMemberEmail());
                }

                classId = invitedEmail.get().getClassId();
            }

            if(invitedEmail.isPresent() && authMember.isEmpty()) { // CREATE_MEMBER_AUTHORITY_TRAINEE
                ResponseEntity<String> memberCreateResponse = memberFeignClient.createMember(MemberRegisterDto.builder()
                        .email(kakaoIdTokenPayload.getEmail())
                        .name(kakaoIdTokenPayload.getNickname())
                        .authority(AuthorityType.TRAINEE)
                        .profileImg(kakaoIdTokenPayload.getPicture())
                        .classId(invitedEmail.get().getClassId())
                        .build());

                jwtPayload = JwtPayload.builder()
                        .classId(invitedEmail.get().getClassId())
                        .memberId(memberCreateResponse.getBody())
                        .memberProfileImg(kakaoIdTokenPayload.getPicture())
                        .memberNickname(kakaoIdTokenPayload.getNickname())
                        .memberAuthority(AuthorityType.TRAINEE)
                        .build();

                classId = invitedEmail.get().getClassId();
            }

            if(invitedEmail.isEmpty() && authMember.isPresent()) { // LOG_IN
                jwtPayload = JwtPayload.builder()
                        .classId(authMember.get().getClassId())
                        .memberId(authMember.get().getMemberId())
                        .memberNickname(authMember.get().getMemberNickname())
                        .memberAuthority(authMember.get().getMemberAuthority())
                        .build();
            }

            if(invitedEmail.isEmpty() && authMember.isEmpty()) { // CREATE_MEMBER_AUTHORITY_GENERAL
                ResponseEntity<String> memberCreateResponse = memberFeignClient.createMember(MemberRegisterDto.builder()
                        .email(kakaoIdTokenPayload.getEmail())
                        .name(kakaoIdTokenPayload.getNickname())
                        .authority(AuthorityType.GENERAL)
                        .profileImg(kakaoIdTokenPayload.getPicture())
                        .classId(null)
                        .build());

                jwtPayload = JwtPayload.builder()
                        .classId(null)
                        .memberId(memberCreateResponse.getBody())
                        .memberProfileImg(kakaoIdTokenPayload.getPicture())
                        .memberNickname(kakaoIdTokenPayload.getNickname())
                        .memberAuthority(AuthorityType.GENERAL)
                        .build();
            }

            Tokens tokens = jwtService.generateTokens(jwtPayload);

            LoginResponse loginResponse = LoginResponse.builder()
                    .sessionToken(tokens.getSessionToken())
                    .accessToken(tokens.getAccessToken())
                    .authority(jwtPayload.getMemberAuthority())
                    .isInvited(isInvited)
                    .classId(classId)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtService.refreshTokenCookie(tokens.getRefreshToken(), TokenType.REFRESH.getValue(), domain).toString())
                    .body(loginResponse);
    }
}
