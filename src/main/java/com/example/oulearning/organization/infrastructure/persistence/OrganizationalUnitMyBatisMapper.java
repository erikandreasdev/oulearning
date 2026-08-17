package com.example.oulearning.organization.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * MyBatis mapper interface using annotations for ORGANIZATIONAL_UNITS and its association tables.
 */
@Mapper
public interface OrganizationalUnitMyBatisMapper {

    @Insert("""
            INSERT INTO organizational_units (id, name, ou_type, snapshot_id, version)
            VALUES (#{id}, #{name}, #{ouType}, #{snapshotId}, #{version})
            """)
    void insertUnit(OrganizationalUnitEntity entity);

    @Update("""
            UPDATE organizational_units
            SET name = #{name}, ou_type = #{ouType}, snapshot_id = #{snapshotId}, version = version + 1
            WHERE id = #{id} AND version = #{version}
            """)
    int updateUnit(OrganizationalUnitEntity entity);

    @Select("""
            SELECT id, name, ou_type AS ouType, snapshot_id AS snapshotId, version
            FROM organizational_units
            WHERE id = #{id}
            """)
    OrganizationalUnitEntity findUnitById(@Param("id") String id);

    @Select("""
            SELECT id, name, ou_type AS ouType, snapshot_id AS snapshotId, version
            FROM organizational_units
            WHERE name = #{name}
            """)
    OrganizationalUnitEntity findUnitByName(@Param("name") String name);

    @Select("""
            SELECT id, name, ou_type AS ouType, snapshot_id AS snapshotId, version
            FROM organizational_units
            WHERE snapshot_id = #{snapshotId}
            """)
    List<OrganizationalUnitEntity> findUnitsBySnapshotId(@Param("snapshotId") String snapshotId);

    // Owners
    @Select("SELECT corporate_key FROM ou_owners WHERE ou_id = #{ouId}")
    Set<String> findOwnersByOuId(@Param("ouId") String ouId);

    @Insert("INSERT INTO ou_owners (ou_id, corporate_key) VALUES (#{ouId}, #{corporateKey})")
    void insertOwner(@Param("ouId") String ouId, @Param("corporateKey") String corporateKey);

    @Delete("DELETE FROM ou_owners WHERE ou_id = #{ouId}")
    void deleteOwnersByOuId(@Param("ouId") String ouId);

    // Parents
    @Select("SELECT parent_ou_id FROM ou_parents WHERE ou_id = #{ouId}")
    Set<String> findParentsByOuId(@Param("ouId") String ouId);

    @Insert("INSERT INTO ou_parents (ou_id, parent_ou_id) VALUES (#{ouId}, #{parentOuId})")
    void insertParent(@Param("ouId") String ouId, @Param("parentOuId") String parentOuId);

    @Delete("DELETE FROM ou_parents WHERE ou_id = #{ouId}")
    void deleteParentsByOuId(@Param("ouId") String ouId);

    // Children
    @Select("SELECT child_ou_id FROM ou_children WHERE ou_id = #{ouId}")
    Set<String> findChildrenByOuId(@Param("ouId") String ouId);

    @Insert("INSERT INTO ou_children (ou_id, child_ou_id) VALUES (#{ouId}, #{childOuId})")
    void insertChild(@Param("ouId") String ouId, @Param("childOuId") String childOuId);

    @Delete("DELETE FROM ou_children WHERE ou_id = #{ouId}")
    void deleteChildrenByOuId(@Param("ouId") String ouId);
}
