package com.example.dadambackend.domain.question.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "question",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_question_date", columnNames = "question_date")
        }
)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionCategory category;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "question_date", nullable = false)
    private LocalDate questionDate;

    public Question(String content, QuestionCategory category, LocalDate questionDate) {
        this.content = content;
        this.category = category;
        this.questionDate = questionDate;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 어떤 방식으로든 엔티티가 생성되더라도
     * createdAt이 비어 있으면 저장 시점에 자동 세팅되도록 보장
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
