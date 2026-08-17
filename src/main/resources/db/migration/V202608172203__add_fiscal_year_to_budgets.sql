-- Add fiscal_year column to budgets table
ALTER TABLE budgets ADD (fiscal_year NUMBER(4) DEFAULT 2026 NOT NULL);

-- Performance and uniqueness constraints
CREATE INDEX idx_budgets_fiscal_year ON budgets(fiscal_year);
ALTER TABLE budgets ADD CONSTRAINT uq_budgets_ou_fiscal_year UNIQUE (ou_id, fiscal_year);
