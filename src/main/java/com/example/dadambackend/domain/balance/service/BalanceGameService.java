// com/example/dadambackend/domain/balance/service/BalanceGameService.java
package com.example.dadambackend.domain.balance.service;

import com.example.dadambackend.domain.balance.dto.BalanceGameResponse;
import com.example.dadambackend.domain.balance.dto.SelectionInfo; // ⭐ 추가: SelectionInfo import
import com.example.dadambackend.domain.balance.model.BalanceGame;
import com.example.dadambackend.domain.balance.model.BalanceGameSelection;
import com.example.dadambackend.domain.balance.repository.BalanceGameRepository;
import com.example.dadambackend.domain.balance.repository.BalanceGameSelectionRepository;
import com.example.dadambackend.domain.user.model.User;
import com.example.dadambackend.domain.user.repository.UserRepository;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // List 및 stream 사용

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BalanceGameService {
    private final BalanceGameRepository balanceGameRepository;
    private final BalanceGameSelectionRepository selectionRepository;
    private final UserRepository userRepository;

    // 현재 사용자 ID (임시: 실제로는 시큐리티 컨텍스트에서 가져와야 함)
    private final Long TEMP_USER_ID = 1L;

    /**
     * 현재 활성화된 밸런스 게임을 조회하고, 사용자의 참여 여부와 전체 선택자 상세 정보를 반환합니다.
     * @return BalanceGameResponse: 질문 정보, 참여 여부, 전체 참여자 리스트
     */
    public BalanceGameResponse getCurrentGameWithStatus() {
        // 1. 가장 최근의 밸런스 게임 질문을 가져옵니다.
        BalanceGame game = balanceGameRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        // 2. 현재 사용자가 이 게임에 참여했는지 확인합니다.
        boolean hasParticipated = selectionRepository.existsByBalanceGameIdAndUserId(
                game.getId(), TEMP_USER_ID);

        // 3. ⭐ 수정: 모든 참여자의 선택 정보를 조회합니다. (참여 여부와 관계없이 조회)
        List<BalanceGameSelection> selections = selectionRepository.findByBalanceGameId(game.getId());

        // 4. ⭐ 수정: 엔티티 리스트를 DTO 리스트(SelectionInfo)로 변환합니다.
        List<SelectionInfo> selectionDetails = selections.stream()
                .map(SelectionInfo::from)
                .collect(Collectors.toList());

        // 5. DTO로 변환하여 반환합니다.
        return BalanceGameResponse.from(game, hasParticipated, selectionDetails);
    }

    /**
     * 답변 제출 로직 (이 부분은 수정할 필요 없습니다.)
     */
    @Transactional
    public void submitSelection(Long gameId, String selectedOption) {
        // 1. 중복 참여 확인
        if (selectionRepository.existsByBalanceGameIdAndUserId(gameId, TEMP_USER_ID)) {
            throw new BusinessException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 2. 게임과 사용자 객체 조회
        BalanceGame game = balanceGameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        User user = userRepository.findById(TEMP_USER_ID)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. 답변 저장
        BalanceGameSelection selection = new BalanceGameSelection(game, user, selectedOption);
        selectionRepository.save(selection);
    }
}