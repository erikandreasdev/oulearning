package com.example.oulearning.training.domain.request;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.request.exception.IllegalTrainingRequestStateException;
import com.example.oulearning.training.domain.request.exception.InvalidRejectionReasonException;
import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;
import com.example.oulearning.training.domain.request.exception.UnauthorizedManagerException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import com.example.oulearning.training.domain.request.vo.identity.CorporateKey;
import com.example.oulearning.training.domain.request.vo.decision.ManagerNotes;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.vo.decision.RejectionReason;
import com.example.oulearning.training.domain.request.vo.details.TrainingCost;
import com.example.oulearning.training.domain.request.vo.details.TrainingHours;
import com.example.oulearning.training.domain.request.vo.details.TrainingName;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurpose;
import com.example.oulearning.training.domain.request.vo.identity.TrainingRequestId;
import com.example.oulearning.training.domain.request.vo.decision.TrainingRequestStatus;

/**
 * Aggregate Root representing an OU Owner's request for an employee training program.
 * Captures training parameters, purpose, assigned assistants, target fiscal year,
 * and tracks the Manager review lifecycle (DRAFT -> APPROVED / REJECTED).
 */
public record TrainingRequest(
        TrainingRequestId id,
        OuId ouId,
        CorporateKey requester,
        TrainingName name,
        TrainingCost cost,
        TrainingPurpose purpose,
        TrainingHours hours,
        boolean availableAtOrgUniversity,
        Set<CorporateKey> assistants,
        FiscalYear fiscalYear,
        TrainingRequestStatus status,
        CorporateKey reviewedBy,
        RejectionReason rejectionReason,
        ManagerNotes managerNotes,
        Instant reviewedAt,
        Instant createdAt) {

    public TrainingRequest {
        if (id == null) {
            throw new InvalidTrainingRequestException("TrainingRequestId cannot be null");
        }
        if (ouId == null) {
            throw new InvalidTrainingRequestException("OuId cannot be null");
        }
        if (requester == null) {
            throw new InvalidTrainingRequestException("Requester CorporateKey cannot be null");
        }
        if (name == null) {
            throw new InvalidTrainingRequestException("TrainingName cannot be null");
        }
        if (cost == null) {
            throw new InvalidTrainingRequestException("TrainingCost cannot be null");
        }
        if (purpose == null) {
            throw new InvalidTrainingRequestException("TrainingPurpose cannot be null");
        }
        if (hours == null) {
            throw new InvalidTrainingRequestException("TrainingHours cannot be null");
        }
        if (assistants == null || assistants.isEmpty()) {
            throw new InvalidTrainingRequestException("Assistants list cannot be null or empty");
        }
        if (fiscalYear == null) {
            throw new InvalidTrainingRequestException("FiscalYear cannot be null");
        }
        if (status == null) {
            throw new InvalidTrainingRequestException("TrainingRequestStatus cannot be null");
        }
        if (createdAt == null) {
            throw new InvalidTrainingRequestException("CreatedAt instant cannot be null");
        }

        if (status == TrainingRequestStatus.REJECTED && rejectionReason == null) {
            throw new InvalidRejectionReasonException("Rejection reason is required when request status is REJECTED");
        }

        assistants = Set.copyOf(assistants);
    }

    public static TrainingRequest create(
            TrainingRequestId id,
            OuId ouId,
            CorporateKey requester,
            TrainingName name,
            TrainingCost cost,
            TrainingPurpose purpose,
            TrainingHours hours,
            boolean availableAtOrgUniversity,
            Set<CorporateKey> assistants,
            FiscalYear fiscalYear,
            Instant createdAt) {
        return new TrainingRequest(
                id,
                ouId,
                requester,
                name,
                cost,
                purpose,
                hours,
                availableAtOrgUniversity,
                assistants,
                fiscalYear,
                TrainingRequestStatus.DRAFT,
                null,
                null,
                null,
                null,
                createdAt);
    }

    public TrainingRequest approve(CorporateKey managerKey, ManagerNotes notes, Instant approvedAt) {
        if (this.status != TrainingRequestStatus.DRAFT) {
            throw new IllegalTrainingRequestStateException(
                    "Cannot approve training request '%s': current status is '%s', but must be DRAFT"
                            .formatted(id.value(), status));
        }
        if (managerKey == null) {
            throw new UnauthorizedManagerException("Manager CorporateKey cannot be null when approving a request");
        }

        return new TrainingRequest(
                id,
                ouId,
                requester,
                name,
                cost,
                purpose,
                hours,
                availableAtOrgUniversity,
                assistants,
                fiscalYear,
                TrainingRequestStatus.APPROVED,
                managerKey,
                null,
                notes,
                approvedAt != null ? approvedAt : Instant.now(),
                createdAt);
    }

    public TrainingRequest reject(
            CorporateKey managerKey,
            RejectionReason reason,
            ManagerNotes notes,
            Instant rejectedAt) {
        if (this.status != TrainingRequestStatus.DRAFT) {
            throw new IllegalTrainingRequestStateException(
                    "Cannot reject training request '%s': current status is '%s', but must be DRAFT"
                            .formatted(id.value(), status));
        }
        if (managerKey == null) {
            throw new UnauthorizedManagerException("Manager CorporateKey cannot be null when rejecting a request");
        }
        if (reason == null) {
            throw new InvalidRejectionReasonException("Rejection reason cannot be null when rejecting a request");
        }

        return new TrainingRequest(
                id,
                ouId,
                requester,
                name,
                cost,
                purpose,
                hours,
                availableAtOrgUniversity,
                assistants,
                fiscalYear,
                TrainingRequestStatus.REJECTED,
                managerKey,
                reason,
                notes,
                rejectedAt != null ? rejectedAt : Instant.now(),
                createdAt);
    }

    public Optional<CorporateKey> optionalReviewedBy() {
        return Optional.ofNullable(reviewedBy);
    }

    public Optional<RejectionReason> optionalRejectionReason() {
        return Optional.ofNullable(rejectionReason);
    }

    public Optional<ManagerNotes> optionalManagerNotes() {
        return Optional.ofNullable(managerNotes);
    }

    public Optional<Instant> optionalReviewedAt() {
        return Optional.ofNullable(reviewedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainingRequest that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
