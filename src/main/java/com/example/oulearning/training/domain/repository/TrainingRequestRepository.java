package com.example.oulearning.training.domain.repository;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.OuId;
import com.example.oulearning.training.domain.TrainingRequest;
import com.example.oulearning.training.domain.TrainingRequestId;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for persisting and retrieving {@link TrainingRequest} aggregates.
 */
public interface TrainingRequestRepository {

    void save(TrainingRequest trainingRequest);

    Optional<TrainingRequest> findById(TrainingRequestId id);

    List<TrainingRequest> findByOuId(OuId ouId);

    List<TrainingRequest> findByOuIdAndFiscalYear(OuId ouId, FiscalYear fiscalYear);

    List<TrainingRequest> findByFiscalYear(FiscalYear fiscalYear);

    List<TrainingRequest> findByCriteria(TrainingRequestSearchCriteria criteria);
}
