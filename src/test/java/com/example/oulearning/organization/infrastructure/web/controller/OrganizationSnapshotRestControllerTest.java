package com.example.oulearning.organization.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.application.port.in.usecase.snapshot.CreateOrganizationSnapshotUseCase;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.GetLatestOrganizationUseCase;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.GetOrganizationHistoryUseCase;
import com.example.oulearning.organization.application.port.in.query.GetOrganizationSnapshotQuery;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.GetOrganizationSnapshotUseCase;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.UploadOrganizationSnapshotUseCase;
import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.SnapshotStatus;
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
import org.springframework.mock.web.MockMultipartFile;
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
    private UploadOrganizationSnapshotUseCase uploadSnapshotUseCase;

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
        return new Organization(SnapshotId.of(snapshotId), rootOu, SnapshotStatus.ACTIVE, timestamp);
    }

    @Nested
    @DisplayName("Snapshot Endpoints")
    class SnapshotEndpoints {

        @Test
        @DisplayName("should create snapshot from JSON and return 201 Created")
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
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.rootOu.name").value("Global Corp"));
        }

        @Test
        @DisplayName("should upload multipart snapshot file and return 201 Created")
        void should_uploadSnapshot_successfully() throws Exception {
            final var snapshotId = UUID.randomUUID();
            final var organization = createSampleOrg(snapshotId, Instant.now());

            when(uploadSnapshotUseCase.execute(any())).thenReturn(snapshotId);
            when(getSnapshotUseCase.execute(any(GetOrganizationSnapshotQuery.class))).thenReturn(Optional.of(organization));

            final var file = new MockMultipartFile("file", "org.csv", "text/csv", "name,parent\nCEO,\nEngineering,CEO\n".getBytes());

            mockMvc.perform(multipart("/api/v1/organizations/snapshots/upload")
                            .file(file)
                            .param("managerCorporateKey", "CK0001"))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/organizations/snapshots/" + snapshotId))
                    .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should retrieve latest cached snapshot with 200 OK")
        void should_getLatestSnapshot() throws Exception {
            final var snapshotId = UUID.randomUUID();
            final var organization = createSampleOrg(snapshotId, Instant.now());

            when(getLatestUseCase.execute()).thenReturn(Optional.of(organization));

            mockMvc.perform(get("/api/v1/organizations/latest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.snapshotId").value(snapshotId.toString()))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
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
