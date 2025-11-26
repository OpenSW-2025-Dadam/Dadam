package com.example.dadambackend.domain.balance.repository;

import com.example.dadambackend.domain.balance.model.BalanceGameSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BalanceGameSelectionRepository extends JpaRepository<BalanceGameSelection, Long> {
    // 1. 특정 유저의 참여 여부 확인
    boolean existsByBalanceGameIdAndUserId(Long balanceGameId, Long userId);

    // 2. 특정 게임의 모든 선택 결과를 조회 (결과 화면용)
    List<BalanceGameSelection> findByBalanceGameId(Long balanceGameId);
}
