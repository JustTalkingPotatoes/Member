package com.mid.night.member.controller;

import com.mid.night._core.utils.ApiUtils;
import com.mid.night.member.dto.MemberResponseDTO;
import com.mid.night.member.service.MemberSocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class MemberSocialLoginController {

    private final MemberSocialLoginService memberSocialLoginService;

    // https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=049ebf43b54fe431e060d0aa0a8e39af&redirect_uri=http://localhost:8080/api/auth/kakao/login
    /*
        카카오 로그인
     */
    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam(name = "code") String code) {

        MemberResponseDTO.authTokenDTO responseDTO = memberSocialLoginService.kakaoLogin(code);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}
