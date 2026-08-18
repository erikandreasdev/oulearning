package com.example.oulearning.training.application.port.in.command;

import java.util.UUID;

/**
 * Command to reject a training request by a manager with a mandatory rejection reason.
 */
public record RejectTrainingRequestCommand(
        UUID requestId,
        String managerCorporateKey,
        String rejectionReason,
        String managerNotes) {}
