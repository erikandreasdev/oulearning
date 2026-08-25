# Naming Conventions Reference

Quick-reference guide for naming across all layers of the **OULearning** codebase.

---

## 1. Packages
- **Bounded Context**: lowercase singular noun (`organization`, `budgeting`, `training`)
- **Sub-domain**: lowercase singular noun under bounded context (`organization.domain.employee`, `organization.domain.hierarchy`)
- **Layer**: `domain`, `application`, `infrastructure`
- **Rule**: No `shared` package. Package by context first, then layer.

---

## 2. Domain Layer
- **Aggregate Root / Entity**: PascalCase singular noun (`Employee`, `OrganizationalUnit`, `Training`, `Budget`)
- **Value Object**: PascalCase singular noun representing the wrapped concept (`Email`, `Money`, `FiscalYear`, `FullName`, `Cost`, `Hours`)
- **Typed ID**: `<Aggregate>Id` (`EmployeeId`, `OrganizationalUnitId`, `BudgetId`, `TrainingId`, `TypeId`)
- **Domain Guard**: `<Context|SubDomain>Guard` (`HierarchyGuard`, `EmployeeGuard`, `BudgetingGuard`, `TrainingGuard`)
- **Domain Constants**: `<Context|SubDomain>Constants` (`HierarchyConstants`, `EmployeeConstants`, `BudgetingConstants`, `TrainingConstants`)
- **Domain Exception Base**: sealed `<Context|SubDomain>Exception` (`HierarchyException`, `TrainingException`, `BudgetingException`, `EmployeeException`)
- **Domain Exception (specific)**: `Invalid<Concept>Exception`, `<Reason>Exception` (`InvalidTrainingOperationException`, `InsufficientBudgetException`, `CyclicHierarchyException`)
- **Repository Port**: `<Aggregate>Repository` (interface, speaks only in aggregates)
- **ID Generator Port**: `IdGenerator` (interface per domain package)
- **Factory Methods**: `create(...)`, `reconstitute(...)`, `of(...)`
- **Domain Mutation Methods**: Intention-revealing verb phrases (`addOwner`, `removeMember`, `rename`, `updateAmounts`, `updateDetails`, `deactivate`)

---

## 3. Application Layer
- **Use Case (Input Port Interface)**: `<Verb><Noun>UseCase` (`CreateEmployeeUseCase`, `AssignOwnerUseCase`, `UpdateBudgetUseCase`)
- **Use Case Service (Implementation)**: `<Verb><Noun>Service` (`CreateEmployeeService`, `AssignOwnerService`, `UpdateBudgetService`)
- **Command Record**: `<Verb><Noun>Command` (`CreateEmployeeCommand`, `AssignOwnerCommand`, `UpdateBudgetCommand`)
- **Application Exception**: `<Aggregate>NotFoundException` (`EmployeeNotFoundException`, `OrganizationalUnitNotFoundException`, `BudgetNotFoundException`, `TrainingNotFoundException`)

---

## 4. Tests
- **Test Class**: `<ClassUnderTest>Test` (`CreateEmployeeServiceTest`, `EmployeeTest`, `OrganizationalUnitTest`)
- **Test Method**: `given<A>_when<B>_then<C>` (`givenValidCommand_whenCreatingEmployee_thenEmployeeIsSavedAndIdReturned`)
- **Test Factory**: `<Context|SubDomain>TestFactory` (`EmployeeTestFactory`, `HierarchyTestFactory`, `BudgetingTestFactory`, `TrainingTestFactory`)
- **Structure**: Explicit comment blocks `// given`, `// when`, `// then` (only allowed comments in tests)

---

## 5. Variables & Types
- **Immutability**: `final var` for local variables (`final var employee = ...`)
- **Fields & Parameters**: `final` on all method parameters and class fields
- **Formatting**: Google Java Style, 4-space indent, 120 columns. No string concatenation (`+`), use `String#formatted` / `"%s ...".formatted(...)`.
