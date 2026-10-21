package com.safecircle.dto;

import com.safecircle.model.Circle;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class CircleCreateRequest {

    @NotBlank(message = "Circle name is required")
    private String name;

    public CircleCreateRequest() {
    }

    public CircleCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
