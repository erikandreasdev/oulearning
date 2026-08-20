# Ubiquitous Language & Domain Glossary

This document defines the ubiquitous language and domain concepts for the **OULearning** platform across all bounded contexts.

---

## 1. Organization Bounded Context

The Organization context models company hierarchy, structure, organizational units, and employees.

### A. Hierarchy Sub-domain
- **Organization**: Aggregate root representing the company organization, containing the set of organizational unit identifiers that form the company tree.
- **OrganizationalUnit**: Core structural unit of the company (e.g. department, squad, division). Defines identity, name, hierarchical parent/child relations, designated unit owners, and assigned members.
- **OrganizationalUnitId**: Value object representing the unique identifier of an Organizational Unit.
- **Name (OrganizationalUnit)**: Value object representing the official name of an organizational unit.
- **Owners**: Employees designated as managers or leaders with administrative authority over the organizational unit.
- **Members**: Employees belonging to and participating in the organizational unit.

### B. Employee Sub-domain
- **Employee**: Aggregate root representing an individual employee in the organization. Encapsulates identity, full name, and corporate email.
- **EmployeeId**: Value object representing the unique identifier of an Employee.
- **Name (Employee)**: Value object representing an employee's first name.
- **Surname (Employee)**: Value object representing an employee's surname or family name.
- **FullName**: Value object composing an employee's first name and surname.
- **Email**: Value object representing a valid corporate email address.

---

## 2. Budgeting Bounded Context

The Budgeting context models fiscal allocations, financial reservations, and training funds availability.

- **Budget**: Aggregate root representing the annual training budget allocated to a specific organizational unit for a given fiscal year. Encapsulates the total allocated amount, reserved funds committed to approved/pending requests, and the currently available balance.
- **BudgetId**: Value object representing the unique identifier of a Budget.
- **FiscalYear**: Value object representing the fiscal calendar year to which a budget applies.
- **Money**: Value object representing a monetary amount in EUR. Provides arithmetic and comparison operations for financial calculations.

---

## 3. Training Bounded Context

The Training context handles training catalog types, training requests, lifecycle states, approvals, and external provider coordination.

- **Training**: Aggregate root representing a formal training request submitted by an employee. Encapsulates the applicant, target unit, title, projected cost, required hours, strategic purpose, category type, lifecycle status, manager review outcome, and enrolled attendees.
- **TrainingId**: Value object representing the unique identifier of a Training request.
- **TrainingName**: Value object representing the descriptive title of the training program.
- **Cost**: Value object representing the monetary expenditure required for a training program.
- **Hours**: Value object representing the total duration and time commitment in hours.
- **TrainingStatus**: Lifecycle state of a training request:
  - `REQUESTED`: Submitted by the employee and pending manager evaluation.
  - `APPROVED`: Evaluated, accepted, and authorized by the manager.
  - `REJECTED`: Evaluated and declined by the manager.
- **TrainingPurposeType**: Classification of the strategic rationale for the training:
  - `INDIVIDUAL_DEVELOPMENT_PLAN`: Aligned with the employee's Individual Development Plan.
  - `DEPARTMENT_GOALS`: Aligned with strategic departmental objectives.
  - `OTHER`: Custom purpose requiring additional contextual explanation.
- **TrainingPurpose**: Value object capturing the purpose classification and optional custom description when categorized as `OTHER`.
- **Modality**: Delivery format of the training program:
  - `VIRTUAL`: Online instructor-led session.
  - `ON_SITE`: In-person classroom or location-based training.
  - `E_LEARNING`: Self-paced digital learning modules.
  - `BLENDED`: Combination of delivery methods.
- **ManagerReview**: Value object capturing the manager's evaluation decision: feedback comments, delivery modality, scheduled date window (start and end dates), review timestamp, and optional external provider.
- **ExternalProvider**: Value object representing a third-party organization delivering training services, composed of provider name and contact information.
- **ExternalProviderName**: Value object representing the name of a third-party training vendor.
- **ExternalProviderContact**: Value object holding contact details (email and phone number) for a training provider.
- **Phone**: Value object representing a validated contact telephone number.
- **Type**: Entity representing a training category within the organization's training taxonomy, optionally referencing a parent category.
- **TypeId**: Value object representing the unique identifier of a training type category.
- **TypeName**: Value object representing the name of a training category.
