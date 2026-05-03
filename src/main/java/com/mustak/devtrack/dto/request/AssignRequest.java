package com.mustak.devtrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRequest {

    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;
}