package com.example.dadambackend.domain.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // 프로필 이미지 URL
    private String avatarUrl;

    // 가족 역할 (child / parent / grandparent 등)
    private String familyRole;

    // 초대 코드
    private String familyCode;

    @Builder
    public User(String email, String password, String name,
                String avatarUrl, String familyRole, String familyCode) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.familyRole = familyRole;
        this.familyCode = familyCode;
    }

    // ============================
    // UPDATE METHODS
    // ============================

    /** 프사 변경 */
    public void updateAvatar(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /** 프사 제거 (기본 아바타) */
    public void clearAvatar() {
        this.avatarUrl = null;
    }

    /** 프로필 전체 수정 */
    public void updateProfile(String name, String familyRole, String familyCode, String avatarUrl) {
        if (name != null) this.name = name;
        if (familyRole != null) this.familyRole = familyRole;
        if (familyCode != null) this.familyCode = familyCode;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }
}
