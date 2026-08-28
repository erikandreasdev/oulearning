package com.example.oulearning.organization.infrastructure.persistence.employee;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
interface EmployeeSequenceMapper {

    @Select("SELECT employee_seq.NEXTVAL FROM DUAL")
    Long nextId();
}
