-- Budgets table (Aggregate root for OU financial budget)
CREATE TABLE budgets (
    id VARCHAR2(36) NOT NULL,
    ou_id VARCHAR2(36) NOT NULL,
    allocated_amount NUMBER(19, 2) NOT NULL,
    allocated_currency VARCHAR2(3) DEFAULT 'EUR' NOT NULL,
    reserved_amount NUMBER(19, 2) DEFAULT 0.00 NOT NULL,
    reserved_currency VARCHAR2(3) DEFAULT 'EUR' NOT NULL,
    spent_amount NUMBER(19, 2) DEFAULT 0.00 NOT NULL,
    spent_currency VARCHAR2(3) DEFAULT 'EUR' NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (id),
    CONSTRAINT uq_budgets_ou_id UNIQUE (ou_id)
);
