INSERT INTO employee (id, name, surname, email, active) VALUES (11, 'Jane', 'Smith', 'jane.smith@example.com', 1);
INSERT INTO organizational_unit (id, name, parent_id, active) VALUES (2, 'OU 2', NULL, 1);
INSERT INTO training_type (id, name, parent_type_id) VALUES (5, 'Technology', NULL);
INSERT INTO training (id, requested_by_employee_id, organizational_unit_id, name, cost_amount, cost_currency, hours, purpose_type, purpose_other, type_id, status, created_at, updated_at, active)
VALUES (2, 11, 2, 'Spring Boot Masterclass', 1200.00, 'EUR', 30, 'INDIVIDUAL_DEVELOPMENT_PLAN', NULL, 5, 'REQUESTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO employee (id, name, surname, email, active) VALUES (10, 'Test', 'Owner', 'owner10@example.com', 1);
INSERT INTO organizational_unit_owner (organizational_unit_id, employee_id) VALUES (2, 11);
INSERT INTO organizational_unit_member (organizational_unit_id, employee_id) VALUES (2, 11);
INSERT INTO organizational_unit_owner (organizational_unit_id, employee_id) VALUES (2, 10);
INSERT INTO organizational_unit_member (organizational_unit_id, employee_id) VALUES (2, 10);
