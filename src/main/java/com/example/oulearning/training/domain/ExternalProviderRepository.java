package com.example.oulearning.training.domain;

import java.util.Optional;

public interface ExternalProviderRepository {
    Optional<ExternalProvider> findById(ExternalProviderId id);

    void save(ExternalProvider externalProvider);
}
