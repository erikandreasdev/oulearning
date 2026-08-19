# OULearning Bruno API Collection

Bruno API collection for testing all endpoints across the bounded contexts of the **OULearning Platform**:
- `organization/`: Organization snapshot hierarchy, cache queries, and employee assignment.
- `budgeting/`: Fiscal year budget allocations, fund reservations, and distributions.
- `training/`: Training request form submissions, budget reservations, manager reviews, approvals, and rejections.
- `environments/`: Environment variable configurations (`local`).
- `samples/`: Sample CSV and Excel files for multipart upload requests:
  - `organization-50-ou.csv`: 50 Organizational Units hierarchy (1 Root, 7 Divisions, 21 Departments, 21 Teams).
  - `employees-100.csv`: 100 Employees distributed across the 50 OUs with valid `CK0001`-`CK0100` keys.
  - `organization-100-ou.csv`: 100 Organizational Units hierarchy (1 Root, 9 Divisions, 30 Departments, 60 Teams).
  - `employees-5000.csv`: 5000 Employees evenly distributed across 100 OUs with valid `CK0001`-`CK5000` keys.
  - `organization.csv` & `employees.csv`: Minimal quick-start samples.

---

## 📖 Testing Guide

For a complete step-by-step end-to-end workflow walkthrough with sequence diagrams, request payloads, and expected responses, see:

👉 [**WORKFLOW_TESTING.md**](file:///Users/erik/dev/oulearning/bruno/WORKFLOW_TESTING.md)
