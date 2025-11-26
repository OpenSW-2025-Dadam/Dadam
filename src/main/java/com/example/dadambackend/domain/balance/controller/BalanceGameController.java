package com.example.dadambackend.domain.balance.controller;

import com.example.dadambackend.domain.balance.dto.BalanceGameRequest;
import com.example.dadambackend.domain.balance.dto.BalanceGameResponse;
import com.example.dadambackend.domain.balance.service.BalanceGameService;
import com.example.dadambackend.global.exception.BusinessException; // ⭐ 추가: BusinessException import
import com.example.dadambackend.global.exception.ErrorCode;         // ⭐ 추가: ErrorCode import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance-games")
@RequiredArgsConstructor
public class BalanceGameController {

    private final BalanceGameService balanceGameService;

    /**
     * GET /api/v1/balance-games/current
     * 현재 활성화된 밸런스 게임 질문과 사용자 참여 상태를 조회
     */
    @GetMapping("/current")
    public ResponseEntity<BalanceGameResponse> getCurrentBalanceGame() {
        // Service 계층에서 DTO (BalanceGameResponse)를 반환받아 응답
        BalanceGameResponse response = balanceGameService.getCurrentGameWithStatus();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/balance-games/{gameId}/select
     * 밸런스 게임에 답변 제출
     */
    @PostMapping("/{gameId}/select")
    public ResponseEntity<Void> submitSelection(
            @PathVariable Long gameId,
            @RequestBody BalanceGameRequest request) {

        // selectedOption 유효성 검사 (A 또는 B만 허용)
        if (!"A".equals(request.getSelectedOption()) && !"B".equals(request.getSelectedOption())) {
            // 유효하지 않은 요청이라면 BusinessException을 발생시킵니다.
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        balanceGameService.submitSelection(gameId, request.getSelectedOption());

        // 201 Created 응답 (성공적인 리소스 생성)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}