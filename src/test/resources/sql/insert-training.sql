INSERT INTO employee (id, name, surname, email, active) VALUES (11, 'Jane', 'Smith', 'jane.smith@example.com', 1);
INSERT INTO organizational_unit (id, name, parent_id, active) VALUES (2, 'OU 2', NULL, 1);
INSERT INTO training_type (id, name, parent_type_id) VALUES (5, 'Technology', NULL);
INSERT INTO training (id, requested_by_employee_id, organizational_unit_id, name, cost_amount, cost_currency, hours, purpose_type, purpose_other, type_id, status, created_at, updated_at, active)
VALUES (2, 11, 2, 'Spring Boot Masterclass', 1200.00, 'EUR', 30, 'INDIVIDUAL_DEVELOPMENT_PLAN', NULL, 5, 'DRAFT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
