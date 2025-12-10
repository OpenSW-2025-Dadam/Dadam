package com.example.dadambackend.domain.quiz.controller;

import com.example.dadambackend.domain.quiz.dto.SlangQuizTodayResponse;
import com.example.dadambackend.domain.quiz.dto.SlangQuizVoteRequest;
import com.example.dadambackend.domain.quiz.service.SlangQuizService;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import com.example.dadambackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class SlangQuizController {

    private final SlangQuizService slangQuizService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 오늘자 퀴즈 조회 (로그인 안 해도 가능)
     *  - 로그인 했으면 내 선택 인덱스 포함
     */
    @GetMapping("/today")
    public ResponseEntity<SlangQuizTodayResponse> getTodayQuiz(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdOrNull(authHeader);
        SlangQuizTodayResponse quiz = slangQuizService.getOrCreateTodayQuiz(userId);
        return ResponseEntity.ok(quiz);
    }

    /**
     * 오늘자 퀴즈에 투표 (로그인 필수)
     */
    @PostMapping("/today/vote")
    public ResponseEntity<SlangQuizTodayResponse> voteToday(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SlangQuizVoteRequest request
    ) {
        Long userId = extractUserIdOrThrow(authHeader);
        SlangQuizTodayResponse quiz =
                slangQuizService.voteToday(userId, request.getChoiceIndex());
        return ResponseEntity.ok(quiz);
    }

    /* =================== 헬퍼 =================== */

    private Long extractUserIdOrNull(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return null;
        }
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private Long extractUserIdOrThrow(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
