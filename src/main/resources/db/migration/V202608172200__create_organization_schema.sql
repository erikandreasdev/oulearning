-- Organization Snapshots table (Aggregate root for organization history)
CREATE TABLE organization_snapshots (
    id VARCHAR2(36) NOT NULL,
    root_ou_id VARCHAR2(36) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_organization_snapshots PRIMARY KEY (id)
);

-- Organizational Units table
CREATE TABLE organizational_units (
    id VARCHAR2(36) NOT NULL,
    name VARCHAR2(100) NOT NULL,
    ou_type VARCHAR2(30) NOT NULL,
    snapshot_id VARCHAR2(36),
    parent_ou_id VARCHAR2(36),
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_organizational_units PRIMARY KEY (id),
    CONSTRAINT fk_ou_snapshot FOREIGN KEY (snapshot_id) REFERENCES organization_snapshots(id) ON DELETE CASCADE,
    CONSTRAINT fk_ou_parent FOREIGN KEY (parent_ou_id) REFERENCES organizational_units(id) ON DELETE SET NULL
);

-- OU Owners join table
CREATE TABLE ou_owners (
    ou_id VARCHAR2(36) NOT NULL,
    corporate_key VARCHAR2(10) NOT NULL,
    CONSTRAINT pk_ou_owners PRIMARY KEY (ou_id, corporate_key),
    CONSTRAINT fk_ou_owners_ou FOREIGN KEY (ou_id) REFERENCES organizational_units(id) ON DELETE CASCADE
);

-- OU Parents join table
CREATE TABLE ou_parents (
    ou_id VARCHAR2(36) NOT NULL,
    parent_ou_id VARCHAR2(36) NOT NULL,
    CONSTRAINT pk_ou_parents PRIMARY KEY (ou_id, parent_ou_id),
    CONSTRAINT fk_ou_parents_ou FOREIGN KEY (ou_id) REFERENCES organizational_units(id) ON DELETE CASCADE
);

-- OU Children join table
CREATE TABLE ou_children (
    ou_id VARCHAR2(36) NOT NULL,
    child_ou_id VARCHAR2(36) NOT NULL,
    CONSTRAINT pk_ou_children PRIMARY KEY (ou_id, child_ou_id),
    CONSTRAINT fk_ou_children_ou FOREIGN KEY (ou_id) REFERENCES organizational_units(id) ON DELETE CASCADE
);

-- Performance indexes
CREATE INDEX idx_ou_snapshot_id ON organizational_units(snapshot_id);
CREATE INDEX idx_ou_parent_id ON organizational_units(parent_ou_id);
CREATE INDEX idx_ou_name ON organizational_units(name);
CREATE INDEX idx_org_created_at ON organization_snapshots(created_at);
