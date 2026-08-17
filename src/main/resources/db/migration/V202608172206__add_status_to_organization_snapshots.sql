-- Add status column to ORGANIZATION_SNAPSHOTS table
ALTER TABLE organization_snapshots ADD (
    status VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL
);

-- Performance index for active status query
CREATE INDEX idx_org_status ON organization_snapshots(status);
