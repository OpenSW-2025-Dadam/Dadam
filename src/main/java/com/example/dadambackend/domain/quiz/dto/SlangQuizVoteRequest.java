package com.example.dadambackend.domain.quiz.dto;

import lombok.Getter;

@Getter
public class SlangQuizVoteRequest {
    /**
     * 사용자가 선택한 보기 인덱스 (0, 1, 2 ...)
     */
    private int choiceIndex;
}
