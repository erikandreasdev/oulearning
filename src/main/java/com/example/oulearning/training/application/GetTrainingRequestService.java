package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingRequest;
import com.example.oulearning.training.domain.TrainingRequestId;
import com.example.oulearning.training.domain.repository.TrainingRequestRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating single training request queries.
 */
@Service
@Transactional(readOnly = true)
public class GetTrainingRequestService implements GetTrainingRequestUseCase {

    private final TrainingRequestRepository repository;

    public GetTrainingRequestService(TrainingRequestRepository repository) {
        this.repository = Objects.requireNonNull(repository, "TrainingRequestRepository cannot be null");
    }

    @Override
    public Optional<TrainingRequest> execute(GetTrainingRequestQuery query) {
        Objects.requireNonNull(query, "GetTrainingRequestQuery cannot be null");
        return repository.findById(TrainingRequestId.of(query.id()));
    }
}
