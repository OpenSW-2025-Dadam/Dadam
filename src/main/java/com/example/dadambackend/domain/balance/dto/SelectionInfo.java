// com/example/dadambackend/domain/balance/dto/SelectionInfo.java
package com.example.dadambackend.domain.balance.dto;

import com.example.dadambackend.domain.balance.model.BalanceGameSelection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SelectionInfo {
    private Long userId;
    private String userName;
    private String selectedOption; // 'A' 또는 'B'

    public static SelectionInfo from(BalanceGameSelection selection) {
        return SelectionInfo.builder()
                .userId(selection.getUser().getId())
                .userName(selection.getUser().getName())
                .selectedOption(selection.getSelectedOption())
                .build();
    }
}