package com.example.oulearning.organization.application.service.snapshot;
import com.example.oulearning.organization.domain.employee.vo.contact.Email;

import com.example.oulearning.organization.application.port.out.EmployeeFileParserPort;
import com.example.oulearning.organization.application.port.out.OrganizationFileParserPort;
import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.vo.identity.EmployeeRole;
import com.example.oulearning.organization.domain.employee.vo.name.FullName;
import com.example.oulearning.organization.domain.employee.vo.name.Name;
import com.example.oulearning.organization.domain.employee.vo.contact.Phone;
import com.example.oulearning.organization.domain.employee.vo.name.Surname;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationTreeException;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import com.example.oulearning.organization.domain.unit.OuName;
import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.organization.application.port.in.command.UploadOrganizationSnapshotCommand;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.UploadOrganizationSnapshotUseCase;

/**
 * Service orchestrating the upload, parsing, validation, and activation of an organization hierarchy snapshot from files.
 */
@Service
@Transactional
public class UploadOrganizationSnapshotService implements UploadOrganizationSnapshotUseCase {

    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganizationFileParserPort organizationFileParser;
    private final EmployeeFileParserPort employeeFileParser;
    private final Clock clock;

    public UploadOrganizationSnapshotService(
            OrganizationRepository organizationRepository,
            EmployeeRepository employeeRepository,
            OrganizationFileParserPort organizationFileParser,
            EmployeeFileParserPort employeeFileParser,
            Clock clock) {
        this.organizationRepository = Objects.requireNonNull(organizationRepository, "OrganizationRepository cannot be null");
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
        this.organizationFileParser = Objects.requireNonNull(organizationFileParser, "OrganizationFileParserPort cannot be null");
        this.employeeFileParser = Objects.requireNonNull(employeeFileParser, "EmployeeFileParserPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    @Override
    public UUID execute(UploadOrganizationSnapshotCommand command) {
        Objects.requireNonNull(command, "UploadOrganizationSnapshotCommand cannot be null");

        // 1. Verify Manager authorization (if manager exists in repository)
        if (command.managerCorporateKey() != null && !command.managerCorporateKey().isBlank()) {
            final var managerCk = CorporateKey.of(command.managerCorporateKey().trim());
            final var managerOpt = employeeRepository.findByCorporateKey(managerCk);
            if (managerOpt.isPresent()) {
                final var emp = managerOpt.get();
                if (emp.role() != EmployeeRole.MANAGER && emp.role() != EmployeeRole.ADMIN) {
                    throw new IllegalArgumentException(
                            "Employee '%s' is not authorized to upload organization snapshots. Required role: MANAGER"
                                    .formatted(command.managerCorporateKey()));
                }
            }
        }

        // 2. Parse Organization File
        final var orgStream = new ByteArrayInputStream(command.organizationFileBytes());
        final var rootOu = organizationFileParser.parse(orgStream, command.organizationFilename());

        // 3. Create active Organization aggregate
        final var snapshotId = SnapshotId.of(UUID.randomUUID());
        final var createdAt = Instant.now(clock);
        final var organization = Organization.active(snapshotId, rootOu, createdAt);

        // 4. Save and activate snapshot (persister automatically archives previous snapshots)
        organizationRepository.save(organization);

        // 5. Parse and save optional employee file
        if (command.employeeFileBytes() != null && command.employeeFileBytes().length > 0) {
            final var empStream = new ByteArrayInputStream(command.employeeFileBytes());
            final var rawEmployees = employeeFileParser.parse(empStream, command.employeeFilename());

            for (final var rawEmp : rawEmployees) {
                final var targetOuName = OuName.of(rawEmp.ouName());
                final var ouOpt = organization.findOu(targetOuName);
                if (ouOpt.isEmpty()) {
                    throw new InvalidOrganizationTreeException(
                            "OU '%s' not found in organization hierarchy for employee '%s'"
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
            }
        }

        return organization.snapshotId().value();
    }
}
