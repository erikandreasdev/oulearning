package com.example.oulearning.budgeting.infrastructure.persistence;

import com.example.oulearning.budgeting.domain.model.IdGenerator;
import org.springframework.stereotype.Component;

@Component
class MyBatisBudgetIdGenerator implements IdGenerator {

    private final BudgetSequenceMapper sequenceMapper;

    MyBatisBudgetIdGenerator(final BudgetSequenceMapper sequenceMapper) {
        this.sequenceMapper = sequenceMapper;
    }

    @Override
    public long generate() {
        return sequenceMapper.nextId();
    }
}
