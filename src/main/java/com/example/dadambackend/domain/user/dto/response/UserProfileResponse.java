package com.example.dadambackend.domain.user.dto.response;

import com.example.dadambackend.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "내 프로필 응답")
public class UserProfileResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "가족 내 역할(child/parent/grandparent)", example = "child")
    private String familyRole;

    @Schema(description = "가족 코드", example = "FAM1234")
    private String familyCode;

    @Schema(description = "프로필 이미지 URL", example = "/uploads/avatars/user1.png")
    private String avatarUrl;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getFamilyRole(),
                user.getFamilyCode(),
                user.getAvatarUrl()
        );
    }
}
