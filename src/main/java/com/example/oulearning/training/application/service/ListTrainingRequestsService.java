package com.example.oulearning.training.application.service;

import com.example.oulearning.training.application.port.in.command.ListTrainingRequestsQuery;
import com.example.oulearning.training.application.port.in.model.PaginatedTrainingRequestsResult;
import com.example.oulearning.training.application.port.in.usecase.ListTrainingRequestsUseCase;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.repository.TrainingFilterCriteria;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import org.springframework.stereotype.Service;

@Service
public class ListTrainingRequestsService implements ListTrainingRequestsUseCase {

    private final TrainingRepository trainingRepository;

    public ListTrainingRequestsService(final TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public PaginatedTrainingRequestsResult execute(final ListTrainingRequestsQuery query) {
        final var name = query.name() != null && !query.name().isBlank() ? new TrainingName(query.name()) : null;
        final var cost = query.costAmount() != null ? Cost.of(query.costAmount(), "EUR") : null;
        final var hours = query.hours() != null ? new Hours(query.hours()) : null;

        final var criteria = new TrainingFilterCriteria(
                name,
                cost,
                query.organizationalUnitId(),
                query.purposeType(),
                query.typeId(),
                hours,
                query.status());

        final int page = query.page() != null && query.page() >= 0 ? query.page() : 0;
        final int size = query.size() != null && query.size() > 0 ? query.size() : 20;
        final int offset = page * size;

        final var items = trainingRepository.findAll(criteria, offset, size);
        final long totalElements = trainingRepository.count(criteria);
        final int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PaginatedTrainingRequestsResult(items, totalElements, totalPages, page, size);
    }
}
