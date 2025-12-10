package com.example.dadambackend.domain.balance.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BalanceGameVoteRequest {
    // "A" 또는 "B"
    private String choice;
}
