package com.mid.night.member.service;

import com.mid.night._core.error.exception.Exception400;
import com.mid.night._core.error.exception.Exception401;
import com.mid.night._core.jwt.JWTTokenProvider;
import com.mid.night._core.utils.RedisUtils;
import com.mid.night.member.domain.Authority;
import com.mid.night.member.domain.Member;
import com.mid.night.member.domain.SocialType;
import com.mid.night.member.dto.MemberRequestDTO;
import com.mid.night.member.dto.MemberResponseDTO;
import com.mid.night.member.repository.MemberRepository;
import com.mid.night.redis.domain.RefreshToken;
import com.mid.night.redis.repository.RefreshTokenRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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

    public MemberResponseDTO.authTokenDTO login(HttpServletRequest httpServletRequest, MemberRequestDTO.loginDTO requestDTO) {

        // 1. 이메일 확인
        Member member = findMemberByEmail(requestDTO.email())
                .orElseThrow(() -> new Exception401("해당 이메일은 회원 가입 되지 않은 이메일입니다."));

        // 2. 비밀번호 확인
        checkValidPassword(requestDTO.password(), member.getPassword());

        return getAuthTokenDTO(requestDTO.email(), requestDTO.password(), httpServletRequest);
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

    // 토큰 발급
    protected MemberResponseDTO.authTokenDTO getAuthTokenDTO(String email, String password, HttpServletRequest httpServletRequest) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(email, password);
        AuthenticationManager manager = authenticationManagerBuilder.getObject();
        Authentication authentication = manager.authenticate(usernamePasswordAuthenticationToken);

        MemberResponseDTO.authTokenDTO authTokenDTO = jwtTokenProvider.generateToken(authentication);

        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(authentication.getName())
                .authorities(authentication.getAuthorities())
                .refreshToken(authTokenDTO.refreshToken())
                .build()
        );

        return authTokenDTO;
    }

    // 토큰 재발급
    public MemberResponseDTO.authTokenDTO reissueToken(HttpServletRequest httpServletRequest) {

        // Request Header 에서 JWT Token 추출
        String token = jwtTokenProvider.resolveToken(httpServletRequest);

        // 토큰 유효성 검사
        if(token == null || !jwtTokenProvider.validateToken(token)) {
            throw new Exception400("유효하지 않은 Access Token 입니다.");
        }

        // type 확인
        if(!jwtTokenProvider.isRefreshToken(token)) {
            throw new Exception400("유효하지 않은 Refresh Token 입니다.");
        }

        // RefreshToken
        RefreshToken refreshToken = refreshTokenRedisRepository.findByRefreshToken(token);

        if(refreshToken == null) {
            throw new Exception400("유효하지 않은 Refresh Token 입니다.");
        }

        // Redis 에 저장된 RefreshToken 정보를 기반으로 JWT Token 생성
        MemberResponseDTO.authTokenDTO authTokenDTO = jwtTokenProvider.generateToken(
                refreshToken.getId(), refreshToken.getAuthorities()
        );

        // Redis 에 RefreshToken Update
        refreshTokenRedisRepository.save(RefreshToken.builder()
                .id(refreshToken.getId())
                .authorities(refreshToken.getAuthorities())
                .refreshToken(authTokenDTO.refreshToken())
                .build());

        return authTokenDTO;
    }

    /*
        로그아웃
     */
    public void logout(HttpServletRequest httpServletRequest) {

        log.info("로그아웃 - Refresh Token 확인");

        String token = jwtTokenProvider.resolveToken(httpServletRequest);

        if(token == null || !jwtTokenProvider.validateToken(token)) {
            throw new Exception400("유효하지 않은 Refresh Token 입니다.");
        }

        RefreshToken refreshToken = refreshTokenRedisRepository.findByRefreshToken(token);
        refreshTokenRedisRepository.delete(refreshToken);
    }
}
