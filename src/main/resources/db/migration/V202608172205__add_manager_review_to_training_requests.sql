-- Add manager review and rejection fields to TRAINING_REQUESTS table
ALTER TABLE training_requests ADD (
    reviewed_by VARCHAR2(10),
    rejection_reason VARCHAR2(500),
    manager_notes VARCHAR2(1000),
    reviewed_at TIMESTAMP
);

-- Performance indexes for manager queries
CREATE INDEX idx_tr_reviewed_by ON training_requests(reviewed_by);
CREATE INDEX idx_tr_status ON training_requests(status);
