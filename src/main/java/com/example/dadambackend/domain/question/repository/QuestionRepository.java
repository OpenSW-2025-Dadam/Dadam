package com.example.dadambackend.domain.question.repository;

import com.example.dadambackend.domain.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 특정 날짜(questionDate)에 해당하는 질문 중
     * 가장 나중에 생성된(createdAt DESC) 하나만 가져온다.
     * (혹시라도 중복 데이터가 있어도 한 개만 반환하도록 방지 로직)
     */
    Optional<Question> findTopByQuestionDateOrderByCreatedAtDesc(LocalDate questionDate);

    /**
     * 가장 최근에 생성된 질문
     */
    Optional<Question> findTopByOrderByCreatedAtDesc();
}
