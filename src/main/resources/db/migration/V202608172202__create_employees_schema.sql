-- Employees table
CREATE TABLE employees (
    corporate_key VARCHAR2(10) NOT NULL,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(255) NOT NULL,
    phone VARCHAR2(50),
    role VARCHAR2(30) NOT NULL,
    ou_id VARCHAR2(36) NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_employees PRIMARY KEY (corporate_key),
    CONSTRAINT fk_employees_ou FOREIGN KEY (ou_id) REFERENCES organizational_units(id) ON DELETE CASCADE
);

-- Performance indexes for OU queries and unique email lookups
CREATE INDEX idx_employees_ou_id ON employees(ou_id);
CREATE INDEX idx_employees_email ON employees(email);
