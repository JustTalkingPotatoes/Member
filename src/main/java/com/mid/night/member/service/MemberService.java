package com.mid.night.member.service;

import com.mid.night._core.error.exception.Exception400;
import com.mid.night._core.jwt.JWTTokenProvider;
import com.mid.night._core.utils.RedisUtils;
import com.mid.night.member.domain.Authority;
import com.mid.night.member.domain.Member;
import com.mid.night.member.domain.SocialType;
import com.mid.night.member.dto.MemberRequestDTO;
import com.mid.night.member.dto.MemberResponseDTO;
import com.mid.night.member.repository.MemberRepository;
import com.mid.night.redis.repository.RefreshTokenRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtils redisUtils;
    private final JWTTokenProvider jwtTokenProvider;

    /*
       기본 회원 가입
    */
    @Transactional
    public void signUp(MemberRequestDTO.signUpDTO requestDTO) {

        // 비밀번호 확인
        checkValidPassword(requestDTO.password(), passwordEncoder.encode(requestDTO.confirmPassword()));

        // 회원 생성
        Member member = newMember(requestDTO);

        // 회원 저장
        memberRepository.save(member);
    }

    // 비밀번호 확인
    private void checkValidPassword(String rawPassword, String encodedPassword) {

        log.info("{} {}", rawPassword, encodedPassword);

        if(!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new Exception400("비밀번호가 일치하지 않습니다.");
        }
    }

    protected Optional<Member> findMemberByEmail(String email) {
        log.info("회원 확인 : {}", email);

        return memberRepository.findByEmail(email);
    }

    // 회원 생성
    protected Member newMember(MemberRequestDTO.signUpDTO requestDTO) {
        return Member.builder()
                .email(requestDTO.email())
                .password(passwordEncoder.encode(requestDTO.password()))
                .nickName(requestDTO.nickName())
                .socialType(SocialType.NONE)
                .authority(Authority.USER)
                .build();
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
