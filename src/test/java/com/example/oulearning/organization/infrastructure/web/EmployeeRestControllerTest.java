package com.example.oulearning.organization.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.application.GetEmployeeQuery;
import com.example.oulearning.organization.application.GetEmployeeUseCase;
import com.example.oulearning.organization.application.GetEmployeesByOuQuery;
import com.example.oulearning.organization.application.GetEmployeesByOuUseCase;
import com.example.oulearning.organization.application.RegisterEmployeeCommand;
import com.example.oulearning.organization.application.RegisterEmployeeUseCase;
import com.example.oulearning.organization.application.UploadEmployeesCommand;
import com.example.oulearning.organization.application.UploadEmployeesUseCase;
import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Name;
import com.example.oulearning.organization.domain.employee.Phone;
import com.example.oulearning.organization.domain.employee.Surname;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.shared.infrastructure.web.GlobalRestControllerAdvice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeRestController.class)
@Import(GlobalRestControllerAdvice.class)
class EmployeeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterEmployeeUseCase registerEmployeeUseCase;

    @MockitoBean
    private UploadEmployeesUseCase uploadEmployeesUseCase;

    @MockitoBean
    private GetEmployeeUseCase getEmployeeUseCase;

    @MockitoBean
    private GetEmployeesByOuUseCase getEmployeesByOuUseCase;

    @Test
    @DisplayName("should register employee and return 201 Created with Location header")
    void should_registerEmployee_return201() throws Exception {
        when(registerEmployeeUseCase.execute(any(RegisterEmployeeCommand.class))).thenReturn("CK0001");

        final var requestJson = """
                {
                    "corporateKey": "CK0001",
                    "firstName": "Alice",
                    "lastName": "Smith",
                    "email": "alice@example.com",
                    "phone": "+34911223344",
                    "role": "MANAGER",
                    "ouId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
                }
                """;

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/employees/CK0001"));
    }

    @Test
    @DisplayName("should upload employee file and return 200 OK")
    void should_uploadEmployees_return200() throws Exception {
        when(uploadEmployeesUseCase.execute(any(UploadEmployeesCommand.class))).thenReturn(5);

        final var file = new MockMultipartFile("file", "employees.csv", "text/csv", "ck,firstName,lastName,email,role,ouName\nCK0001,Alice,Smith,alice@example.com,MANAGER,Engineering\n".getBytes());

        mockMvc.perform(multipart("/api/v1/employees/upload")
                        .file(file)
                        .param("managerCorporateKey", "CK0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importedCount").value(5));
    }

    @Test
    @DisplayName("should return 400 Bad Request when payload fails validation")
    void should_return400_whenPayloadInvalid() throws Exception {
        final var invalidJson = """
                {
                    "corporateKey": "",
                    "firstName": "",
                    "lastName": "Smith",
                    "email": "not-an-email",
                    "role": "MANAGER",
                    "ouId": null
                }
                """;

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation Failed"));
    }

    @Test
    @DisplayName("should get employee by corporate key and return 200 OK")
    void should_getEmployee_return200() throws Exception {
        final var ouId = UUID.randomUUID();
        final var employee = Employee.of(
                CorporateKey.of("CK0001"),
                FullName.of(Name.of("Alice"), Surname.of("Smith")),
                Email.of("alice@example.com"),
                Phone.of("+34911223344"),
                EmployeeRole.MANAGER,
                OuId.of(ouId));

        when(getEmployeeUseCase.execute(any(GetEmployeeQuery.class))).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/v1/employees/CK0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.corporateKey").value("CK0001"))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.phone").value("+34911223344"))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.ouId").value(ouId.toString()));
    }

    @Test
    @DisplayName("should return 404 Not Found when employee does not exist")
    void should_return404_whenEmployeeNotFound() throws Exception {
        when(getEmployeeUseCase.execute(any(GetEmployeeQuery.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employees/CK9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should get employees by OU ID with subtree flag and return 200 OK")
    void should_getEmployeesByOuId_return200() throws Exception {
        final var ouId = UUID.randomUUID();
        final var e1 = Employee.of(
                CorporateKey.of("CK0001"),
                FullName.of(Name.of("Alice"), Surname.of("Smith")),
                Email.of("alice@example.com"),
                null,
                EmployeeRole.MANAGER,
                OuId.of(ouId));

        when(getEmployeesByOuUseCase.execute(any(GetEmployeesByOuQuery.class))).thenReturn(List.of(e1));

        mockMvc.perform(get("/api/v1/employees/ou/{ouId}", ouId).param("includeSubtree", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].corporateKey").value("CK0001"))
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    @DisplayName("should get employees by OU name and return 200 OK")
    void should_getEmployeesByOuName_return200() throws Exception {
        final var ouId = UUID.randomUUID();
        final var e1 = Employee.of(
                CorporateKey.of("CK0001"),
                FullName.of(Name.of("Alice"), Surname.of("Smith")),
                Email.of("alice@example.com"),
                null,
                EmployeeRole.MANAGER,
                OuId.of(ouId));

        when(getEmployeesByOuUseCase.execute(any(GetEmployeesByOuQuery.class))).thenReturn(List.of(e1));

        mockMvc.perform(get("/api/v1/employees").param("ouName", "Engineering Area"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].corporateKey").value("CK0001"));
    }
}
