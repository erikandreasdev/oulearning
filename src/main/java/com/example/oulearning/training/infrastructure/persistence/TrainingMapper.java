package com.example.oulearning.training.infrastructure.persistence;

import java.util.Optional;
import java.util.Set;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
interface TrainingMapper {

    @Select(
            "SELECT id, requested_by_employee_id AS requestedByEmployeeId, organizational_unit_id AS organizationalUnitId, "
                    + "name, cost_amount AS costAmount, cost_currency AS costCurrency, hours, purpose_type AS purposeType, "
                    + "purpose_other AS purposeOther, type_id AS typeId, status, manager_review_comments AS managerReviewComments, "
                    + "manager_review_modality AS managerReviewModality, manager_review_start_date AS managerReviewStartDate, "
                    + "manager_review_end_date AS managerReviewEndDate, manager_review_external_provider_id AS managerReviewExternalProviderId, "
                    + "manager_review_reviewed_at AS managerReviewReviewedAt, created_at AS createdAt, updated_at AS updatedAt, active "
                    + "FROM training WHERE id = #{id}")
    Optional<TrainingEntity> findById(Long id);

    @Select(
            "SELECT id, requested_by_employee_id AS requestedByEmployeeId, organizational_unit_id AS organizationalUnitId, "
                    + "name, cost_amount AS costAmount, cost_currency AS costCurrency, hours, purpose_type AS purposeType, "
                    + "purpose_other AS purposeOther, type_id AS typeId, status, manager_review_comments AS managerReviewComments, "
                    + "manager_review_modality AS managerReviewModality, manager_review_start_date AS managerReviewStartDate, "
                    + "manager_review_end_date AS managerReviewEndDate, manager_review_external_provider_id AS managerReviewExternalProviderId, "
                    + "manager_review_reviewed_at AS managerReviewReviewedAt, created_at AS createdAt, updated_at AS updatedAt, active "
                    + "FROM training WHERE organizational_unit_id = #{organizationalUnitId}")
    java.util.List<TrainingEntity> findByOrganizationalUnitId(Long organizationalUnitId);

    @Select("<script>"
            + "SELECT id, requested_by_employee_id AS requestedByEmployeeId, organizational_unit_id AS organizationalUnitId, "
            + "name, cost_amount AS costAmount, cost_currency AS costCurrency, hours, purpose_type AS purposeType, "
            + "purpose_other AS purposeOther, type_id AS typeId, status, manager_review_comments AS managerReviewComments, "
            + "manager_review_modality AS managerReviewModality, manager_review_start_date AS managerReviewStartDate, "
            + "manager_review_end_date AS managerReviewEndDate, manager_review_external_provider_id AS managerReviewExternalProviderId, "
            + "manager_review_reviewed_at AS managerReviewReviewedAt, created_at AS createdAt, updated_at AS updatedAt, active "
            + "FROM training "
            + "<where>"
            + "<if test='name != null and name != \"\"'> AND name LIKE #{name} </if>"
            + "<if test='costAmount != null'> AND cost_amount = #{costAmount} </if>"
            + "<if test='organizationalUnitId != null'> AND organizational_unit_id = #{organizationalUnitId} </if>"
            + "<if test='purposeType != null and purposeType != \"\"'> AND purpose_type = #{purposeType} </if>"
            + "<if test='typeId != null'> AND type_id = #{typeId} </if>"
            + "<if test='hours != null'> AND hours = #{hours} </if>"
            + "<if test='status != null and status != \"\"'> AND status = #{status} </if>"
            + "</where>"
            + "ORDER BY id DESC "
            + "OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY"
            + "</script>")
    java.util.List<TrainingEntity> findAll(
            @Param("name") String name,
            @Param("costAmount") java.math.BigDecimal costAmount,
            @Param("organizationalUnitId") Long organizationalUnitId,
            @Param("purposeType") String purposeType,
            @Param("typeId") Long typeId,
            @Param("hours") Integer hours,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Select("<script>"
            + "SELECT COUNT(*) FROM training "
            + "<where>"
            + "<if test='name != null and name != \"\"'> AND name LIKE #{name} </if>"
            + "<if test='costAmount != null'> AND cost_amount = #{costAmount} </if>"
            + "<if test='organizationalUnitId != null'> AND organizational_unit_id = #{organizationalUnitId} </if>"
            + "<if test='purposeType != null and purposeType != \"\"'> AND purpose_type = #{purposeType} </if>"
            + "<if test='typeId != null'> AND type_id = #{typeId} </if>"
            + "<if test='hours != null'> AND hours = #{hours} </if>"
            + "<if test='status != null and status != \"\"'> AND status = #{status} </if>"
            + "</where>"
            + "</script>")
    long count(
            @Param("name") String name,
            @Param("costAmount") java.math.BigDecimal costAmount,
            @Param("organizationalUnitId") Long organizationalUnitId,
            @Param("purposeType") String purposeType,
            @Param("typeId") Long typeId,
            @Param("hours") Integer hours,
            @Param("status") String status);

    @Insert(
            "INSERT INTO training (id, requested_by_employee_id, organizational_unit_id, name, cost_amount, cost_currency, hours, "
                    + "purpose_type, purpose_other, type_id, status, manager_review_comments, manager_review_modality, "
                    + "manager_review_start_date, manager_review_end_date, manager_review_external_provider_id, "
                    + "manager_review_reviewed_at, created_at, updated_at, active) "
                    + "VALUES (#{id}, #{requestedByEmployeeId}, #{organizationalUnitId}, #{name}, #{costAmount}, #{costCurrency}, #{hours}, "
                    + "#{purposeType}, #{purposeOther}, #{typeId}, #{status}, #{managerReviewComments}, #{managerReviewModality}, "
                    + "#{managerReviewStartDate}, #{managerReviewEndDate}, #{managerReviewExternalProviderId}, "
                    + "#{managerReviewReviewedAt}, #{createdAt}, #{updatedAt}, #{active})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(TrainingEntity entity);

    @Update(
            "UPDATE training SET requested_by_employee_id = #{requestedByEmployeeId}, organizational_unit_id = #{organizationalUnitId}, "
                    + "name = #{name}, cost_amount = #{costAmount}, cost_currency = #{costCurrency}, hours = #{hours}, "
                    + "purpose_type = #{purposeType}, purpose_other = #{purposeOther}, type_id = #{typeId}, status = #{status}, "
                    + "manager_review_comments = #{managerReviewComments}, manager_review_modality = #{managerReviewModality}, "
                    + "manager_review_start_date = #{managerReviewStartDate}, manager_review_end_date = #{managerReviewEndDate}, "
                    + "manager_review_external_provider_id = #{managerReviewExternalProviderId}, manager_review_reviewed_at = #{managerReviewReviewedAt}, "
                    + "created_at = #{createdAt}, updated_at = #{updatedAt}, active = #{active} "
                    + "WHERE id = #{id}")
    void update(TrainingEntity entity);

    @Select("SELECT employee_id FROM training_attendee WHERE training_id = #{trainingId}")
    Set<Long> findAttendeesByTrainingId(Long trainingId);

    @Insert("INSERT INTO training_attendee (training_id, employee_id) VALUES (#{trainingId}, #{employeeId})")
    void insertAttendee(@Param("trainingId") Long trainingId, @Param("employeeId") Long employeeId);

    @Delete("DELETE FROM training_attendee WHERE training_id = #{trainingId}")
    void deleteAttendeesByTrainingId(Long trainingId);
}
