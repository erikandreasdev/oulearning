# Domain Glossary

This document captures the Ubiquitous Language for the domain model.

## Shared Domain

### DomainException
The sealed base class for all domain-specific exceptions across bounded contexts.

## Organization Bounded Context

### OuId
Strongly-typed identity value object uniquely identifying an organizational unit using a `UUID`. Package: `com.example.oulearning.organization.domain.unit`.

### Email
An electronic mail address used for communication and identification. It is normalized (trimmed and lowercased) and validated against standard email format constraints.

### Phone
A contact telephone number conforming to E.164-compatible international numbering standards (7 to 15 digits, with an optional leading `+`). Formatted input is normalized by removing separating characters such as spaces, hyphens, and parentheses.

### Name
A person's given/first name. Normalized and validated to contain valid Unicode letters, spaces, hyphens, or apostrophes within length constraints (1 to 100 characters).

### Surname
A person's family/last name. Normalized and validated to contain valid Unicode letters, spaces, hyphens, or apostrophes within length constraints (1 to 100 characters).

### FullName
A composite value object combining a `Name` and a `Surname` to represent a person's complete name.

### CorporateKey
A unique organizational identifier assigned to an employee, adhering to the format `CK` followed by 4 numeric digits (e.g. `CK0001`, `CK1234`).

### EmployeeRole
The organizational functional role assigned to an employee (`EMPLOYEE`, `MANAGER`, `TRAINER`, `ADMIN`).

### Employee
A value object representing an employee in the organization, composed of their `CorporateKey`, `FullName`, `Email`, and `EmployeeRole`.

### OuName
A value object representing the normalized name of an organizational unit (1 to 100 characters).

### OuType
Classification of an organizational unit: `ORGANIZATION` (root), `AREA` (composite/intermediate), or `SUBAREA` (leaf).

### OuSearchCriteria
Value object encapsulating search parameters (`OuId`, `OuName`, `includeSubtree`) for locating organizational units.

### OrganizationalUnit
The domain model representing an organizational unit within the organization hierarchy across N levels. It encapsulates its identity, name, type classification, owners, a single parent reference (`parentId`), child references (`childIds`), and loaded child instances (`loadedChildren`).
- **Root Unit**: `isRoot() == true` (`parentId == null`).
- **Leaf Unit**: `isLeaf() == true` (`childIds.isEmpty()`).
- **Subtree Loaded**: `isSubtreeLoaded() == true` (`childIds.isEmpty() || loadedChildren.size() == childIds.size()`).

### OrganizationalUnitRepository
Domain repository port interface for finding and persisting `OrganizationalUnit` instances. Package: `com.example.oulearning.organization.domain.unit.repository`.

### SnapshotId
Strongly-typed identity value object uniquely identifying an immutable snapshot of the organization.

### Organization
The Aggregate Root representing the entire organizational hierarchy at a specific point in time. It starts from a single root `OrganizationalUnit` (Level 1) with no parent (`parentId == null`) and maintains the full set of all unit IDs (`ouIds: Set<OuId>`) belonging to the organization snapshot. Supports querying total units count (`totalOusCount()`), tree depth (`depth()`), checking membership (`containsOu(OuId)`), and finding units by ID or Name.

### OrganizationRepository
Domain repository port interface for persisting and querying historical `Organization` snapshots. Package: `com.example.oulearning.organization.domain.organization.repository`.

## Budgeting Bounded Context

### Money
An immutable value object representing a monetary amount backed by the Moneta library (JSR 354 reference implementation), using EUR as the default currency across the project. Package: `com.example.oulearning.budgeting.domain.budget`.

### BudgetId
Strongly-typed identity value object uniquely identifying a `Budget` aggregate using a `UUID`.

### Budget
The Aggregate Root in the budgeting context representing the financial allocation and lifecycle for an organizational unit (`OuId`). Tracks allocated, reserved, and spent funds, and computes the currently available balance.

### BudgetRepository
Domain repository port interface for persisting and querying `Budget` aggregates. Package: `com.example.oulearning.budgeting.domain.budget.repository`.

### Allocated
The total monetary budget assigned to an organizational unit. Defaults to zero if no budget has been assigned.

### Reserved
Funds temporarily placed on hold/committed for planned activities (e.g. approved upcoming trainings) before invoices or final costs are settled.

### Spent
Finalized, consumed expenses that have been deducted from the budget.

### Available
The computed uncommitted funds remaining in a budget, calculated as $\text{allocated} - (\text{reserved} + \text{spent})$.

### BudgetDistributionStrategy
A domain strategy defining how a parent organizational unit distributes its allocated funds to its children:
- **`ExclusiveAllocation`**: Funds stay exclusively on the parent OU without cascading.
- **`EqualDistribution`**: Funds are divided equally among all child OUs (with exact cent remainder handling).
- **`ExplicitDistribution`**: Custom specific amounts are allocated to designated child OUs.

### BudgetDistributionService
Domain Service for executing budget distribution strategies across child organizational units.

## Training Bounded Context

### TrainingRequestId
Strongly-typed identity value object uniquely identifying a `TrainingRequest` using a `UUID`. Package: `com.example.oulearning.training.domain.request.vo.identity`.

### TrainingName
Value object representing the descriptive name of a requested training program (1 to 200 characters).

### TrainingCost
Value object encapsulating the estimated or actual monetary cost of a training program.

### TrainingHours
Value object representing the total duration in hours dedicated to a training program.

### TrainingPurpose
Value object encapsulating the rationale for taking a training program, combining a `TrainingPurposeType` (`UPSKILLING`, `RESKILLING`, `CERTIFICATION`, `COMPLIANCE`, `OTHER`) and descriptive notes.

### TrainingRequestStatus
Lifecycle state of a training request: `PENDING_APPROVAL`, `APPROVED`, `REJECTED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.

### TrainingRequest
The Aggregate Root in the training bounded context representing an employee's application for professional training, including financial impact, organizational unit context, approval decision lifecycle, and auditing timestamps.

### TrainingRequestRepository
Domain repository port interface for persisting and querying `TrainingRequest` aggregates. Package: `com.example.oulearning.training.domain.request.repository`.
