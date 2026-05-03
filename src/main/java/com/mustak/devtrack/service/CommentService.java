package com.mustak.devtrack.service;

import com.mustak.devtrack.dto.request.CommentRequest;
import com.mustak.devtrack.dto.response.CommentResponse;
import com.mustak.devtrack.dto.response.UserResponse;
import com.mustak.devtrack.entity.Comment;
import com.mustak.devtrack.entity.Issue;
import com.mustak.devtrack.entity.User;
import com.mustak.devtrack.enums.Role;
import com.mustak.devtrack.exception.ResourceNotFoundException;
import com.mustak.devtrack.exception.UnauthorizedException;
import com.mustak.devtrack.repository.CommentRepository;
import com.mustak.devtrack.repository.IssueRepository;
import com.mustak.devtrack.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final SecurityUtil securityUtil;

    // Add comment to issue
    @Transactional
    public CommentResponse addComment(Long issueId, CommentRequest request) {
        User currentUser = securityUtil.getCurrentUser();

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue not found with id: " + issueId));

        Comment comment = Comment.builder()
                .issue(issue)
                .user(currentUser)
                .content(request.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    // Get all comments for an issue
    public List<CommentResponse> getCommentsByIssue(Long issueId) {
        // Validate that issue exists
        if (!issueRepository.existsById(issueId)) {
            throw new ResourceNotFoundException("Issue not found with id: " + issueId);
        }

        return commentRepository.findByIssueIdOrderByCreatedAtDesc(issueId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Delete comment - only owner or admin
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = securityUtil.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment not found with id: " + commentId));

        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(
                    "You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    // ============ Helper Methods ============

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(mapUserToResponse(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}