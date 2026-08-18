package com.example.oulearning.organization.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.application.port.in.usecase.unit.CreateOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.port.in.query.GetOrganizationalUnitQuery;
import com.example.oulearning.organization.application.port.in.usecase.unit.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import com.example.oulearning.shared.infrastructure.web.GlobalRestControllerAdvice;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrganizationalUnitRestController.class)
@Import(GlobalRestControllerAdvice.class)
class OrganizationalUnitRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOrganizationalUnitUseCase createUseCase;

    @MockitoBean
    private GetOrganizationalUnitUseCase getUseCase;

    @Nested
    @DisplayName("Create Organizational Unit Endpoints")
    class CreateEndpoints {

        @Test
        @DisplayName("should create unit successfully and return 201 Created with Location header")
        void should_createUnit_successfully() throws Exception {
            final var unitId = UUID.randomUUID();
            final var unit = OrganizationalUnit.leaf(
                    OuId.of(unitId),
                    OuName.of("Engineering"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());

            when(createUseCase.execute(any())).thenReturn(unitId);
            when(getUseCase.execute(any(GetOrganizationalUnitQuery.class))).thenReturn(Optional.of(unit));

            final var requestJson = """
                    {
                        "id": "%s",
                        "name": "Engineering",
                        "ouType": "AREA",
                        "ownerCorporateKeys": ["CK0001"],
                        "parentIds": [],
                        "childIds": []
                    }
                    """.formatted(unitId);

            mockMvc.perform(post("/api/v1/organizational-units")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/organizational-units/" + unitId))
                    .andExpect(jsonPath("$.id").value(unitId.toString()))
                    .andExpect(jsonPath("$.name").value("Engineering"))
                    .andExpect(jsonPath("$.ouType").value("SUBAREA"))
                    .andExpect(jsonPath("$.owners[0]").value("CK0001"));
        }

        @Test
        @DisplayName("should return 400 Bad Request Problem Details when unit name is blank")
        void should_return400_when_nameIsBlank() throws Exception {
            final var requestJson = """
                    {
                        "name": ""
                    }
                    """;

            mockMvc.perform(post("/api/v1/organizational-units")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Validation Failed"))
                    .andExpect(jsonPath("$.violations.name").exists());
        }
    }

    @Nested
    @DisplayName("Get Organizational Unit Endpoints")
    class GetEndpoints {

        @Test
        @DisplayName("should return unit by ID with 200 OK")
        void should_getUnitById() throws Exception {
            final var unitId = UUID.randomUUID();
            final var unit = OrganizationalUnit.leaf(
                    OuId.of(unitId),
                    OuName.of("Sales"),
                    Set.of(CorporateKey.of("CK1000")),
                    Set.of());

            when(getUseCase.execute(any(GetOrganizationalUnitQuery.class))).thenReturn(Optional.of(unit));

            mockMvc.perform(get("/api/v1/organizational-units/{id}", unitId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(unitId.toString()))
                    .andExpect(jsonPath("$.name").value("Sales"));
        }

        @Test
        @DisplayName("should return 404 Not Found Problem Details when unit does not exist")
        void should_return404_when_unitNotFound() throws Exception {
            when(getUseCase.execute(any(GetOrganizationalUnitQuery.class))).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/organizational-units/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Resource Not Found"));
        }
    }
}
