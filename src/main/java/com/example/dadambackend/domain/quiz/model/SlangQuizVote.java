package com.example.dadambackend.domain.quiz.model;

import com.example.dadambackend.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "slang_quiz_vote",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_slang_quiz_vote_quiz_user",
                columnNames = {"slang_quiz_id", "user_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlangQuizVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slang_quiz_id", nullable = false)
    private SlangQuiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 사용자가 선택한 보기 인덱스 (0, 1, 2 ...)
     */
    @Column(nullable = false)
    private Integer choiceIndex;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public SlangQuizVote(SlangQuiz quiz, User user, int choiceIndex) {
        this.quiz = quiz;
        this.user = user;
        this.choiceIndex = choiceIndex;
    }

    public void updateChoice(int choiceIndex) {
        this.choiceIndex = choiceIndex;
    }
}
