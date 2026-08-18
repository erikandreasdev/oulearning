package com.example.oulearning.organization.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import com.example.oulearning.organization.infrastructure.web.response.EmployeeResponse;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class EmployeeRestFlowIT {

    @Container
    static final OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", oracle::getJdbcUrl);
        registry.add("spring.datasource.username", oracle::getUsername);
        registry.add("spring.datasource.password", oracle::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Complete Employee flow: create OU hierarchy -> register employees in parent & child OUs -> query direct vs subtree")
    void should_executeCompleteEmployeeFlow() throws Exception {
        final var rootAreaId = UUID.randomUUID();
        final var childSubareaId = UUID.randomUUID();
        final var rootAreaName = "Engineering Department " + rootAreaId.toString().substring(0, 8);
        final var childSubareaName = "Frontend Team " + childSubareaId.toString().substring(0, 8);

        // 1. Create Child OU
        final var childJson = """
                {
                    "id": "%s",
                    "name": "%s",
                    "ouType": "SUBAREA",
                    "ownerCorporateKeys": ["CK0002"],
                    "parentIds": ["%s"],
                    "childIds": []
                }
                """.formatted(childSubareaId, childSubareaName, rootAreaId);

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(childJson))
                .andExpect(status().isCreated());

        // 2. Create Parent Area OU
        final var parentJson = """
                {
                    "id": "%s",
                    "name": "%s",
                    "ouType": "AREA",
                    "ownerCorporateKeys": ["CK0001"],
                    "parentIds": [],
                    "childIds": ["%s"]
                }
                """.formatted(rootAreaId, rootAreaName, childSubareaId);

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(parentJson))
                .andExpect(status().isCreated());

        // 3. Register Employee 1 (Department Manager in Parent Area)
        final var emp1Key = "CK" + UUID.randomUUID().toString().substring(0, 4);
        final var emp1Json = """
                {
                    "corporateKey": "%s",
                    "firstName": "Ada",
                    "lastName": "Lovelace",
                    "email": "%s@oulearning.com",
                    "phone": "+34911000001",
                    "role": "MANAGER",
                    "ouId": "%s"
                }
                """.formatted(emp1Key, emp1Key.toLowerCase(), rootAreaId);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emp1Json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/employees/" + emp1Key));

        // 4. Register Employee 2 (Software Engineer in Child Subarea)
        final var emp2Key = "CK" + UUID.randomUUID().toString().substring(0, 4);
        final var emp2Json = """
                {
                    "corporateKey": "%s",
                    "firstName": "Alan",
                    "lastName": "Turing",
                    "email": "%s@oulearning.com",
                    "phone": "+34911000002",
                    "role": "EMPLOYEE",
                    "ouId": "%s"
                }
                """.formatted(emp2Key, emp2Key.toLowerCase(), childSubareaId);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emp2Json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/employees/" + emp2Key));

        // 5. Query individual employee by corporate key
        mockMvc.perform(get("/api/v1/employees/{corporateKey}", emp1Key))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.corporateKey").value(emp1Key))
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.ouId").value(rootAreaId.toString()));

        // 6. Query direct employees of Root Area (includeSubtree = false) -> should ONLY return Employee 1
        final var directResult = mockMvc.perform(get("/api/v1/employees/ou/{ouId}", rootAreaId)
                        .param("includeSubtree", "false"))
                .andExpect(status().isOk())
                .andReturn();

        final List<EmployeeResponse> directEmployees = objectMapper.readValue(
                directResult.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertThat(directEmployees).hasSize(1);
        assertThat(directEmployees.get(0).corporateKey()).isEqualTo(emp1Key);

        // 7. Query employees of Root Area WITH subtree (includeSubtree = true) -> should return BOTH Employee 1 and 2
        final var subtreeResult = mockMvc.perform(get("/api/v1/employees/ou/{ouId}", rootAreaId)
                        .param("includeSubtree", "true"))
                .andExpect(status().isOk())
                .andReturn();

        final List<EmployeeResponse> subtreeEmployees = objectMapper.readValue(
                subtreeResult.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertThat(subtreeEmployees).hasSize(2);
        assertThat(subtreeEmployees.stream().map(EmployeeResponse::corporateKey))
                .containsExactlyInAnyOrder(emp1Key, emp2Key);

        // 8. Query employees by OU name WITH subtree (includeSubtree = true) -> should return BOTH
        final var byNameResult = mockMvc.perform(get("/api/v1/employees")
                        .param("ouName", rootAreaName)
                        .param("includeSubtree", "true"))
                .andExpect(status().isOk())
                .andReturn();

        final List<EmployeeResponse> byNameEmployees = objectMapper.readValue(
                byNameResult.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertThat(byNameEmployees).hasSize(2);
        assertThat(byNameEmployees.stream().map(EmployeeResponse::corporateKey))
                .containsExactlyInAnyOrder(emp1Key, emp2Key);

        // 9. Error case: Register employee to non-existent OU -> 404 Not Found
        final var nonExistentOuId = UUID.randomUUID();
        final var invalidOuEmpJson = """
                {
                    "corporateKey": "CK9999",
                    "firstName": "Non",
                    "lastName": "Existent",
                    "email": "non.existent@oulearning.com",
                    "role": "EMPLOYEE",
                    "ouId": "%s"
                }
                """.formatted(nonExistentOuId);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOuEmpJson))
                .andExpect(status().isNotFound());
    }
}
