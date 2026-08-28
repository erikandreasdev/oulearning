package com.example.oulearning.training.infrastructure.persistence;

import com.example.oulearning.training.domain.model.IdGenerator;
import org.springframework.stereotype.Component;

@Component
class MyBatisTrainingIdGenerator implements IdGenerator {

    private final TrainingSequenceMapper sequenceMapper;

    MyBatisTrainingIdGenerator(final TrainingSequenceMapper sequenceMapper) {
        this.sequenceMapper = sequenceMapper;
    }

    @Override
    public long generate() {
        return sequenceMapper.nextId();
    }
}
