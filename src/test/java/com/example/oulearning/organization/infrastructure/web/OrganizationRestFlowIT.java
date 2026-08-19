package com.example.oulearning.organization.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class OrganizationRestFlowIT {

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
    @DisplayName("Complete Organization flow: create leaf units -> create composite unit -> take snapshot -> verify latest cache and history")
    void should_executeCompleteOrganizationFlow() throws Exception {
        final var leaf1Id = UUID.randomUUID();
        final var leaf2Id = UUID.randomUUID();
        final var areaId = UUID.randomUUID();
        final var snapshotId = UUID.randomUUID();

        // 1. Create Leaf Unit 1 (Frontend)
        final var leaf1Json = """
                {
                    "id": "%s",
                    "name": "Frontend Team %s",
                    "ouType": "SUBAREA",
                    "ownerCorporateKeys": ["CK0001"],
                    "parentId": null,
                    "childIds": []
                }
                """.formatted(leaf1Id, leaf1Id.toString().substring(0, 8));

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leaf1Json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(leaf1Id.toString()));

        // 2. Create Leaf Unit 2 (Backend)
        final var leaf2Json = """
                {
                    "id": "%s",
                    "name": "Backend Team %s",
                    "ouType": "SUBAREA",
                    "ownerCorporateKeys": ["CK0002"],
                    "parentId": null,
                    "childIds": []
                }
                """.formatted(leaf2Id, leaf2Id.toString().substring(0, 8));

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(leaf2Json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(leaf2Id.toString()));

        // 3. Create Composite Unit (Engineering Area)
        final var areaJson = """
                {
                    "id": "%s",
                    "name": "Engineering Area %s",
                    "ouType": "AREA",
                    "ownerCorporateKeys": ["CK0099"],
                    "parentId": null,
                    "childIds": ["%s", "%s"]
                }
                """.formatted(areaId, areaId.toString().substring(0, 8), leaf1Id, leaf2Id);

        mockMvc.perform(post("/api/v1/organizational-units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(areaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(areaId.toString()));

        // 4. Retrieve Composite Unit with Subtree Hydration
        mockMvc.perform(get("/api/v1/organizational-units/{id}", areaId)
                        .param("includeSubtree", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(areaId.toString()))
                .andExpect(jsonPath("$.loadedChildren.length()").value(2));

        // 5. Take Organization Snapshot
        final var snapshotJson = """
                {
                    "snapshotId": "%s",
                    "rootOuId": "%s",
                    "createdAt": "2026-08-17T22:30:00Z"
                }
                """.formatted(snapshotId, areaId);

        mockMvc.perform(post("/api/v1/organizations/snapshots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(snapshotJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()));

        // 6. Query Latest Organization (Served from Cache)
        mockMvc.perform(get("/api/v1/organizations/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()))
                .andExpect(jsonPath("$.rootOu.id").value(areaId.toString()))
                .andExpect(jsonPath("$.rootOu.loadedChildren.length()").value(2));

        // 7. Query Snapshot by ID
        mockMvc.perform(get("/api/v1/organizations/snapshots/{snapshotId}", snapshotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()));

        // 8. Query Snapshot History
        mockMvc.perform(get("/api/v1/organizations/snapshots/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }
}
