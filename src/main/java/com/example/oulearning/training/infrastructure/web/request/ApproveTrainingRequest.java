package com.example.oulearning.training.infrastructure.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST Request DTO for approving a training request.
 */
@Schema(description = "Payload for approving a training request by an authorized manager")
public record ApproveTrainingRequest(
        @NotBlank(message = "Manager corporate key cannot be blank")
        @Schema(description = "Corporate key of the reviewing manager", example = "CK0001")
        String managerCorporateKey,

        @Size(max = 1000, message = "Manager notes cannot exceed 1000 characters")
        @Schema(description = "Optional review notes or remarks", example = "Approved for Q3 skills enhancement")
        String managerNotes) {}
