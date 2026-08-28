package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
interface OrganizationalUnitSequenceMapper {

    @Select("SELECT organizational_unit_seq.NEXTVAL FROM DUAL")
    Long nextId();
}
