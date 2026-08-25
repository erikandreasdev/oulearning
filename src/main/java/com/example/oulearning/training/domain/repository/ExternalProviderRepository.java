package com.example.oulearning.training.domain.repository;

import com.example.oulearning.training.domain.model.*;

import java.util.Optional;

public interface ExternalProviderRepository {
    Optional<ExternalProvider> findById(ExternalProviderId id);

    void save(ExternalProvider externalProvider);
}
