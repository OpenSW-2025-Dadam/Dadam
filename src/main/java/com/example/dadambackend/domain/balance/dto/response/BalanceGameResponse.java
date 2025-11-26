// com/example/dadambackend/domain/balance/dto/BalanceGameResponse.java
package com.example.dadambackend.domain.balance.dto;

import com.example.dadambackend.domain.balance.model.BalanceGame;
import lombok.Builder;
import lombok.Getter;

import java.util.List; // ⭐ 추가: List import

@Getter
@Builder
public class BalanceGameResponse {
    private Long id;
    private String questionContent;
    private String optionA;
    private String optionB;

    private boolean hasParticipated;

    // ⭐ 수정: 투표 인원 수 대신, 개별 선택 정보 리스트로 대체
    private List<SelectionInfo> selectionDetails;

    // ⭐ 수정된 from 메서드 시그니처
    public static BalanceGameResponse from(BalanceGame game, boolean participated, List<SelectionInfo> selectionDetails) {
        return BalanceGameResponse.builder()
                .id(game.getId())
                .questionContent(game.getQuestionContent())
                .optionA(game.getOptionA())
                .optionB(game.getOptionB())
                .hasParticipated(participated)
                .selectionDetails(selectionDetails) // ⭐ 추가/수정된 필드
                .build();
    }
}