package com.github.Clementol.lovable_clone.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @Size(min = 4, message = "Should have a min length of 4 and max length of 10") @NotBlank() String name) {
}
