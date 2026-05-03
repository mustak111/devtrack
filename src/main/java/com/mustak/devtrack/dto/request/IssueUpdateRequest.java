package com.mustak.devtrack.dto.request;

import com.mustak.devtrack.enums.Priority;
import com.mustak.devtrack.enums.Status;
import lombok.Data;

@Data
public class IssueUpdateRequest {
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private Long assigneeId;
}