#!/bin/bash
set -e

# Setup directories
mkdir -p src/test/it/com/example/oulearning/budgeting
mkdir -p src/test/it/com/example/oulearning/training
mkdir -p src/test/it/com/example/oulearning/organization

# 1. Budgeting
cat << 'JAVA' > src/test/it/com/example/oulearning/budgeting/BudgetingApiEndpoints.java
package com.example.oulearning.budgeting;

public final class BudgetingApiEndpoints {
    private BudgetingApiEndpoints() {}
    public static final String BUDGETS_OU = "/api/v1/budgets/organizational-unit";
    public static final String BUDGETS_OU_BY_ID = "/api/v1/budgets/organizational-unit/%s";
}
JAVA

# 2. Training
cat << 'JAVA' > src/test/it/com/example/oulearning/training/TrainingApiEndpoints.java
package com.example.oulearning.training;

public final class TrainingApiEndpoints {
    private TrainingApiEndpoints() {}
    public static final String TRAINING_REQUESTS = "/api/v1/trainings/requests";
    public static final String TRAINING_REQUESTS_BY_OU = "/api/v1/trainings/requests?organizationalUnitId=%s";
    public static final String TRAINING_BY_ID = "/api/v1/trainings/%s";
    public static final String TRAINING_REVIEW = "/api/v1/trainings/%s/review";
    public static final String TRAINING_DETAILS = "/api/v1/trainings/%s/details";
    public static final String AREA_TRAININGS = "/api/v1/areas/%s/trainings";
}
JAVA

# 3. Organization
cat << 'JAVA' > src/test/it/com/example/oulearning/organization/OrganizationApiEndpoints.java
package com.example.oulearning.organization;

public final class OrganizationApiEndpoints {
    private OrganizationApiEndpoints() {}
    public static final String EMPLOYEES = "/api/v1/employees";
    public static final String EMPLOYEE_BY_ID = "/api/v1/employees/%s";
    public static final String ORGANIZATIONAL_UNITS = "/api/v1/organizational-units";
    public static final String ORGANIZATIONAL_UNIT_BY_ID = "/api/v1/organizational-units/%s";
    public static final String ORGANIZATIONAL_UNIT_MEMBERS = "/api/v1/organizational-units/%s/members";
}
JAVA

# Move WorkflowITs
mv src/test/it/com/example/oulearning/it/BudgetingWorkflowIT.java src/test/it/com/example/oulearning/budgeting/
mv src/test/it/com/example/oulearning/it/training/TrainingWorkflowIT.java src/test/it/com/example/oulearning/training/
mv src/test/it/com/example/oulearning/it/organization/OrganizationWorkflowIT.java src/test/it/com/example/oulearning/organization/

# Delete old `it` package
rm -rf src/test/it/com/example/oulearning/it

