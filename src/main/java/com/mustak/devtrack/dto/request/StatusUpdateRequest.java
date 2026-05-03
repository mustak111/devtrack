package com.mustak.devtrack.dto.request;

import com.mustak.devtrack.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Status status;
}