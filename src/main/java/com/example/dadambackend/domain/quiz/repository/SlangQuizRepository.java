package com.example.dadambackend.domain.quiz.repository;

import com.example.dadambackend.domain.quiz.model.SlangQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SlangQuizRepository extends JpaRepository<SlangQuiz, Long> {

    /**
     * 특정 날짜에 해당하는 퀴즈 1개 조회
     * - 하루 1개 정책이므로, createdAt 기준 가장 먼저 생성된 것 하나만 사용
     */
    Optional<SlangQuiz> findFirstByQuizDateOrderByCreatedAtAsc(LocalDate quizDate);
}
