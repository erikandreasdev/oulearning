package com.example.oulearning.training.application;

import java.util.UUID;

/**
 * Command to approve a training request by a manager.
 */
public record ApproveTrainingRequestCommand(
        UUID requestId,
        String managerCorporateKey,
        String managerNotes) {}
