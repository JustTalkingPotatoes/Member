package com.mid.night.member.service;

import com.mid.night.member.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberSocialLoginService {
    public MemberResponseDTO.authTokenDTO kakaoLogin(String code) {
        return null;
    }
}
