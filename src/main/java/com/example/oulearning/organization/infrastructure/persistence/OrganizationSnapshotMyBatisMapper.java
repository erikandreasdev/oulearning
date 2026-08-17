package com.example.oulearning.organization.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * MyBatis mapper interface using annotations for ORGANIZATION_SNAPSHOTS table.
 */
@Mapper
public interface OrganizationSnapshotMyBatisMapper {

    @Insert("""
            INSERT INTO organization_snapshots (id, root_ou_id, status, created_at, version)
            VALUES (#{id}, #{rootOuId}, #{status}, #{createdAt}, #{version})
            """)
    void insertSnapshot(OrganizationSnapshotEntity entity);

    @Update("""
            UPDATE organization_snapshots
            SET status = 'ARCHIVED'
            WHERE status = 'ACTIVE'
            """)
    int archivePreviousSnapshots();

    @Select("""
            SELECT id, root_ou_id AS rootOuId, status, created_at AS createdAt, version
            FROM organization_snapshots
            WHERE status = 'ACTIVE'
            ORDER BY created_at DESC
            FETCH FIRST 1 ROWS ONLY
            """)
    OrganizationSnapshotEntity findLatestSnapshot();

    @Select("""
            SELECT id, root_ou_id AS rootOuId, status, created_at AS createdAt, version
            FROM organization_snapshots
            WHERE id = #{snapshotId}
            """)
    OrganizationSnapshotEntity findSnapshotById(@Param("snapshotId") String snapshotId);

    @Select("""
            SELECT id, root_ou_id AS rootOuId, status, created_at AS createdAt, version
            FROM organization_snapshots
            WHERE created_at <= #{timestamp}
            ORDER BY created_at DESC
            FETCH FIRST 1 ROWS ONLY
            """)
    OrganizationSnapshotEntity findSnapshotAt(@Param("timestamp") Instant timestamp);

    @Select("""
            SELECT id, root_ou_id AS rootOuId, status, created_at AS createdAt, version
            FROM organization_snapshots
            ORDER BY created_at ASC
            """)
    List<OrganizationSnapshotEntity> findAllSnapshots();
}
