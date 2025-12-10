// com.example.dadambackend.domain.quiz.dto.SlangQuizTodayResponse

package com.example.dadambackend.domain.quiz.dto;

import com.example.dadambackend.domain.quiz.model.SlangQuiz;
import com.example.dadambackend.domain.quiz.model.SlangQuizVote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SlangQuizTodayResponse {

    private Long id;
    private String question;
    private List<String> choices;
    private String answer;
    private String explanation;
    private Integer answerIndex;    // 정답 보기 인덱스
    private Integer myChoiceIndex;  // 현재 로그인 유저가 고른 인덱스 (없으면 null)

    private List<VoterSummary> votes0;
    private List<VoterSummary> votes1;
    private List<VoterSummary> votes2;

    @Getter
    @AllArgsConstructor
    public static class VoterSummary {
        private Long userId;
        private String userName;
    }

    public static SlangQuizTodayResponse of(SlangQuiz quiz,
                                            List<SlangQuizVote> votes) {
        return of(quiz, votes, null);
    }

    public static SlangQuizTodayResponse of(SlangQuiz quiz,
                                            List<SlangQuizVote> votes,
                                            Long currentUserId) {

        String[] choiceArr = quiz.getChoiceArray();
        List<String> choices = Arrays.asList(choiceArr);

        // 정답 인덱스 계산
        Integer answerIndex = null;
        if (quiz.getAnswer() != null) {
            for (int i = 0; i < choiceArr.length; i++) {
                if (quiz.getAnswer().trim().equals(choiceArr[i].trim())) {
                    answerIndex = i;
                    break;
                }
            }
        }

        List<VoterSummary> votes0 = new ArrayList<>();
        List<VoterSummary> votes1 = new ArrayList<>();
        List<VoterSummary> votes2 = new ArrayList<>();
        Integer myChoiceIndex = null;

        for (SlangQuizVote v : votes) {
            VoterSummary summary = new VoterSummary(
                    v.getUser().getId(),
                    v.getUser().getName()
            );

            int idx = v.getChoiceIndex();
            if (idx == 0) votes0.add(summary);
            else if (idx == 1) votes1.add(summary);
            else if (idx == 2) votes2.add(summary);

            if (currentUserId != null &&
                    currentUserId.equals(v.getUser().getId())) {
                myChoiceIndex = idx;
            }
        }

        return SlangQuizTodayResponse.builder()
                .id(quiz.getId())
                .question(quiz.getQuestion())
                .choices(choices)
                .answer(quiz.getAnswer())
                .explanation(quiz.getExplanation())
                .answerIndex(answerIndex)
                .myChoiceIndex(myChoiceIndex)
                .votes0(votes0)
                .votes1(votes1)
                .votes2(votes2)
                .build();
    }
}
