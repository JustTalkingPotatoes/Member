package com.mid.night.member.domain;

import com.mid.night.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_tb")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;
    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 255, nullable = false)
    private String nickName;

    @ColumnDefault("'NONE'")
    private SocialType socialType;
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'USER'")
    private Authority authority;

    @Builder
    public Member(String email, String password, String nickName, SocialType socialType, Authority authority) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.socialType = socialType;
        this.authority = authority;
    }
}
