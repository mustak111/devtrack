package com.mustak.devtrack.service;

import com.mustak.devtrack.dto.request.IssueCreateRequest;
import com.mustak.devtrack.dto.request.IssueUpdateRequest;
import com.mustak.devtrack.dto.response.IssueResponse;
import com.mustak.devtrack.dto.response.UserResponse;
import com.mustak.devtrack.entity.Issue;
import com.mustak.devtrack.entity.User;
import com.mustak.devtrack.enums.Status;
import com.mustak.devtrack.exception.BadRequestException;
import com.mustak.devtrack.exception.ResourceNotFoundException;
import com.mustak.devtrack.repository.IssueRepository;
import com.mustak.devtrack.repository.UserRepository;
import com.mustak.devtrack.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    // CREATE Issue
    @Transactional
    public IssueResponse createIssue(IssueCreateRequest request) {
        User currentUser = securityUtil.getCurrentUser();

        Issue issue = Issue.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Status.TODO)  // Default status
                .creator(currentUser)
                .build();

        // If assignee provided, fetch and set
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assignee not found with id: " + request.getAssigneeId()));
            issue.setAssignee(assignee);
        }

        Issue saved = issueRepository.save(issue);
        return mapToResponse(saved);
    }

    // GET All Issues (with pagination)
    public Page<IssueResponse> getAllIssues(Pageable pageable) {
        return issueRepository.findAll(pageable).map(this::mapToResponse);
    }

    // GET Single Issue
    public IssueResponse getIssueById(Long id) {
        Issue issue = findIssueById(id);
        return mapToResponse(issue);
    }

    // UPDATE Issue (full update)
    @Transactional
    public IssueResponse updateIssue(Long id, IssueUpdateRequest request) {
        Issue issue = findIssueById(id);

        if (request.getTitle() != null) {
            issue.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            issue.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            issue.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            validateStatusTransition(issue.getStatus(), request.getStatus());
            issue.setStatus(request.getStatus());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + request.getAssigneeId()));
            issue.setAssignee(assignee);
        }

        Issue updated = issueRepository.save(issue);
        return mapToResponse(updated);
    }

    // CHANGE Status
    @Transactional
    public IssueResponse changeStatus(Long id, Status newStatus) {
        Issue issue = findIssueById(id);
        validateStatusTransition(issue.getStatus(), newStatus);
        issue.setStatus(newStatus);
        Issue updated = issueRepository.save(issue);
        return mapToResponse(updated);
    }

    // ASSIGN Issue to user
    @Transactional
    public IssueResponse assignIssue(Long id, Long assigneeId) {
        Issue issue = findIssueById(id);
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + assigneeId));
        issue.setAssignee(assignee);
        Issue updated = issueRepository.save(issue);
        return mapToResponse(updated);
    }

    // DELETE Issue
    @Transactional
    public void deleteIssue(Long id) {
        Issue issue = findIssueById(id);
        issueRepository.delete(issue);
    }

    // ============ Helper Methods ============

    private Issue findIssueById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue not found with id: " + id));
    }

    // Status workflow validation: TODO → IN_PROGRESS → DONE
    private void validateStatusTransition(Status current, Status next) {
        if (current == next) return;

        boolean isValid = switch (current) {
            case TODO -> next == Status.IN_PROGRESS;
            case IN_PROGRESS -> next == Status.DONE || next == Status.TODO;
            case DONE -> next == Status.IN_PROGRESS;  // Reopen allowed
        };

        if (!isValid) {
            throw new BadRequestException(
                    String.format("Invalid status transition: %s → %s", current, next)
            );
        }
    }

    // Convert Issue Entity → Response DTO
    private IssueResponse mapToResponse(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .creator(mapUserToResponse(issue.getCreator()))
                .assignee(issue.getAssignee() != null ? mapUserToResponse(issue.getAssignee()) : null)
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
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