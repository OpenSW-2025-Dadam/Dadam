package com.example.dadambackend.domain.balance.repository;

import com.example.dadambackend.domain.balance.model.BalanceGame;
import com.example.dadambackend.domain.balance.model.BalanceGameVote;
import com.example.dadambackend.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceGameVoteRepository extends JpaRepository<BalanceGameVote, Long> {

    boolean existsByBalanceGameAndUser(BalanceGame balanceGame, User user);

    List<BalanceGameVote> findByBalanceGame(BalanceGame balanceGame);

    // 오늘 게임 + 특정 유저의 투표 1건
    Optional<BalanceGameVote> findByBalanceGameAndUser(BalanceGame balanceGame, User user);
}
