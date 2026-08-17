package com.example.oulearning.organization.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.application.CreateOrganizationSnapshotUseCase;
import com.example.oulearning.organization.application.GetLatestOrganizationUseCase;
import com.example.oulearning.organization.application.GetOrganizationHistoryUseCase;
import com.example.oulearning.organization.application.GetOrganizationSnapshotQuery;
import com.example.oulearning.organization.application.GetOrganizationSnapshotUseCase;
import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.shared.infrastructure.web.GlobalRestControllerAdvice;
import java.time.Instant;
import java.util.List;
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

@WebMvcTest(OrganizationSnapshotRestController.class)
@Import(GlobalRestControllerAdvice.class)
class OrganizationSnapshotRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOrganizationSnapshotUseCase createSnapshotUseCase;

    @MockitoBean
    private GetLatestOrganizationUseCase getLatestUseCase;

    @MockitoBean
    private GetOrganizationSnapshotUseCase getSnapshotUseCase;

    @MockitoBean
    private GetOrganizationHistoryUseCase getHistoryUseCase;

    private Organization createSampleOrg(UUID snapshotId, Instant timestamp) {
        final var rootOu = OrganizationalUnit.leaf(
                OuId.of(UUID.randomUUID()),
                OuName.of("Global Corp"),
                Set.of(CorporateKey.of("CK0001")),
                Set.of());
        return new Organization(SnapshotId.of(snapshotId), rootOu, timestamp);
    }

    @Nested
    @DisplayName("Snapshot Endpoints")
    class SnapshotEndpoints {

        @Test
        @DisplayName("should create snapshot and return 201 Created")
        void should_createSnapshot_successfully() throws Exception {
            final var snapshotId = UUID.randomUUID();
            final var rootOuId = UUID.randomUUID();
            final var organization = createSampleOrg(snapshotId, Instant.now());

            when(createSnapshotUseCase.execute(any())).thenReturn(snapshotId);
            when(getSnapshotUseCase.execute(any(GetOrganizationSnapshotQuery.class))).thenReturn(Optional.of(organization));

            final var requestJson = """
                    {
                        "snapshotId": "%s",
                        "rootOuId": "%s",
                        "createdAt": "2026-08-17T22:00:00Z"
                    }
                    """.formatted(snapshotId, rootOuId);

            mockMvc.perform(post("/api/v1/organizations/snapshots")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/organizations/snapshots/" + snapshotId))
                    .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()))
                    .andExpect(jsonPath("$.rootOu.name").value("Global Corp"));
        }

        @Test
        @DisplayName("should retrieve latest cached snapshot with 200 OK")
        void should_getLatestSnapshot() throws Exception {
            final var snapshotId = UUID.randomUUID();
            final var organization = createSampleOrg(snapshotId, Instant.now());

            when(getLatestUseCase.execute()).thenReturn(Optional.of(organization));

            mockMvc.perform(get("/api/v1/organizations/latest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()));
        }

        @Test
        @DisplayName("should retrieve historical snapshots ordered chronologically")
        void should_getHistory() throws Exception {
            final var org1 = createSampleOrg(UUID.randomUUID(), Instant.now().minusSeconds(100));
            final var org2 = createSampleOrg(UUID.randomUUID(), Instant.now());

            when(getHistoryUseCase.execute()).thenReturn(List.of(org1, org2));

            mockMvc.perform(get("/api/v1/organizations/snapshots/history"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }
}
