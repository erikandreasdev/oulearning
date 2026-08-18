package com.example.oulearning.training.application.service;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.TrainingRequest;
import com.example.oulearning.training.domain.request.repository.TrainingRequestRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.training.application.port.in.query.GetTrainingRequestsByOuQuery;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingRequestsByOuUseCase;

/**
 * Service orchestrating retrieval of training requests by OU and optional Fiscal Year.
 */
@Service
@Transactional(readOnly = true)
public class GetTrainingRequestsByOuService implements GetTrainingRequestsByOuUseCase {

    private final TrainingRequestRepository repository;

    public GetTrainingRequestsByOuService(TrainingRequestRepository repository) {
        this.repository = Objects.requireNonNull(repository, "TrainingRequestRepository cannot be null");
    }

    @Override
    public List<TrainingRequest> execute(GetTrainingRequestsByOuQuery query) {
        Objects.requireNonNull(query, "GetTrainingRequestsByOuQuery cannot be null");

        final var ouId = OuId.of(query.ouId());
        if (query.fiscalYear() != null) {
            return repository.findByOuIdAndFiscalYear(ouId, FiscalYear.of(query.fiscalYear()));
        }
        return repository.findByOuId(ouId);
    }
}
