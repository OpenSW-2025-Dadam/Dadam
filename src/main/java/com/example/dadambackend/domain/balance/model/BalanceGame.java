package com.example.dadambackend.domain.balance.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String questionContent;

    // ⭐ 수정: DB 컬럼 이름 'option_a'를 명시적으로 지정
    @Column(name = "option_a", nullable = false)
    private String optionA;

    // ⭐ 수정: DB 컬럼 이름 'option_b'를 명시적으로 지정
    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "created_at") // createdAt도 명시하는 것이 안전합니다.
    private LocalDateTime createdAt;

    // 생성자 (테스트용)
    public BalanceGame(String questionContent, String optionA, String optionB, LocalDateTime createdAt) {
        this.questionContent = questionContent;
        this.optionA = optionA;
        this.optionB = optionB;
        this.createdAt = createdAt;
    }
}