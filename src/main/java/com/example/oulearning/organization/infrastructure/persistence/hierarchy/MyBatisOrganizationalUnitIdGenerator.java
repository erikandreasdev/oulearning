package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.model.IdGenerator;
import org.springframework.stereotype.Component;

@Component
class MyBatisOrganizationalUnitIdGenerator implements IdGenerator {

    private final OrganizationalUnitSequenceMapper sequenceMapper;

    MyBatisOrganizationalUnitIdGenerator(final OrganizationalUnitSequenceMapper sequenceMapper) {
        this.sequenceMapper = sequenceMapper;
    }

    @Override
    public long generate() {
        return sequenceMapper.nextId();
    }
}
