package com.example.dadambackend.domain.comment.service;

import com.example.dadambackend.domain.answer.model.Answer;
import com.example.dadambackend.domain.answer.repository.AnswerRepository; // Answer Repository 참조
import com.example.dadambackend.domain.comment.dto.request.CommentRequest;
import com.example.dadambackend.domain.comment.dto.response.CommentResponse;
import com.example.dadambackend.domain.comment.model.Comment;
import com.example.dadambackend.domain.comment.repository.CommentRepository;
import com.example.dadambackend.domain.user.model.User;
import com.example.dadambackend.domain.user.repository.UserRepository;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    public static final int MAX_COMMENTS_PER_USER = 10; // 최대 댓글 개수 제한

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository; // 답변 엔티티 조회를 위해 필요
    private final UserRepository userRepository;

    /**
     * 특정 답변에 달린 모든 댓글 조회
     */
    public List<CommentResponse> getCommentsByAnswer(Long answerId) {
        // Answer 존재 여부 확인
        if (!answerRepository.existsById(answerId)) {
            throw new BusinessException(ErrorCode.GAME_NOT_FOUND, "해당 답변을 찾을 수 없습니다.");
        }

        List<Comment> comments = commentRepository.findByAnswerIdOrderByCreatedAtAsc(answerId);

        return comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 작성 로직
     */
    @Transactional
    public void createComment(Long answerId, Long userId, CommentRequest request) {

        // 1. 1인당 댓글 개수 제한 검사
        long userCommentCount = commentRepository.countByUserId(userId);
        if (userCommentCount >= MAX_COMMENTS_PER_USER) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "1인당 최대 " + MAX_COMMENTS_PER_USER + "개의 댓글만 작성할 수 있습니다."
            );
        }

        // 2. 답변, 사용자 객체 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND, "해당 답변을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)); // User 엔티티는 재사용

        // 3. 댓글 생성 및 저장
        Comment comment = new Comment(answer, user, request.getContent());

        // Comment 생성자에서 글자 수 제한 유효성 검사를 수행합니다.
        commentRepository.save(comment);
    }

    /**
     * 댓글 수정 로직
     */
    @Transactional
    public void updateComment(Long answerId, Long commentId, Long userId, CommentRequest request) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_REQUEST,
                        "해당 댓글을 찾을 수 없습니다."
                ));

        // 2. 해당 답변에 속한 댓글인지 검증
        if (!comment.getAnswer().getId().equals(answerId)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "해당 답변에 속한 댓글이 아닙니다."
            );
        }

        // 3. 작성자 검증 (본인 댓글만 수정 가능)
        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "본인이 작성한 댓글만 수정할 수 있습니다."
            );
        }

        // 4. 내용 수정
        comment.updateContent(request.getContent());
        // 변경 감지는 @Transactional + 영속 엔티티 수정으로 자동 반영
    }

    /**
     * 댓글 삭제 로직
     */
    @Transactional
    public void deleteComment(Long answerId, Long commentId, Long userId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_REQUEST,
                        "해당 댓글을 찾을 수 없습니다."
                ));

        // 2. 해당 답변에 속한 댓글인지 검증
        if (!comment.getAnswer().getId().equals(answerId)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "해당 답변에 속한 댓글이 아닙니다."
            );
        }

        // 3. 작성자 검증 (본인 댓글만 삭제 가능)
        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "본인이 작성한 댓글만 삭제할 수 있습니다."
            );
        }

        // 4. 삭제
        commentRepository.delete(comment);
    }
}
