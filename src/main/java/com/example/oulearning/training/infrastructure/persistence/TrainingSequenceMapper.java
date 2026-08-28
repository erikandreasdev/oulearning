package com.example.oulearning.training.infrastructure.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
interface TrainingSequenceMapper {

    @Select("SELECT training_seq.NEXTVAL FROM DUAL")
    Long nextId();
}
