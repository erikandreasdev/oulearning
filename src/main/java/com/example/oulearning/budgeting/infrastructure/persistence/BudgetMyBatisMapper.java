package com.example.oulearning.budgeting.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * MyBatis mapper interface using annotations for BUDGETS table.
 */
@Mapper
public interface BudgetMyBatisMapper {

    @Insert("""
            INSERT INTO budgets (
                id, ou_id, allocated_amount, allocated_currency,
                reserved_amount, reserved_currency, spent_amount, spent_currency, version
            ) VALUES (
                #{id}, #{ouId}, #{allocatedAmount}, #{allocatedCurrency},
                #{reservedAmount}, #{reservedCurrency}, #{spentAmount}, #{spentCurrency}, #{version}
            )
            """)
    void insertBudget(BudgetEntity entity);

    @Update("""
            UPDATE budgets
            SET allocated_amount = #{allocatedAmount},
                allocated_currency = #{allocatedCurrency},
                reserved_amount = #{reservedAmount},
                reserved_currency = #{reservedCurrency},
                spent_amount = #{spentAmount},
                spent_currency = #{spentCurrency},
                version = version + 1
            WHERE id = #{id} AND version = #{version}
            """)
    int updateBudget(BudgetEntity entity);

    @Select("""
            SELECT id, ou_id AS ouId,
                   allocated_amount AS allocatedAmount, allocated_currency AS allocatedCurrency,
                   reserved_amount AS reservedAmount, reserved_currency AS reservedCurrency,
                   spent_amount AS spentAmount, spent_currency AS spentCurrency,
                   version
            FROM budgets
            WHERE id = #{id}
            """)
    BudgetEntity findBudgetById(@Param("id") String id);

    @Select("""
            SELECT id, ou_id AS ouId,
                   allocated_amount AS allocatedAmount, allocated_currency AS allocatedCurrency,
                   reserved_amount AS reservedAmount, reserved_currency AS reservedCurrency,
                   spent_amount AS spentAmount, spent_currency AS spentCurrency,
                   version
            FROM budgets
            WHERE ou_id = #{ouId}
            """)
    BudgetEntity findBudgetByOuId(@Param("ouId") String ouId);

    @Select("""
            <script>
            SELECT id, ou_id AS ouId,
                   allocated_amount AS allocatedAmount, allocated_currency AS allocatedCurrency,
                   reserved_amount AS reservedAmount, reserved_currency AS reservedCurrency,
                   spent_amount AS spentAmount, spent_currency AS spentCurrency,
                   version
            FROM budgets
            WHERE ou_id IN
            <foreach item='ouId' collection='ouIds' open='(' separator=',' close=')'>
                #{ouId}
            </foreach>
            </script>
            """)
    List<BudgetEntity> findAllBudgetsByOuIds(@Param("ouIds") Collection<String> ouIds);
}
