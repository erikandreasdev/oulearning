package com.example.oulearning.organization.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class OrganizationUploadRestFlowIT {

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
    @DisplayName("Complete Multipart Upload Flow: upload CSV snapshot -> verify ACTIVE -> upload Excel snapshot -> verify previous ARCHIVED & new ACTIVE")
    void should_executeCompleteMultipartUploadFlow() throws Exception {
        // 1. Prepare CSV Organization and Employee files
        final var orgCsv = """
                name,parent,type,owners
                Global Corp,,ORGANIZATION,CK0001
                Engineering Dept,Global Corp,DEPARTMENT,CK0001
                DevOps Team,Engineering Dept,TEAM,CK0002
                """;

        final var empCsv = """
                corporateKey,firstName,lastName,email,phone,role,ouName
                CK0001,Alice,Director,alice@corp.com,+34600111222,MANAGER,Global Corp
                CK0002,Bob,Engineer,bob@corp.com,+34600333444,EMPLOYEE,DevOps Team
                """;

        final var orgFile = new MockMultipartFile(
                "file", "organization.csv", "text/csv", orgCsv.getBytes(StandardCharsets.UTF_8));
        final var empFile = new MockMultipartFile(
                "employeeFile", "employees.csv", "text/csv", empCsv.getBytes(StandardCharsets.UTF_8));

        // 2. Upload first Snapshot (CSV)
        final var upload1Result = mockMvc.perform(multipart("/api/v1/organizations/snapshots/upload")
                        .file(orgFile)
                        .file(empFile)
                        .param("managerCorporateKey", "CK0001"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.rootOu.name").value("Global Corp"))
                .andExpect(jsonPath("$.totalOusCount").value(3))
                .andReturn();

        final var snapshot1Response = objectMapper.readTree(upload1Result.getResponse().getContentAsString());
        final var snapshot1Id = snapshot1Response.get("snapshotId").asText();

        // 3. Verify Latest is Snapshot 1 and ACTIVE
        mockMvc.perform(get("/api/v1/organizations/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshotId").value(snapshot1Id))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // 4. Verify employees were created and linked to OUs
        mockMvc.perform(get("/api/v1/employees/CK0002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@corp.com"));

        // 5. Prepare Excel Organization file for second snapshot
        final var workbook = new XSSFWorkbook();
        final var sheet = workbook.createSheet("OUs");

        final var header = sheet.createRow(0);
        header.createCell(0).setCellValue("name");
        header.createCell(1).setCellValue("parent");
        header.createCell(2).setCellValue("type");
        header.createCell(3).setCellValue("owners");

        final var r1 = sheet.createRow(1);
        r1.createCell(0).setCellValue("Global Corp V2");
        r1.createCell(1).setCellValue("");
        r1.createCell(2).setCellValue("ORGANIZATION");
        r1.createCell(3).setCellValue("CK0001");

        final var r2 = sheet.createRow(2);
        r2.createCell(0).setCellValue("Innovation Hub");
        r2.createCell(1).setCellValue("Global Corp V2");
        r2.createCell(2).setCellValue("DEPARTMENT");
        r2.createCell(3).setCellValue("CK0001");

        final var excelOut = new ByteArrayOutputStream();
        workbook.write(excelOut);
        workbook.close();

        final var excelFile = new MockMultipartFile(
                "file", "organization_v2.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelOut.toByteArray());

        // 6. Upload second Snapshot (Excel) by authorized Manager CK0001
        final var upload2Result = mockMvc.perform(multipart("/api/v1/organizations/snapshots/upload")
                        .file(excelFile)
                        .param("managerCorporateKey", "CK0001"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.rootOu.name").value("Global Corp V2"))
                .andExpect(jsonPath("$.totalOusCount").value(2))
                .andReturn();

        final var snapshot2Response = objectMapper.readTree(upload2Result.getResponse().getContentAsString());
        final var snapshot2Id = snapshot2Response.get("snapshotId").asText();

        // 7. Verify Latest is now Snapshot 2 and ACTIVE
        mockMvc.perform(get("/api/v1/organizations/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshotId").value(snapshot2Id))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.rootOu.name").value("Global Corp V2"));

        // 8. Verify Snapshot 1 is now ARCHIVED for audit purposes
        mockMvc.perform(get("/api/v1/organizations/snapshots/{snapshotId}", snapshot1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snapshotId").value(snapshot1Id))
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        // 9. Upload batch employees to active snapshot via /api/v1/employees/upload
        final var batchEmpCsv = """
                corporateKey,firstName,lastName,email,phone,role,ouName
                CK0099,David,Innovator,david@corp.com,+34600999888,EMPLOYEE,Innovation Hub
                """;
        final var batchEmpFile = new MockMultipartFile(
                "file", "new_employees.csv", "text/csv", batchEmpCsv.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/employees/upload")
                        .file(batchEmpFile)
                        .param("managerCorporateKey", "CK0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importedCount").value(1));

        // 10. Verify new employee assigned to Innovation Hub
        mockMvc.perform(get("/api/v1/employees/CK0099"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("David"));
    }
}
