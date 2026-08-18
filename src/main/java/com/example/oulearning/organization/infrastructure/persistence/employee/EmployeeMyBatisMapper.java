package com.example.oulearning.organization.infrastructure.persistence.employee;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Annotation-style MyBatis mapper for the {@code employees} table.
 */
@Mapper
public interface EmployeeMyBatisMapper {

    @Select("""
            SELECT corporate_key, first_name, last_name, email, phone, role, ou_id, version
            FROM employees
            WHERE corporate_key = #{corporateKey}
            """)
    EmployeeEntity findEmployeeByCorporateKey(@Param("corporateKey") String corporateKey);

    @Select("""
            SELECT corporate_key, first_name, last_name, email, phone, role, ou_id, version
            FROM employees
            WHERE ou_id = #{ouId}
            ORDER BY corporate_key ASC
            """)
    List<EmployeeEntity> findEmployeesByOuId(@Param("ouId") String ouId);

    @Select("""
            <script>
            SELECT corporate_key, first_name, last_name, email, phone, role, ou_id, version
            FROM employees
            WHERE ou_id IN
            <foreach item='item' collection='ouIds' open='(' separator=',' close=')'>
                #{item}
            </foreach>
            ORDER BY corporate_key ASC
            </script>
            """)
    List<EmployeeEntity> findEmployeesByOuIds(@Param("ouIds") Collection<String> ouIds);

    @Insert("""
            INSERT INTO employees (
                corporate_key, first_name, last_name, email, phone, role, ou_id, version
            ) VALUES (
                #{corporateKey}, #{firstName}, #{lastName}, #{email}, #{phone}, #{role}, #{ouId}, #{version}
            )
            """)
    int insertEmployee(EmployeeEntity entity);

    @Update("""
            UPDATE employees
            SET first_name = #{firstName},
                last_name = #{lastName},
                email = #{email},
                phone = #{phone},
                role = #{role},
                ou_id = #{ouId},
                version = version + 1
            WHERE corporate_key = #{corporateKey} AND version = #{version}
            """)
    int updateEmployee(EmployeeEntity entity);

    @Delete("""
            DELETE FROM employees
            WHERE corporate_key = #{corporateKey}
            """)
    int deleteEmployee(@Param("corporateKey") String corporateKey);
}
