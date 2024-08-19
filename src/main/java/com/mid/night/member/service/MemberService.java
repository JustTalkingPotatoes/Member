package com.mid.night.member.service;

import com.mid.night.member.dto.MemberRequestDTO;
import com.mid.night.member.dto.MemberResponseDTO;
import com.mid.night.member.repository.MemberRepository;
import com.mid.night.redis.repository.RefreshTokenRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public void signUp(MemberRequestDTO.signUpDTO requestDTO) {

    }

    public MemberResponseDTO.authTokenDTO login(HttpServletRequest httpServletRequest, MemberRequestDTO.loginDTO requestDTO) {
        return null;
    }

    public MemberResponseDTO.authTokenDTO reissueToken(HttpServletRequest httpServletRequest) {
        return null;
    }

    public void logout(HttpServletRequest httpServletRequest) {

    }
}
