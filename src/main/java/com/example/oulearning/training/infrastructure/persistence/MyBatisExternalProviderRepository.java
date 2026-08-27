package com.example.oulearning.training.infrastructure.persistence;

import com.example.oulearning.training.domain.model.ExternalProvider;
import com.example.oulearning.training.domain.model.ExternalProviderContact;
import com.example.oulearning.training.domain.model.ExternalProviderId;
import com.example.oulearning.training.domain.model.ExternalProviderName;
import com.example.oulearning.training.domain.repository.ExternalProviderRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class MyBatisExternalProviderRepository implements ExternalProviderRepository {

    private final ExternalProviderMapper externalProviderMapper;

    MyBatisExternalProviderRepository(final ExternalProviderMapper externalProviderMapper) {
        this.externalProviderMapper = externalProviderMapper;
    }

    @Override
    public Optional<ExternalProvider> findById(final ExternalProviderId id) {
        return externalProviderMapper.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void save(final ExternalProvider externalProvider) {
        final var entity = toEntity(externalProvider);
        if (externalProvider.id() == null) {
            externalProviderMapper.insert(entity);
        } else {
            externalProviderMapper.update(entity);
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private ExternalProvider toDomain(final ExternalProviderEntity entity) {
        return ExternalProvider.reconstitute(
                new ExternalProviderId(entity.getId()),
                new ExternalProviderName(entity.getName()),
                new ExternalProviderContact(new com.example.oulearning.organization.domain.employee.model.Email(entity.getEmail()), new com.example.oulearning.training.domain.model.Phone(entity.getPhone())),
                entity.getActive());
    }

    private ExternalProviderEntity toEntity(final ExternalProvider provider) {
        final var entity = new ExternalProviderEntity();
        if (provider.id() != null) {
            entity.setId(provider.id().value());
        }
        entity.setName(provider.name().value());
        entity.setEmail(provider.contact().email().value());
        entity.setPhone(provider.contact().phone().value());
        entity.setActive(provider.active());
        return entity;
    }
}
