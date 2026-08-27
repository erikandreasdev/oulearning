INSERT INTO organizational_unit (id, name, parent_id, active) VALUES (2, 'OU 2', NULL, 1);
INSERT INTO budget (id, organizational_unit_id, fiscal_year, total_amount, reserved_amount, available_amount, active)
VALUES (2, 2, 2026, 5000.00, 0.00, 5000.00, 1);
