package com.example.oulearning.budgeting.infrastructure.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
interface BudgetSequenceMapper {

    @Select("SELECT budget_seq.NEXTVAL FROM DUAL")
    Long nextId();
}
