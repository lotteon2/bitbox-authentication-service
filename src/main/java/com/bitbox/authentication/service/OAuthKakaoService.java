package com.bitbox.authentication.service;

import com.bitbox.authentication.client.KafkaClient;
import com.bitbox.authentication.client.MemberFeignClient;
import com.bitbox.authentication.dto.KakaoIdTokenPayload;
import com.bitbox.authentication.dto.request.KakaoTokenRequest;
import com.bitbox.authentication.dto.response.KakaoTokenResponse;
import com.bitbox.authentication.entity.AuthMember;
import com.bitbox.authentication.entity.InvitedEmail;
import com.bitbox.authentication.repository.AuthMemberRepository;
import com.bitbox.authentication.repository.InvitedEmailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.bitbox.bitbox.dto.MemberRegisterDto;
import io.github.bitbox.bitbox.enums.AuthorityType;
import io.github.bitbox.bitbox.jwt.JwtPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthKakaoService {
    private final Environment env;
    private final ObjectMapper objectMapper;
    private final Base64.Decoder decoder;

    private final AuthMemberRepository authMemberRepository;
    private final InvitedEmailRepository invitedEmailRepository;
    private final MemberFeignClient memberFeignClient;
    private final KafkaClient memberKafkaClient;

    public HttpHeaders redirect(String redirectURI) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(redirectURI));
        return httpHeaders;
    }

    public String kakaoRedirectURI() {
        return new StringBuilder()
                .append("https://kauth.kakao.com/oauth/authorize?")
                .append("client_id=").append(env.getProperty("oauth.kakao.client-id"))
                .append("&")
                .append("redirect_uri=").append(env.getProperty("oauth.kakao.redirect-uri"))
                .append("&")
                .append("response_type=").append("code")
                .append("&")
                .append("scope=").append(env.getProperty("oauth.kakao.scope"))
                .toString();
    }

    public KakaoTokenRequest createKakaoTokenRequest(String code) {
        return KakaoTokenRequest.builder()
                .code(code)
                .clientId(env.getProperty("oauth.kakao.client-id"))
                .clientSecret(env.getProperty("oauth.kakao.client-secret"))
                .redirectUri(env.getProperty("oauth.kakao.redirect-uri"))
                .build();
    }

    public KakaoIdTokenPayload decodeKakaoIdToken(KakaoTokenResponse kakaoTokenResponse) throws JsonProcessingException {
        return objectMapper.readValue(new String(decoder.decode(kakaoTokenResponse.getIdToken().split("\\.")[1])),
                KakaoIdTokenPayload.class);
    }

    // TODO : REFACTORING
    public JwtPayload convertToJwtPayload(KakaoIdTokenPayload kakaoIdTokenPayload) throws FeignException {
        Optional<AuthMember> authMember =
                authMemberRepository.findByMemberEmailAndDeletedIsFalse(kakaoIdTokenPayload.getEmail());
        Optional<InvitedEmail> invitedEmail =
                invitedEmailRepository.findByEmail(kakaoIdTokenPayload.getEmail());
        
        JwtPayload jwtPayload = null;
        // member service와의 통신에서 문제가 발생할 경우 error handling
        if(invitedEmail.isPresent() && authMember.isPresent()) { // UPDATE_MEMBER_AUTHORITY_TRAINEE
            memberKafkaClient.createMemberAuthorityModifyEvent(authMember.get().getMemberId(),
                    authMember.get().getMemberAuthority());

            jwtPayload = JwtPayload.builder()
                    .memberId(authMember.get().getMemberId())
                    .classId(authMember.get().getClassId())
                    .memberNickname(authMember.get().getMemberNickname())
                    .memberAuthority(AuthorityType.TRAINEE)
                    .build();

            invitedEmailRepository.delete(invitedEmail.get());
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
                    .memberNickname(kakaoIdTokenPayload.getNickname())
                    .memberAuthority(AuthorityType.TRAINEE)
                    .build();

            invitedEmailRepository.delete(invitedEmail.get());
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
                    .memberNickname(kakaoIdTokenPayload.getNickname())
                    .memberAuthority(AuthorityType.TRAINEE)
                    .build();
        }

        return jwtPayload;
    }
}
