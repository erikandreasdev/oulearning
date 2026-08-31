package com.example.oulearning.organization.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.application.hierarchy.port.in.model.ImportOrganizationResult;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.ImportOrganizationUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.ListOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.model.FullName;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerIT {

    private static final String IMPORT_ENDPOINT = "/api/v1/organization/import";
    private static final String UNITS_ENDPOINT = "/api/v1/organization/units";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImportOrganizationUseCase importOrganizationUseCase;

    @MockitoBean
    private ListOrganizationalUnitsUseCase listOrganizationalUnitsUseCase;

    @Test
    @DisplayName("given valid multipart file, when importing organization, then returns 200 with summary counts and saved data")
    void givenValidMultipartFile_whenImportingOrganization_thenReturns200WithSummaryCountsAndSavedData() throws Exception {
        // given
        final var mockFile = new MockMultipartFile(
                "file", "organization.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "test content".getBytes());
        final var employee = Employee.create(
                new EmployeeId(10L),
                FullName.of("Jane", "Doe"),
                Email.of("jane.doe@example.com"));
        final var unit = OrganizationalUnit.of(
                new OrganizationalUnitId(1L),
                Name.of("Global"),
                null,
                Set.of(),
                Set.of(new EmployeeId(10L)),
                Set.of(new EmployeeId(10L)));
        final var result = new ImportOrganizationResult(1, 1, 1, 1, List.of(employee), List.of(unit));
        given(importOrganizationUseCase.execute(any(InputStream.class))).willReturn(result);

        // when
        final var response = mockMvc.perform(multipart(IMPORT_ENDPOINT).file(mockFile));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.employeesProcessed").value(1))
                .andExpect(jsonPath("$.organizationalUnitsProcessed").value(1))
                .andExpect(jsonPath("$.ownersAssigned").value(1))
                .andExpect(jsonPath("$.membersAssigned").value(1))
                .andExpect(jsonPath("$.employees[0].id").value(10))
                .andExpect(jsonPath("$.employees[0].name").value("Jane"))
                .andExpect(jsonPath("$.employees[0].surname").value("Doe"))
                .andExpect(jsonPath("$.employees[0].email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.employees[0].active").value(true))
                .andExpect(jsonPath("$.organizationalUnits[0].id").value(1))
                .andExpect(jsonPath("$.organizationalUnits[0].name").value("Global"))
                .andExpect(jsonPath("$.organizationalUnits[0].owners[0]").value(10))
                .andExpect(jsonPath("$.organizationalUnits[0].members[0]").value(10));
    }

    @Test
    @DisplayName("given empty multipart file, when importing organization, then returns 400 bad request")
    void givenEmptyMultipartFile_whenImportingOrganization_thenReturns400BadRequest() throws Exception {
        // given
        final var emptyFile = new MockMultipartFile(
                "file", "empty.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        // when
        final var response = mockMvc.perform(multipart(IMPORT_ENDPOINT).file(emptyFile));

        // then
        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("given existing organizational units, when listing units, then returns 200 with list of units")
    void givenExistingOrganizationalUnits_whenListingUnits_thenReturns200WithListOfUnits() throws Exception {
        // given
        final var unit1 = OrganizationalUnit.of(
                new OrganizationalUnitId(1L),
                Name.of("Global"),
                null,
                Set.of(new OrganizationalUnitId(2L)),
                Set.of(),
                Set.of());
        final var unit2 = OrganizationalUnit.of(
                new OrganizationalUnitId(2L),
                Name.of("Corporate"),
                new OrganizationalUnitId(1L),
                Set.of(),
                Set.of(new EmployeeId(101L)),
                Set.of(new EmployeeId(101L), new EmployeeId(102L)));
        given(listOrganizationalUnitsUseCase.execute()).willReturn(List.of(unit1, unit2));

        // when
        final var response = mockMvc.perform(get(UNITS_ENDPOINT));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Global"))
                .andExpect(jsonPath("$[0].parentId").doesNotExist())
                .andExpect(jsonPath("$[0].childIds[0]").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Corporate"))
                .andExpect(jsonPath("$[1].parentId").value(1))
                .andExpect(jsonPath("$[1].owners[0]").value(101));
    }
}
