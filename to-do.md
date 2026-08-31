# Endpoints
1. Endpoint to get trainings for an specified OU.
Expected parameters: 'ouCode'
Expected response:
Assgined budget, Budget available to spend, List of trainings (id, name, list of ouCodes associated with the training, training budget, training status (enum)).    

Training status can be one of the following values:
- REQUESTED: The training is requested, pending for approval.
- VALIDATED: The training is validated, booked and ready to deliver.
- IN_PROGRESS: The training is in progress.
- COMPLETED: The training is completed.
- CANCELLED: The training is cancelled.

2. Endpoint to get the details of a single training (by id). Must validate if employee doing the request belongs to the list of owners of the OU that owns the training or if it is an employee that belongs to an OU that owns the training. If not, return 403 forbidden.
Expected parameters: 'id' (path parameter), employeeId (query parameter)
Expected response: training name, cost, owner name that request the training, purpose, type, hours and attendees (name, email).

3. Endpoint to request a new Training. The owner of the Ou want to request a new training for his ou and want to send x members of the same ou to the training. Members from another Ous are not allowed.
Expected params: training_name, cost, owner_ou, purpose, type, hours, attendees(list of ids).
Expected Response: Training object (id, name, cost, owner_ou, purpose, type, hours, attendees(ids)).

4. Endpoint to get list of budgets per OU. This query has a flag to query subtree for the specified OU.
Expected parameters: 'ouCode', 'includeSubtree' (boolean)
Expected response: List of budgets (ouCode, assigned_budget, available_budget, owners (list of ouCodes that own this budget).
If includeSubtree = false: returns budgets for the specified OU.
If includeSubtree = true: returns budgets for the specified OU and all its childs OUs.

Example:
OU1 -> OU2 -> OU3
OU1: owners list A
OU2: ownwers list B
OU3: ownwers list C

Query: ouCode = OU2, includeSubtree = true
Response: 
- OU1: [budget1, budget2, budget3] (owners list A)
- OU2: [budget2, budget3] (owners list B)
- OU3: [budget3] (owners list C)

Query: ouCode = OU2, includeSubtree = false
Response: 
- OU2: [budget2] (owners list B)

5. Endpoint to list all training request. It has filters for training_name, cost, owner_ou, purpose, type, hours, training_status. Response must be pageable.
Expected parameters: 'training_name', 'cost', 'owner_ou', 'purpose', 'type', 'hours', 'training_status'
Expected response: List of training requests (id, name, cost, owner_ou, purpose, type, hours, training_status)

6. Endpoint to update a training request. It updates the values of manager review only. The rest is not possible to be updated.

This is the domain layer object
public record ManagerReview(
        String comments,
        Modality modality,
        Instant startDate,
        Instant endDate,
        ExternalProviderId externalProviderId,
        Instant reviewedAt) {

    public ManagerReview {
        comments = TrainingGuard.requireValidComments(comments);
        TrainingGuard.requireModality(modality);
        TrainingGuard.requireStartDate(startDate);
        TrainingGuard.requireEndDate(endDate);
        TrainingGuard.requireReviewedAt(reviewedAt);
        TrainingGuard.requireDateRange(startDate, endDate);
    }

    public Optional<ExternalProviderId> optionalExternalProviderId() {
        return Optional.ofNullable(externalProviderId);
    }
}

Expected parameters: training_id, comments, modality, startDate, endDate, externalProviderId, reviewedAt
Expected response: Training object (id, name, cost, owner_ou, purpose, type, hours, attendees(ids)) with the updated values.    

7. Endpoint to get details of a training.
Expected parameters: 'training_id' (path parameter)
Expected response: Entire Training object.

8. Endpoint to create a new OU-Budget. It can create budget for a specific OU or for an OU and all its childs OUs or even a subset of child OUs not including all childs. Response must be pageable. If it creates the budget for an OU and all its childs OUs, it must return all the budgets for the specific OU and all its childs OUs.
Expected parameters: assignedBudget, ouCode, owners (list of ouCodes that own this budget)
Expected response: OU-Budget object (id, assignedBudget, availableBudget, owners (list of ouCodes that own this budget)).

Notes:
- Remove previously created endpoints but do not remove use cases in application layer for future use.
- Update openapi/doc after those changes.
- Update doc folder after implementing those endpoints.

9. Endpoint to retrieve the list of employees that are members of an OU by ou id