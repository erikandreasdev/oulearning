package com.example.oulearning.training.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * MyBatis mapper interface using annotations for TRAINING_REQUESTS and TRAINING_REQUEST_ASSISTANTS tables.
 */
@Mapper
public interface TrainingRequestMyBatisMapper {

    @Insert("""
            INSERT INTO training_requests (
                id, ou_id, requester_corporate_key, name, cost_amount, cost_currency,
                purpose_type, purpose_custom_text, training_hours, available_at_org_university,
                fiscal_year, status, reviewed_by, rejection_reason, manager_notes, reviewed_at,
                created_at, version
            ) VALUES (
                #{id}, #{ouId}, #{requesterCorporateKey}, #{name}, #{costAmount}, #{costCurrency},
                #{purposeType}, #{purposeCustomText}, #{trainingHours}, #{availableAtOrgUniversity},
                #{fiscalYear}, #{status}, #{reviewedBy}, #{rejectionReason}, #{managerNotes}, #{reviewedAt},
                #{createdAt}, #{version}
            )
            """)
    void insertTrainingRequest(TrainingRequestEntity entity);

    @Update("""
            UPDATE training_requests
            SET ou_id = #{ouId},
                requester_corporate_key = #{requesterCorporateKey},
                name = #{name},
                cost_amount = #{costAmount},
                cost_currency = #{costCurrency},
                purpose_type = #{purposeType},
                purpose_custom_text = #{purposeCustomText},
                training_hours = #{trainingHours},
                available_at_org_university = #{availableAtOrgUniversity},
                fiscal_year = #{fiscalYear},
                status = #{status},
                reviewed_by = #{reviewedBy},
                rejection_reason = #{rejectionReason},
                manager_notes = #{managerNotes},
                reviewed_at = #{reviewedAt},
                version = version + 1
            WHERE id = #{id} AND version = #{version}
            """)
    int updateTrainingRequest(TrainingRequestEntity entity);

    @Insert("""
            INSERT INTO training_request_assistants (training_request_id, corporate_key)
            VALUES (#{trainingRequestId}, #{corporateKey})
            """)
    void insertAssistant(
            @Param("trainingRequestId") String trainingRequestId,
            @Param("corporateKey") String corporateKey);

    @Delete("""
            DELETE FROM training_request_assistants WHERE training_request_id = #{trainingRequestId}
            """)
    void deleteAssistantsByRequestId(@Param("trainingRequestId") String trainingRequestId);

    @Select("""
            SELECT id, ou_id AS ouId, requester_corporate_key AS requesterCorporateKey,
                   name, cost_amount AS costAmount, cost_currency AS costCurrency,
                   purpose_type AS purposeType, purpose_custom_text AS purposeCustomText,
                   training_hours AS trainingHours, available_at_org_university AS availableAtOrgUniversity,
                   fiscal_year AS fiscalYear, status, reviewed_by AS reviewedBy,
                   rejection_reason AS rejectionReason, manager_notes AS managerNotes,
                   reviewed_at AS reviewedAt, created_at AS createdAt, version
            FROM training_requests
            WHERE id = #{id}
            """)
    TrainingRequestEntity findTrainingRequestById(@Param("id") String id);

    @Select("""
            SELECT corporate_key
            FROM training_request_assistants
            WHERE training_request_id = #{trainingRequestId}
            """)
    Set<String> findAssistantsByRequestId(@Param("trainingRequestId") String trainingRequestId);

    @Select("""
            SELECT id, ou_id AS ouId, requester_corporate_key AS requesterCorporateKey,
                   name, cost_amount AS costAmount, cost_currency AS costCurrency,
                   purpose_type AS purposeType, purpose_custom_text AS purposeCustomText,
                   training_hours AS trainingHours, available_at_org_university AS availableAtOrgUniversity,
                   fiscal_year AS fiscalYear, status, reviewed_by AS reviewedBy,
                   rejection_reason AS rejectionReason, manager_notes AS managerNotes,
                   reviewed_at AS reviewedAt, created_at AS createdAt, version
            FROM training_requests
            WHERE ou_id = #{ouId}
            ORDER BY created_at DESC
            """)
    List<TrainingRequestEntity> findTrainingRequestsByOuId(@Param("ouId") String ouId);

    @Select("""
            SELECT id, ou_id AS ouId, requester_corporate_key AS requesterCorporateKey,
                   name, cost_amount AS costAmount, cost_currency AS costCurrency,
                   purpose_type AS purposeType, purpose_custom_text AS purposeCustomText,
                   training_hours AS trainingHours, available_at_org_university AS availableAtOrgUniversity,
                   fiscal_year AS fiscalYear, status, reviewed_by AS reviewedBy,
                   rejection_reason AS rejectionReason, manager_notes AS managerNotes,
                   reviewed_at AS reviewedAt, created_at AS createdAt, version
            FROM training_requests
            WHERE ou_id = #{ouId} AND fiscal_year = #{fiscalYear}
            ORDER BY created_at DESC
            """)
    List<TrainingRequestEntity> findTrainingRequestsByOuIdAndFiscalYear(
            @Param("ouId") String ouId,
            @Param("fiscalYear") int fiscalYear);

    @Select("""
            SELECT id, ou_id AS ouId, requester_corporate_key AS requesterCorporateKey,
                   name, cost_amount AS costAmount, cost_currency AS costCurrency,
                   purpose_type AS purposeType, purpose_custom_text AS purposeCustomText,
                   training_hours AS trainingHours, available_at_org_university AS availableAtOrgUniversity,
                   fiscal_year AS fiscalYear, status, reviewed_by AS reviewedBy,
                   rejection_reason AS rejectionReason, manager_notes AS managerNotes,
                   reviewed_at AS reviewedAt, created_at AS createdAt, version
            FROM training_requests
            WHERE fiscal_year = #{fiscalYear}
            ORDER BY created_at DESC
            """)
    List<TrainingRequestEntity> findTrainingRequestsByFiscalYear(@Param("fiscalYear") int fiscalYear);

    @Select("""
            <script>
            SELECT id, ou_id AS ouId, requester_corporate_key AS requesterCorporateKey,
                   name, cost_amount AS costAmount, cost_currency AS costCurrency,
                   purpose_type AS purposeType, purpose_custom_text AS purposeCustomText,
                   training_hours AS trainingHours, available_at_org_university AS availableAtOrgUniversity,
                   fiscal_year AS fiscalYear, status, reviewed_by AS reviewedBy,
                   rejection_reason AS rejectionReason, manager_notes AS managerNotes,
                   reviewed_at AS reviewedAt, created_at AS createdAt, version
            FROM training_requests
            <where>
                <if test="ouIds != null and !ouIds.isEmpty()">
                    AND ou_id IN
                    <foreach item="ouId" collection="ouIds" open="(" separator="," close=")">
                        #{ouId}
                    </foreach>
                </if>
                <if test="status != null and status != ''">
                    AND status = #{status}
                </if>
                <if test="fiscalYear != null">
                    AND fiscal_year = #{fiscalYear}
                </if>
            </where>
            ORDER BY created_at DESC
            </script>
            """)
    List<TrainingRequestEntity> findByCriteria(
            @Param("ouIds") Collection<String> ouIds,
            @Param("status") String status,
            @Param("fiscalYear") Integer fiscalYear);
}
