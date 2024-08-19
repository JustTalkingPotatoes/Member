package com.mid.night.member.dto;

public class MemberResponseDTO {

    // 토큰 발급
    public record authTokenDTO(
            String grantType,
            String accessToken,
            Long accessTokenValidTime,
            String refreshToken,
            Long refreshTokenValidTime
    ) {
    }
}
