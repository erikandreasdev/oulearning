package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Name;
import com.example.oulearning.organization.domain.employee.Phone;
import com.example.oulearning.organization.domain.employee.Surname;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating {@link RegisterEmployeeUseCase}.
 */
@Service
@Transactional
public class RegisterEmployeeService implements RegisterEmployeeUseCase {

    private final EmployeeRepository employeeRepository;
    private final OrganizationalUnitRepository unitRepository;

    public RegisterEmployeeService(
            EmployeeRepository employeeRepository,
            OrganizationalUnitRepository unitRepository) {
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
        this.unitRepository = Objects.requireNonNull(unitRepository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    public String execute(RegisterEmployeeCommand command) {
        Objects.requireNonNull(command, "RegisterEmployeeCommand cannot be null");

        final var targetOuId = OuId.of(command.ouId());
        final var ouExists = unitRepository.find(OuSearchCriteria.byId(targetOuId, false));
        if (ouExists.isEmpty()) {
            throw new NoSuchElementException(
                    "OrganizationalUnit with ID '%s' not found".formatted(command.ouId()));
        }

        final var corporateKey = CorporateKey.of(command.corporateKey());
        final var name = Name.of(command.firstName());
        final var surname = Surname.of(command.lastName());
        final var fullName = FullName.of(name, surname);
        final var email = Email.of(command.email());
        final var phone = command.phone() != null && !command.phone().isBlank()
                ? Phone.of(command.phone())
                : null;
        final var role = EmployeeRole.fromString(command.role());

        final var employee = Employee.of(corporateKey, fullName, email, phone, role, targetOuId);
        employeeRepository.save(employee);

        return corporateKey.value();
    }
}
