package com.example.dadambackend.domain.question.service;

import com.example.dadambackend.domain.question.dto.QuestionGenerationResult;
import com.example.dadambackend.domain.question.model.Question;
import com.example.dadambackend.domain.question.model.QuestionCategory;
import com.example.dadambackend.domain.question.repository.QuestionRepository;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionAiService questionAiService;

    /**
     * 오늘의 질문을 가져옵니다.
     * - DB에 오늘(questionDate == 오늘) 질문이 하나 이상 있으면
     *   → createdAt 기준으로 가장 최근 것 1개만 사용
     * - 없으면 AI가 새 질문을 생성하고 저장한 뒤 반환
     * - 동시성으로 인한 question_date unique 충돌 시
     *   → 다시 조회하여 이미 생성된 오늘 질문을 반환
     */
    @Transactional
    public Question getTodayQuestion() {
        LocalDate today = LocalDate.now();

        return questionRepository.findTopByQuestionDateOrderByCreatedAtDesc(today)
                .orElseGet(() -> {
                    try {
                        // 아직 없으면 AI를 통해 새로 생성
                        return createTodayQuestionFromAi(today);
                    } catch (DataIntegrityViolationException e) {
                        // 동시에 다른 트랜잭션이 먼저 insert한 경우
                        // 다시 한 번 오늘 날짜 기준으로 가장 최근 질문을 조회
                        return questionRepository.findTopByQuestionDateOrderByCreatedAtDesc(today)
                                .orElseThrow(() -> e);
                    }
                });
    }

    /**
     * 특정 날짜의 질문을 조회합니다. (과거 검색용)
     * - 중복 데이터가 있더라도 가장 최근 것 1개만 사용
     */
    public Question getQuestionByDate(LocalDate date) {
        return questionRepository.findTopByQuestionDateOrderByCreatedAtDesc(date)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
    }

    /**
     * (중요) 특정 ID로 질문을 조회합니다.
     * AnswerService에서 유효성 검사용으로 사용됩니다.
     */
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
    }

    /**
     * AI를 호출해 오늘의 질문을 생성하고 저장한 뒤 반환합니다.
     * - getTodayQuestion() 트랜잭션 내부에서만 호출되는 헬퍼 메서드입니다.
     */
    protected Question createTodayQuestionFromAi(LocalDate today) {
        // 1. AI에게 질문 생성 요청
        QuestionGenerationResult result = questionAiService.generateDailyQuestion();

        // 2. category 문자열을 enum으로 변환
        QuestionCategory category;
        try {
            category = QuestionCategory.valueOf(result.getCategory());
        } catch (Exception e) {
            // 혹시 AI가 이상한 카테고리를 주면 fallback
            category = QuestionCategory.MEMORY;
        }

        // 3. 새 Question 엔티티 생성
        Question question = new Question(
                result.getContent(),
                category,
                today
        );

        // 4. 저장 후 반환
        return questionRepository.save(question);
    }

    /**
     * 초기 데이터 생성: DB가 비어 있을 때 기본 질문 1개를 넣어줍니다.
     */
    @Transactional
    public void createInitialQuestion() {
        if (questionRepository.count() == 0) {
            Question initialQuestion = new Question(
                    "가족과 함께한 가장 즐거웠던 여행은 무엇인가요?",
                    QuestionCategory.TRAVEL,
                    LocalDate.now()
            );
            questionRepository.save(initialQuestion);
        }
    }
}
