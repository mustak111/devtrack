package com.mustak.devtrack.controller;

import com.mustak.devtrack.dto.request.CommentRequest;
import com.mustak.devtrack.dto.response.CommentResponse;
import com.mustak.devtrack.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Add comment to issue
    @PostMapping("/api/issues/{issueId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long issueId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addComment(issueId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all comments for an issue
    @GetMapping("/api/issues/{issueId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long issueId) {
        return ResponseEntity.ok(commentService.getCommentsByIssue(issueId));
    }

    // Delete comment
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}