package com.example.dadambackend.domain.balance.repository;

import com.example.dadambackend.domain.balance.model.BalanceGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BalanceGameRepository extends JpaRepository<BalanceGame, Long> {

    Optional<BalanceGame> findByGameDate(LocalDate gameDate);
}
