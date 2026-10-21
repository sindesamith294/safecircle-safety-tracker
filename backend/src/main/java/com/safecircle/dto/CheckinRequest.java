package com.safecircle.dto;

import com.safecircle.model.SafetyCheckin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class CheckinRequest {

    @NotBlank(message = "Destination label is required")
    private String destinationLabel;

    @NotNull(message = "Due in minutes is required")
    private Integer dueInMinutes;

    public CheckinRequest() {
    }

    public CheckinRequest(String destinationLabel, Integer dueInMinutes) {
        this.destinationLabel = destinationLabel;
        this.dueInMinutes = dueInMinutes;
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }

    public void setDestinationLabel(String destinationLabel) {
        this.destinationLabel = destinationLabel;
    }

    public Integer getDueInMinutes() {
        return dueInMinutes;
    }

    public void setDueInMinutes(Integer dueInMinutes) {
        this.dueInMinutes = dueInMinutes;
    }
}
