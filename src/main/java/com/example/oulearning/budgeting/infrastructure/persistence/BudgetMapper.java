package com.example.oulearning.budgeting.infrastructure.persistence;

import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
interface BudgetMapper {

    @Select(
            "SELECT id, organizational_unit_id AS organizationalUnitId, fiscal_year AS fiscalYear, total_amount AS totalAmount, reserved_amount AS reservedAmount, available_amount AS availableAmount, active FROM budget WHERE id = #{id}")
    Optional<BudgetEntity> findById(Long id);

    @Select(
            "SELECT id, organizational_unit_id AS organizationalUnitId, fiscal_year AS fiscalYear, total_amount AS totalAmount, reserved_amount AS reservedAmount, available_amount AS availableAmount, active FROM budget WHERE organizational_unit_id = #{organizationalUnitId}")
    java.util.List<BudgetEntity> findByOrganizationalUnitId(Long organizationalUnitId);

    @Insert(
            "INSERT INTO budget (organizational_unit_id, fiscal_year, total_amount, reserved_amount, available_amount, active) VALUES (#{organizationalUnitId}, #{fiscalYear}, #{totalAmount}, #{reservedAmount}, #{availableAmount}, #{active})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(BudgetEntity entity);

    @Update(
            "UPDATE budget SET total_amount = #{totalAmount}, reserved_amount = #{reservedAmount}, available_amount = #{availableAmount}, active = #{active} WHERE id = #{id}")
    void update(BudgetEntity entity);
}
