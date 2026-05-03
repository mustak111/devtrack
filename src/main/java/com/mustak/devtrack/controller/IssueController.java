package com.mustak.devtrack.controller;

import com.mustak.devtrack.dto.request.AssignRequest;
import com.mustak.devtrack.dto.request.IssueCreateRequest;
import com.mustak.devtrack.dto.request.IssueUpdateRequest;
import com.mustak.devtrack.dto.request.StatusUpdateRequest;
import com.mustak.devtrack.dto.response.IssueResponse;
import com.mustak.devtrack.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    // Create issue
    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Valid @RequestBody IssueCreateRequest request) {
        IssueResponse response = issueService.createIssue(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all issues (paginated)
    @GetMapping
    public ResponseEntity<Page<IssueResponse>> getAllIssues(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(issueService.getAllIssues(pageable));
    }

    // Get single issue
    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    // Update issue (full update)
    @PutMapping("/{id}")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody IssueUpdateRequest request) {
        return ResponseEntity.ok(issueService.updateIssue(id, request));
    }

    // Change status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(issueService.changeStatus(id, request.getStatus()));
    }

    // Assign to user
    @PatchMapping("/{id}/assign")
    public ResponseEntity<IssueResponse> assignIssue(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request) {
        return ResponseEntity.ok(issueService.assignIssue(id, request.getAssigneeId()));
    }

    // Delete issue (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
}