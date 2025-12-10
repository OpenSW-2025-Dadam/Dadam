package com.example.dadambackend.domain.balance.dto;

import com.example.dadambackend.domain.balance.model.BalanceGame;
import com.example.dadambackend.domain.balance.model.BalanceGameVote;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BalanceGameTodayResponse {

    private Long id;
    private String question;
    private String optionA;
    private String optionB;
    private String category;

    // A / B 를 고른 사람들
    private List<Voter> votesA;
    private List<Voter> votesB;

    @Getter
    @Builder
    public static class Voter {
        private Long userId;
        private String userName;
    }

    public static BalanceGameTodayResponse of(BalanceGame game, List<BalanceGameVote> votes) {
        List<Voter> votesA = votes.stream()
                .filter(v -> "A".equals(v.getChoice()))
                .map(v -> Voter.builder()
                        .userId(v.getUser().getId())
                        .userName(v.getUser().getName())
                        .build())
                .collect(Collectors.toList());

        List<Voter> votesB = votes.stream()
                .filter(v -> "B".equals(v.getChoice()))
                .map(v -> Voter.builder()
                        .userId(v.getUser().getId())
                        .userName(v.getUser().getName())
                        .build())
                .collect(Collectors.toList());

        return BalanceGameTodayResponse.builder()
                .id(game.getId())
                .question(game.getQuestion())
                .optionA(game.getOptionA())
                .optionB(game.getOptionB())
                .category(game.getCategory())
                .votesA(votesA)
                .votesB(votesB)
                .build();
    }
}
