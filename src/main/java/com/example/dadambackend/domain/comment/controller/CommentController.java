package com.example.dadambackend.domain.comment.controller;

import com.example.dadambackend.domain.comment.dto.request.CommentRequest;
import com.example.dadambackend.domain.comment.dto.response.CommentResponse;
import com.example.dadambackend.domain.comment.service.CommentService;
import com.example.dadambackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/answers/{answerId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * GET /api/v1/answers/{answerId}/comments
     * 특정 답변에 달린 모든 댓글 조회
     */
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long answerId) {
        List<CommentResponse> response = commentService.getCommentsByAnswer(answerId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/answers/{answerId}/comments
     * 특정 답변에 댓글 작성
     */
    @PostMapping
    public ResponseEntity<Void> createComment(
            @PathVariable Long answerId,
            @RequestBody CommentRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserId(authHeader);

        commentService.createComment(answerId, userId, request);

        // 201 Created 응답
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * PUT /api/v1/answers/{answerId}/comments/{commentId}
     * 특정 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long answerId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserId(authHeader);

        commentService.updateComment(answerId, commentId, userId, request);
        // 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/v1/answers/{answerId}/comments/{commentId}
     * 특정 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long answerId,
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserId(authHeader);

        commentService.deleteComment(answerId, commentId, userId);
        // 204 No Content
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("잘못된 Authorization 헤더 형식입니다.");
        }
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
