package com.example.dadambackend.domain.balance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "balance_game",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_balance_game_game_date", columnNames = "game_date")
        }
)
public class BalanceGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 오늘 날짜 기준으로 하루 한 개만
    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false, length = 255)
    private String question;

    @Column(name = "option_a", nullable = false, length = 100)
    private String optionA;

    @Column(name = "option_b", nullable = false, length = 100)
    private String optionB;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BalanceGame(LocalDate gameDate,
                       String question,
                       String optionA,
                       String optionB,
                       String category) {
        this.gameDate = gameDate;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }
}
