package com.example.oulearning.training.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST Request DTO for rejecting a training request.
 */
@Schema(description = "Payload for rejecting a training request by an authorized manager")
public record RejectTrainingRequest(
        @NotBlank(message = "Manager corporate key cannot be blank")
        @Schema(description = "Corporate key of the reviewing manager", example = "CK0001")
        String managerCorporateKey,

        @NotBlank(message = "Rejection reason cannot be blank")
        @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
        @Schema(description = "Mandatory explanation for rejecting the training request", example = "Budget allocation exceeded for current fiscal quarter")
        String rejectionReason,

        @Size(max = 1000, message = "Manager notes cannot exceed 1000 characters")
        @Schema(description = "Optional additional manager remarks", example = "Please resubmit next quarter")
        String managerNotes) {}
