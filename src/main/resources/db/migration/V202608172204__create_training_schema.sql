-- Create TRAINING_REQUESTS table
CREATE TABLE training_requests (
    id VARCHAR2(36) NOT NULL,
    ou_id VARCHAR2(36) NOT NULL,
    requester_corporate_key VARCHAR2(10) NOT NULL,
    name VARCHAR2(200) NOT NULL,
    cost_amount NUMBER(15, 2) NOT NULL,
    cost_currency VARCHAR2(3) NOT NULL,
    purpose_type VARCHAR2(50) NOT NULL,
    purpose_custom_text VARCHAR2(500),
    training_hours NUMBER(4) NOT NULL,
    available_at_org_university NUMBER(1) NOT NULL,
    fiscal_year NUMBER(4) NOT NULL,
    status VARCHAR2(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_training_requests PRIMARY KEY (id),
    CONSTRAINT fk_training_requests_ou FOREIGN KEY (ou_id) REFERENCES organizational_units(id)
);

-- Create TRAINING_REQUEST_ASSISTANTS table
CREATE TABLE training_request_assistants (
    training_request_id VARCHAR2(36) NOT NULL,
    corporate_key VARCHAR2(10) NOT NULL,
    CONSTRAINT pk_training_request_assistants PRIMARY KEY (training_request_id, corporate_key),
    CONSTRAINT fk_tr_assistants_request FOREIGN KEY (training_request_id) REFERENCES training_requests(id) ON DELETE CASCADE
);

-- Performance indexes
CREATE INDEX idx_tr_ou_id ON training_requests(ou_id);
CREATE INDEX idx_tr_fiscal_year ON training_requests(fiscal_year);
CREATE INDEX idx_tr_requester ON training_requests(requester_corporate_key);
CREATE INDEX idx_tr_assistants_ck ON training_request_assistants(corporate_key);
