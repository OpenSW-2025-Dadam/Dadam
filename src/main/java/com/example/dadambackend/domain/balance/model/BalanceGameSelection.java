package com.example.dadambackend.domain.balance.model;

import com.example.dadambackend.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceGameSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_game_id", nullable = false)
    private BalanceGame balanceGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1) // 'A' 또는 'B'
    private String selectedOption;

    private LocalDateTime createdAt;

    // 생성자
    public BalanceGameSelection(BalanceGame balanceGame, User user, String selectedOption) {
        this.balanceGame = balanceGame;
        this.user = user;
        this.selectedOption = selectedOption;
        this.createdAt = LocalDateTime.now();
    }
}
