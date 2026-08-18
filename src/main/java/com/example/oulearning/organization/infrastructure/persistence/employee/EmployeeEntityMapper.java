package com.example.oulearning.organization.infrastructure.persistence.employee;
import com.example.oulearning.organization.domain.employee.vo.contact.Email;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.vo.identity.EmployeeRole;
import com.example.oulearning.organization.domain.employee.vo.name.FullName;
import com.example.oulearning.organization.domain.employee.vo.name.Name;
import com.example.oulearning.organization.domain.employee.vo.contact.Phone;
import com.example.oulearning.organization.domain.employee.vo.name.Surname;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Dedicated mapper between domain {@link Employee} and persistence {@link EmployeeEntity}.
 */
@Component
public class EmployeeEntityMapper {

    public EmployeeEntity toEntity(Employee employee, Long version) {
        if (employee == null) {
            return null;
        }

        final var phoneStr = employee.phone() != null ? employee.phone().value() : null;

        return new EmployeeEntity(
                employee.corporateKey().value(),
                employee.fullName().name().value(),
                employee.fullName().surname().value(),
                employee.email().value(),
                phoneStr,
                employee.role().name(),
                employee.ouId().value().toString(),
                version != null ? version : 0L);
    }

    public Employee toDomain(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }

        final var corporateKey = CorporateKey.of(entity.corporateKey());
        final var name = Name.of(entity.firstName());
        final var surname = Surname.of(entity.lastName());
        final var fullName = FullName.of(name, surname);
        final var email = Email.of(entity.email());
        final var phone = entity.phone() != null && !entity.phone().isBlank()
                ? Phone.of(entity.phone())
                : null;
        final var role = EmployeeRole.fromString(entity.role());
        final var ouId = OuId.of(entity.ouId());

        return Employee.of(corporateKey, fullName, email, phone, role, ouId);
    }
}
