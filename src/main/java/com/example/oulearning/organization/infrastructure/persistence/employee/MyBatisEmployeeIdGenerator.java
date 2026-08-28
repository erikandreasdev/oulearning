package com.example.oulearning.organization.infrastructure.persistence.employee;

import com.example.oulearning.organization.domain.employee.model.IdGenerator;
import org.springframework.stereotype.Component;

@Component
class MyBatisEmployeeIdGenerator implements IdGenerator {

    private final EmployeeSequenceMapper sequenceMapper;

    MyBatisEmployeeIdGenerator(final EmployeeSequenceMapper sequenceMapper) {
        this.sequenceMapper = sequenceMapper;
    }

    @Override
    public long generate() {
        return sequenceMapper.nextId();
    }
}
