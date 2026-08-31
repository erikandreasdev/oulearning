package com.example.oulearning.organization.infrastructure.persistence.employee;

import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
interface EmployeeMapper {

    @Select("SELECT id, name, surname, email, active FROM employee WHERE id = #{id}")
    Optional<EmployeeEntity> findById(Long id);

    @Select("SELECT id, name, surname, email, active FROM employee WHERE email = #{email}")
    Optional<EmployeeEntity> findByEmail(String email);

    @Insert("INSERT INTO employee (id, name, surname, email, active) VALUES (#{id}, #{name}, #{surname}, #{email}, #{active})")
    void insert(EmployeeEntity entity);

    @Update("UPDATE employee SET name = #{name}, surname = #{surname}, email = #{email}, active = #{active} WHERE id = #{id}")
    void update(EmployeeEntity entity);
}
