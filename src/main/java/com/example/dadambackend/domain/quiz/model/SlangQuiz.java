package com.example.dadambackend.domain.quiz.model;

import com.example.dadambackend.domain.quiz.dto.SlangQuizGenerationResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "slang_quiz")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlangQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 이 퀴즈가 표시될 날짜 (하루 1개 정책용) */
    @Column(nullable = false)
    private LocalDate quizDate;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 500)
    private String answer;

    /**
     * 보기들을 "||" 로 이어서 저장 (예: "보기1||보기2||보기3")
     * - DB 스키마를 단순하게 유지하기 위한 문자열 컬럼
     */
    @Column(nullable = false, length = 1000)
    private String choices;

    @Column(nullable = false, length = 500)
    private String explanation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // === 생성 팩토리 ===
    public static SlangQuiz of(LocalDate quizDate, SlangQuizGenerationResult dto) {
        SlangQuiz quiz = new SlangQuiz();
        quiz.quizDate = quizDate;
        quiz.question = dto.getQuestion();
        quiz.answer = dto.getAnswer();
        // choices 배열을 "||" 로 join 해서 저장
        String[] choiceArr = dto.getChoices() != null ? dto.getChoices() : new String[]{dto.getAnswer()};
        quiz.choices = String.join("||", choiceArr);
        quiz.explanation = dto.getExplanation();
        return quiz;
    }

    /** DB에 저장된 choices 문자열을 다시 배열로 변환 */
    public String[] getChoiceArray() {
        if (choices == null || choices.isBlank()) {
            return new String[0];
        }
        return choices.split("\\|\\|");
    }

    /** Entity → DTO 변환 헬퍼 */
    public SlangQuizGenerationResult toDto() {
        SlangQuizGenerationResult dto = new SlangQuizGenerationResult();
        dto.setQuestion(this.question);
        dto.setAnswer(this.answer);
        dto.setChoices(this.getChoiceArray());
        dto.setExplanation(this.explanation);
        return dto;
    }

    @Override
    public String toString() {
        return "SlangQuiz{" +
                "id=" + id +
                ", quizDate=" + quizDate +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", choices=" + Arrays.toString(getChoiceArray()) +
                '}';
    }
}
