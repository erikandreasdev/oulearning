package com.example.oulearning.organization.application.service.employee;
import com.example.oulearning.organization.domain.employee.vo.contact.Email;

import com.example.oulearning.organization.application.port.out.EmployeeFileParserPort;
import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.vo.identity.EmployeeRole;
import com.example.oulearning.organization.domain.employee.vo.name.FullName;
import com.example.oulearning.organization.domain.employee.vo.name.Name;
import com.example.oulearning.organization.domain.employee.vo.contact.Phone;
import com.example.oulearning.organization.domain.employee.vo.name.Surname;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationTreeException;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import com.example.oulearning.organization.domain.unit.OuName;
import java.io.ByteArrayInputStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.organization.application.port.in.command.UploadEmployeesCommand;
import com.example.oulearning.organization.application.port.in.usecase.employee.UploadEmployeesUseCase;

/**
 * Service orchestrating the batch upload and update of employees from files, linking them to the active organization snapshot.
 */
@Service
@Transactional
public class UploadEmployeesService implements UploadEmployeesUseCase {

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeFileParserPort employeeFileParser;

    public UploadEmployeesService(
            OrganizationRepository organizationRepository,
            EmployeeRepository employeeRepository,
            EmployeeFileParserPort employeeFileParser) {
        this.organizationRepository = Objects.requireNonNull(organizationRepository, "OrganizationRepository cannot be null");
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
        this.employeeFileParser = Objects.requireNonNull(employeeFileParser, "EmployeeFileParserPort cannot be null");
    }

    @Override
    public int execute(UploadEmployeesCommand command) {
        Objects.requireNonNull(command, "UploadEmployeesCommand cannot be null");

        // 1. Verify Manager Authorization (if manager exists)
        if (command.managerCorporateKey() != null && !command.managerCorporateKey().isBlank()) {
            final var managerCk = CorporateKey.of(command.managerCorporateKey().trim());
            final var managerOpt = employeeRepository.findByCorporateKey(managerCk);
            if (managerOpt.isPresent()) {
                final var emp = managerOpt.get();
                if (emp.role() != EmployeeRole.MANAGER && emp.role() != EmployeeRole.ADMIN) {
                    throw new IllegalArgumentException(
                            "Employee '%s' is not authorized to upload employees. Required role: MANAGER"
                                    .formatted(command.managerCorporateKey()));
                }
            }
        }

        // 2. Fetch Active Organization Snapshot
        final var activeOrg = organizationRepository.findLatest()
                .orElseThrow(() -> new NoSuchElementException("Cannot upload employees: no active organization snapshot found"));

        // 3. Parse employee file
        final var empStream = new ByteArrayInputStream(command.employeeFileBytes());
        final var rawEmployees = employeeFileParser.parse(empStream, command.employeeFilename());

        int count = 0;
        for (final var rawEmp : rawEmployees) {
            final var targetOuName = OuName.of(rawEmp.ouName());
            final var ouOpt = activeOrg.findOu(targetOuName);
            if (ouOpt.isEmpty()) {
                throw new InvalidOrganizationTreeException(
                        "OU '%s' not found in active organization hierarchy for employee '%s'"
                                .formatted(rawEmp.ouName(), rawEmp.corporateKey()));
            }

            EmployeeRole role = EmployeeRole.EMPLOYEE;
            if (rawEmp.role() != null && !rawEmp.role().isBlank()) {
                try {
                    role = EmployeeRole.valueOf(rawEmp.role().trim().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    role = EmployeeRole.EMPLOYEE;
                }
            }

            final var employee = Employee.of(
                    CorporateKey.of(rawEmp.corporateKey()),
                    FullName.of(Name.of(rawEmp.firstName()), Surname.of(rawEmp.lastName())),
                    Email.of(rawEmp.email()),
                    rawEmp.phone() != null && !rawEmp.phone().isBlank() ? Phone.of(rawEmp.phone()) : null,
                    role,
                    ouOpt.get().id());

            employeeRepository.save(employee);
            count++;
        }

        return count;
    }
}
