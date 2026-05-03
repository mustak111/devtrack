package com.mustak.devtrack.dto.response;

import com.mustak.devtrack.enums.Priority;
import com.mustak.devtrack.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponse {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private UserResponse creator;
    private UserResponse assignee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}