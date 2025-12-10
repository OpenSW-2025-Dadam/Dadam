package com.example.dadambackend.domain.balance.model;

import com.example.dadambackend.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "balance_game_vote",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_balance_game_vote_game_user",
                        columnNames = {"balance_game_id", "user_id"}
                )
        }
)
public class BalanceGameVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 오늘의 밸런스 게임
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "balance_game_id", nullable = false)
    private BalanceGame balanceGame;

    // 투표한 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // "A" 또는 "B"
    @Column(nullable = false, length = 1)
    private String choice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BalanceGameVote(BalanceGame balanceGame, User user, String choice) {
        this.balanceGame = balanceGame;
        this.user = user;
        this.choice = choice;
        this.createdAt = LocalDateTime.now();
    }

    // choice 변경용 (이미 투표한 사람이 다시 선택 바꿀 때)
    public void updateChoice(String choice) {
        this.choice = choice;
    }
}
