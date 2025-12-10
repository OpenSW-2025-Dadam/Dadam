package com.example.dadambackend.domain.quiz.repository;

import com.example.dadambackend.domain.quiz.model.SlangQuiz;
import com.example.dadambackend.domain.quiz.model.SlangQuizVote;
import com.example.dadambackend.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SlangQuizVoteRepository extends JpaRepository<SlangQuizVote, Long> {

    /**
     * 단순히 quiz 기준으로 투표 목록 조회 (필요 시 사용)
     */
    List<SlangQuizVote> findByQuiz(SlangQuiz quiz);

    /**
     * quiz + user 기준으로 단일 투표 조회
     */
    Optional<SlangQuizVote> findByQuizAndUser(SlangQuiz quiz, User user);

    /**
     * N+1 방지를 위해 user 를 fetch join 해서 한 번에 조회
     */
    @Query("select v from SlangQuizVote v join fetch v.user where v.quiz = :quiz")
    List<SlangQuizVote> findByQuizWithUser(@Param("quiz") SlangQuiz quiz);
}
