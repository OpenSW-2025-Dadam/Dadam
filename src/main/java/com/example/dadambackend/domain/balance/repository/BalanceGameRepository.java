package com.example.dadambackend.domain.balance.repository;

import com.example.dadambackend.domain.balance.model.BalanceGame;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BalanceGameRepository extends JpaRepository<BalanceGame, Long> {
    // 가장 최근에 생성된 게임을 찾습니다 (기간별 문제 변경 로직의 기초)
    Optional<BalanceGame> findTopByOrderByCreatedAtDesc();
}
