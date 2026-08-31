package com.example.oulearning.organization.infrastructure.persistence.hierarchy;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
interface OrganizationalUnitMapper {

    @Select("SELECT id, name, parent_id, active FROM organizational_unit WHERE id = #{id}")
    Optional<OrganizationalUnitEntity> findById(Long id);

    @Select("SELECT id, name, parent_id, active FROM organizational_unit ORDER BY id ASC")
    List<OrganizationalUnitEntity> findAll();

    @Select("SELECT id, name, parent_id, active FROM organizational_unit WHERE name = #{name} AND parent_id = #{parentId}")
    Optional<OrganizationalUnitEntity> findByNameAndParentId(@Param("name") String name, @Param("parentId") Long parentId);

    @Select("SELECT id, name, parent_id, active FROM organizational_unit WHERE name = #{name} AND parent_id IS NULL")
    Optional<OrganizationalUnitEntity> findRootByName(@Param("name") String name);

    @Insert("INSERT INTO organizational_unit (id, name, parent_id, active) VALUES (#{id}, #{name}, #{parentId}, #{active})")
    void insert(OrganizationalUnitEntity entity);

    @Update("UPDATE organizational_unit SET name = #{name}, parent_id = #{parentId}, active = #{active} WHERE id = #{id}")
    void update(OrganizationalUnitEntity entity);

    @Select("SELECT id FROM organizational_unit WHERE parent_id = #{parentId}")
    List<Long> findChildIds(Long parentId);

    @Select("SELECT employee_id FROM organizational_unit_owner WHERE organizational_unit_id = #{organizationalUnitId}")
    List<Long> findOwnerIds(Long organizationalUnitId);

    @Insert("INSERT INTO organizational_unit_owner (organizational_unit_id, employee_id) VALUES (#{organizationalUnitId}, #{employeeId})")
    void insertOwner(@Param("organizationalUnitId") Long organizationalUnitId, @Param("employeeId") Long employeeId);

    @Delete("DELETE FROM organizational_unit_owner WHERE organizational_unit_id = #{organizationalUnitId} AND employee_id = #{employeeId}")
    void deleteOwner(@Param("organizationalUnitId") Long organizationalUnitId, @Param("employeeId") Long employeeId);

    @Select("SELECT employee_id FROM organizational_unit_member WHERE organizational_unit_id = #{organizationalUnitId}")
    List<Long> findMemberIds(Long organizationalUnitId);

    @Insert("INSERT INTO organizational_unit_member (organizational_unit_id, employee_id) VALUES (#{organizationalUnitId}, #{employeeId})")
    void insertMember(@Param("organizationalUnitId") Long organizationalUnitId, @Param("employeeId") Long employeeId);

    @Delete("DELETE FROM organizational_unit_member WHERE organizational_unit_id = #{organizationalUnitId} AND employee_id = #{employeeId}")
    void deleteMember(@Param("organizationalUnitId") Long organizationalUnitId, @Param("employeeId") Long employeeId);
}
