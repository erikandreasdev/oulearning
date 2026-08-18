package com.example.oulearning.training.infrastructure.persistence;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.TrainingRequest;
import com.example.oulearning.training.domain.request.vo.identity.TrainingRequestId;
import com.example.oulearning.training.domain.request.repository.TrainingRequestRepository;
import com.example.oulearning.training.domain.request.repository.TrainingRequestSearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence adapter implementing {@link TrainingRequestRepository} using MyBatis.
 */
@Repository
public class TrainingRequestPersistenceAdapter implements TrainingRequestRepository {

    private final TrainingRequestMyBatisMapper mapper;
    private final TrainingRequestEntityMapper entityMapper;

    public TrainingRequestPersistenceAdapter(
            TrainingRequestMyBatisMapper mapper,
            TrainingRequestEntityMapper entityMapper) {
        this.mapper = Objects.requireNonNull(mapper, "TrainingRequestMyBatisMapper cannot be null");
        this.entityMapper = Objects.requireNonNull(entityMapper, "TrainingRequestEntityMapper cannot be null");
    }

    @Override
    @Transactional
    public void save(TrainingRequest trainingRequest) {
        Objects.requireNonNull(trainingRequest, "TrainingRequest cannot be null");

        final var idStr = trainingRequest.id().toString();
        final var existing = mapper.findTrainingRequestById(idStr);

        final var entity = entityMapper.toEntity(trainingRequest, existing != null ? existing.version() : 0L);

        if (existing == null) {
            mapper.insertTrainingRequest(entity);
        } else {
            final var updatedRows = mapper.updateTrainingRequest(entity);
            if (updatedRows == 0) {
                throw new OptimisticLockingFailureException(
                        "Failed to update training request '%s': version conflict or record deleted".formatted(idStr));
            }
            mapper.deleteAssistantsByRequestId(idStr);
        }

        for (final var assistant : trainingRequest.assistants()) {
            mapper.insertAssistant(idStr, assistant.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingRequest> findById(TrainingRequestId id) {
        Objects.requireNonNull(id, "TrainingRequestId cannot be null");

        final var entity = mapper.findTrainingRequestById(id.toString());
        if (entity == null) {
            return Optional.empty();
        }

        final var assistants = mapper.findAssistantsByRequestId(id.toString());
        return Optional.of(entityMapper.toDomain(entity, assistants));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingRequest> findByOuId(OuId ouId) {
        Objects.requireNonNull(ouId, "OuId cannot be null");

        final var entities = mapper.findTrainingRequestsByOuId(ouId.toString());
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<TrainingRequest>(entities.size());
        for (final var entity : entities) {
            final var assistants = mapper.findAssistantsByRequestId(entity.id());
            result.add(entityMapper.toDomain(entity, assistants));
        }

        return List.copyOf(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingRequest> findByOuIdAndFiscalYear(OuId ouId, FiscalYear fiscalYear) {
        Objects.requireNonNull(ouId, "OuId cannot be null");
        Objects.requireNonNull(fiscalYear, "FiscalYear cannot be null");

        final var entities = mapper.findTrainingRequestsByOuIdAndFiscalYear(ouId.toString(), fiscalYear.value());
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<TrainingRequest>(entities.size());
        for (final var entity : entities) {
            final var assistants = mapper.findAssistantsByRequestId(entity.id());
            result.add(entityMapper.toDomain(entity, assistants));
        }

        return List.copyOf(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingRequest> findByFiscalYear(FiscalYear fiscalYear) {
        Objects.requireNonNull(fiscalYear, "FiscalYear cannot be null");

        final var entities = mapper.findTrainingRequestsByFiscalYear(fiscalYear.value());
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<TrainingRequest>(entities.size());
        for (final var entity : entities) {
            final var assistants = mapper.findAssistantsByRequestId(entity.id());
            result.add(entityMapper.toDomain(entity, assistants));
        }

        return List.copyOf(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingRequest> findByCriteria(TrainingRequestSearchCriteria criteria) {
        Objects.requireNonNull(criteria, "TrainingRequestSearchCriteria cannot be null");

        final var ouIdStrings = criteria.ouIds().stream()
                .map(OuId::toString)
                .toList();
        final var statusStr = criteria.status() != null ? criteria.status().name() : null;
        final var fiscalYearVal = criteria.fiscalYear() != null ? criteria.fiscalYear().value() : null;

        final var entities = mapper.findByCriteria(
                ouIdStrings.isEmpty() ? null : ouIdStrings,
                statusStr,
                fiscalYearVal);

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<TrainingRequest>(entities.size());
        for (final var entity : entities) {
            final var assistants = mapper.findAssistantsByRequestId(entity.id());
            result.add(entityMapper.toDomain(entity, assistants));
        }

        return List.copyOf(result);
    }
}
