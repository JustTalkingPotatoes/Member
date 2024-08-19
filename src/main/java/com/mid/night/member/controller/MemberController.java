package com.mid.night.member.controller;

import com.mid.night._core.utils.ApiUtils;
import com.mid.night.member.dto.MemberResponseDTO;
import com.mid.night.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    /*
        기본 회원 가입
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody signUpDTO requestDTO) {

        memberService.signUp(requestDTO);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    /*
        기본 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest httpServletRequest, @Valid @RequestBody loginDTO requestDTO) {

        MemberResponseDTO.authTokenDTO responseDTO = memberService.login(httpServletRequest, requestDTO);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    /*
        Access Token 재발급 - Refresh Token 필요
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest httpServletRequest) {

        MemberResponseDTO.authTokenDTO responseDTO = memberService.reissueToken(httpServletRequest);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    /*
        로그아웃 - Refresh Token 필요
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {

        log.info("로그아웃 시도");

        memberService.logout(httpServletRequest);

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}
