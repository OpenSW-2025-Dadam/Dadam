package com.example.dadambackend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "로그인 응답")
public class LoginResponse {
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
}
