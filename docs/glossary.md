# Domain Glossary

This document captures the Ubiquitous Language for the domain model.

## Shared Domain

### Money
An immutable value object representing a monetary amount backed by the Moneta library (JSR 354 reference implementation), using EUR as the default currency across the project.

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

### OuId
Strongly-typed identity value object uniquely identifying an organizational unit using a `UUID`.

### OuName
A value object representing the normalized name of an organizational unit (1 to 100 characters).

### OuType
Classification of an organizational unit: `AREA` (Type 1) or `SUBAREA` (Type 2).

### OrganizationalUnit
Sealed interface representing an organizational unit within the organization hierarchy. Permitted implementations are `Area` (Type 1) and `Subarea` (Type 2).

### Area
A Type 1 organizational unit that contains 0 or more child `Subarea`s and 0 or more parent OU references. When an `Area` contains child `Subarea`s, the sum of all its `Subarea`s' budgets must match the `Area` budget.

### Subarea
A Type 2 organizational unit representing a leaf unit in the organizational hierarchy with no child OUs. Budgets across subareas are not required to be equally distributed.
