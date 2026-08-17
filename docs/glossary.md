# Domain Glossary

This document captures the Ubiquitous Language for the domain model.

## Shared Domain

### Money
An immutable value object representing a monetary amount backed by the Moneta library (JSR 354 reference implementation), using EUR as the default currency across the project.

### OuId
Strongly-typed identity value object uniquely identifying an organizational unit across bounded contexts using a `UUID`.

## Organization Bounded Context

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

### OrganizationalUnit
The unified domain model representing an organizational unit within the organization hierarchy across N levels. It encapsulates its identity, name, type classification, owners, parent references, assigned budget, child references, and loaded child instances.
- **Root Unit**: `isRoot() == true` (`parentIds.isEmpty()`).
- **Leaf Unit**: `isLeaf() == true` (`childIds.isEmpty()`).
- **Child Budget Consistency**: When an OU's child units are loaded, the sum of all its direct child budgets must equal the OU's budget.

### SnapshotId
Strongly-typed identity value object uniquely identifying an immutable snapshot of the organization.

### Organization
The Aggregate Root representing the entire organizational hierarchy at a specific point in time. It starts from a single root `OrganizationalUnit` (Level 1) with no parents and maintains historical snapshots as organization structure evolutions occur. Supports calculating total organization budgets, subtree budgets, and collection budgets.

## Budgeting Bounded Context

### BudgetId
Strongly-typed identity value object uniquely identifying a `Budget` aggregate using a `UUID`.

### Budget
The Aggregate Root in the budgeting context representing the financial allocation and lifecycle for an organizational unit (`OuId`). Tracks allocated, reserved, and spent funds, and computes the currently available balance.

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
