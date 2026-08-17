package com.example.oulearning.training.infrastructure.persistence;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.CorporateKey;
import com.example.oulearning.training.domain.ManagerNotes;
import com.example.oulearning.training.domain.OuId;
import com.example.oulearning.training.domain.RejectionReason;
import com.example.oulearning.training.domain.TrainingCost;
import com.example.oulearning.training.domain.TrainingHours;
import com.example.oulearning.training.domain.TrainingName;
import com.example.oulearning.training.domain.TrainingPurpose;
import com.example.oulearning.training.domain.TrainingPurposeType;
import com.example.oulearning.training.domain.TrainingRequest;
import com.example.oulearning.training.domain.TrainingRequestId;
import com.example.oulearning.training.domain.TrainingRequestStatus;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Dedicated mapper for bidirectional mapping between {@link TrainingRequest} domain aggregates
 * and {@link TrainingRequestEntity} persistence records.
 */
@Component
public class TrainingRequestEntityMapper {

    public TrainingRequestEntity toEntity(TrainingRequest domain, Long version) {
        Objects.requireNonNull(domain, "TrainingRequest domain model cannot be null");

        return new TrainingRequestEntity(
                domain.id().toString(),
                domain.ouId().toString(),
                domain.requester().value(),
                domain.name().value(),
                domain.cost().amount(),
                domain.cost().currency(),
                domain.purpose().type().name(),
                domain.purpose().customText(),
                domain.hours().value(),
                domain.availableAtOrgUniversity() ? 1 : 0,
                domain.fiscalYear().value(),
                domain.status().name(),
                domain.reviewedBy() != null ? domain.reviewedBy().value() : null,
                domain.rejectionReason() != null ? domain.rejectionReason().value() : null,
                domain.managerNotes() != null ? domain.managerNotes().value() : null,
                domain.reviewedAt(),
                domain.createdAt(),
                version != null ? version : 0L);
    }

    public TrainingRequest toDomain(TrainingRequestEntity entity, Set<String> assistantKeys) {
        Objects.requireNonNull(entity, "TrainingRequestEntity cannot be null");

        final var id = TrainingRequestId.of(entity.id());
        final var ouId = OuId.of(entity.ouId());
        final var requester = CorporateKey.of(entity.requesterCorporateKey());
        final var name = TrainingName.of(entity.name());
        final var cost = TrainingCost.of(entity.costAmount(), entity.costCurrency());
        final var purposeType = TrainingPurposeType.valueOf(entity.purposeType());
        final var purpose = purposeType == TrainingPurposeType.OTHER
                ? TrainingPurpose.other(entity.purposeCustomText())
                : TrainingPurpose.of(purposeType);
        final var hours = TrainingHours.of(entity.trainingHours());
        final var availableAtOrgUniversity = entity.availableAtOrgUniversity() != null && entity.availableAtOrgUniversity() == 1;
        final var fiscalYear = FiscalYear.of(entity.fiscalYear());
        final var status = TrainingRequestStatus.valueOf(entity.status());
        final var reviewedBy = entity.reviewedBy() != null ? CorporateKey.of(entity.reviewedBy()) : null;
        final var rejectionReason = entity.rejectionReason() != null ? RejectionReason.of(entity.rejectionReason()) : null;
        final var managerNotes = entity.managerNotes() != null ? ManagerNotes.of(entity.managerNotes()) : null;
        final var reviewedAt = entity.reviewedAt();
        final var createdAt = entity.createdAt();

        final var safeAssistants = (assistantKeys != null && !assistantKeys.isEmpty())
                ? assistantKeys.stream().map(CorporateKey::of).collect(Collectors.toSet())
                : Set.<CorporateKey>of();

        return new TrainingRequest(
                id,
                ouId,
                requester,
                name,
                cost,
                purpose,
                hours,
                availableAtOrgUniversity,
                safeAssistants,
                fiscalYear,
                status,
                reviewedBy,
                rejectionReason,
                managerNotes,
                reviewedAt,
                createdAt);
    }
}
