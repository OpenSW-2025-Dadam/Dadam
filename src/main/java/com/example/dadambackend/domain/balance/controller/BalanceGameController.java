package com.example.dadambackend.domain.balance.controller;

import com.example.dadambackend.domain.balance.dto.BalanceGameTodayResponse;
import com.example.dadambackend.domain.balance.dto.BalanceGameVoteRequest;
import com.example.dadambackend.domain.balance.service.BalanceGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceGameController {

    private final BalanceGameService balanceGameService;

    /**
     * 오늘의 밸런스 게임 조회 (없으면 생성해서 반환)
     * GET /api/v1/balance/today
     */
    @GetMapping("/today")
    public ResponseEntity<BalanceGameTodayResponse> getTodayGame() {
        return ResponseEntity.ok(balanceGameService.getOrCreateTodayGame());
    }

    /**
     * 오늘의 밸런스 게임에 투표 (A/B)
     * POST /api/v1/balance/today/vote
     */
    @PostMapping("/today/vote")
    public ResponseEntity<BalanceGameTodayResponse> voteToday(
            @RequestBody BalanceGameVoteRequest request
    ) {
        return ResponseEntity.ok(balanceGameService.voteToday(request));
    }
}
