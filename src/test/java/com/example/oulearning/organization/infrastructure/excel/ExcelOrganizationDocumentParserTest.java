package com.example.oulearning.organization.infrastructure.excel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationImportException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExcelOrganizationDocumentParserTest {

    private final ExcelOrganizationDocumentParser parser = new ExcelOrganizationDocumentParser();

    @Test
    @DisplayName("given valid excel with employee rows, when parsing, then returns parsed records and updates template")
    void givenValidExcelWithEmployeeRows_whenParsing_thenReturnsParsedRecords() throws IOException {
        // given
        final var bytes = createSampleWorkbookBytes();

        try (final var fos = new FileOutputStream("/Users/erik/dev/oulearning/Employee_Hierarchy_Template.xlsx")) {
            fos.write(bytes);
        }

        // when
        final var records = parser.parse(new ByteArrayInputStream(bytes));

        // then
        assertThat(records).hasSize(2);

        final var first = records.get(0);
        assertThat(first.name()).isEqualTo("Jane");
        assertThat(first.surname()).isEqualTo("Doe");
        assertThat(first.email()).isEqualTo("jane.doe@example.com");
        assertThat(first.isManager()).isTrue();
        assertThat(first.hierarchyPath()).containsExactly(
                "Global", "Corporate", "Finance & Ops", "Accounting", "General Accounting", "Level 8", "Level 9");

        final var second = records.get(1);
        assertThat(second.name()).isEqualTo("John");
        assertThat(second.surname()).isEqualTo("Smith");
        assertThat(second.email()).isEqualTo("john.smith@example.com");
        assertThat(second.isManager()).isFalse();
        assertThat(second.hierarchyPath()).containsExactly(
                "Global", "Corporate", "Finance & Ops", "Accounting", "General Accounting");
    }

    @Test
    @DisplayName("given empty workbook, when parsing, then throws organization import exception")
    void givenEmptyWorkbook_whenParsing_thenThrowsException() throws IOException {
        // given
        final byte[] emptyBytes;
        try (Workbook wb = new XSSFWorkbook(); final var out = new ByteArrayOutputStream()) {
            wb.createSheet("Empty");
            wb.write(out);
            emptyBytes = out.toByteArray();
        }

        // when / then
        assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(emptyBytes)))
                .isInstanceOf(OrganizationImportException.class);
    }

    private byte[] createSampleWorkbookBytes() throws IOException {
        try (Workbook wb = new XSSFWorkbook(); final var out = new ByteArrayOutputStream()) {
            final var sheet = wb.createSheet("Employees");
            final var header = sheet.createRow(0);
            header.createCell(0).setCellValue("Employee ID");
            header.createCell(1).setCellValue("Corporate Key");
            header.createCell(2).setCellValue("Worker");
            header.createCell(3).setCellValue("Email");
            header.createCell(4).setCellValue("Worker type");
            header.createCell(5).setCellValue("Weekly hours");
            header.createCell(6).setCellValue("Worker is Manager?");
            header.createCell(7).setCellValue("HLM - Supervisory ID");
            header.createCell(8).setCellValue("HLM - Supervisory Organization");
            header.createCell(9).setCellValue("HLM - Supervisory Manager");
            header.createCell(10).setCellValue("HLM 03 - Hierarchy");
            header.createCell(11).setCellValue("HLM 04 - Hierarchy - Domain");
            header.createCell(12).setCellValue("HLM 05 - Hierarchy -Area");
            header.createCell(13).setCellValue("HLM - 06 Hierarchy - Subarea");
            header.createCell(14).setCellValue("HLM 07 - Hierarchy - Team");
            header.createCell(15).setCellValue("HLM 08 - Hierarchy");
            header.createCell(16).setCellValue("HLM 09 - Hierarchy");
            header.createCell(17).setCellValue("Cost Center");

            final var row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("E10023");
            row1.createCell(1).setCellValue("CK-10023");
            row1.createCell(2).setCellValue("Jane Doe");
            row1.createCell(3).setCellValue("jane.doe@example.com");
            row1.createCell(4).setCellValue("Employee");
            row1.createCell(5).setCellValue("40");
            row1.createCell(6).setCellValue("Yes");
            row1.createCell(7).setCellValue("SUP-045");
            row1.createCell(8).setCellValue("Finance");
            row1.createCell(9).setCellValue("John Smith");
            row1.createCell(10).setCellValue("Global");
            row1.createCell(11).setCellValue("Corporate");
            row1.createCell(12).setCellValue("Finance & Ops");
            row1.createCell(13).setCellValue("Accounting");
            row1.createCell(14).setCellValue("General Accounting");
            row1.createCell(15).setCellValue("Level 8");
            row1.createCell(16).setCellValue("Level 9");
            row1.createCell(17).setCellValue("CC-3001");

            final var row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("E10024");
            row2.createCell(1).setCellValue("CK-10024");
            row2.createCell(2).setCellValue("John Smith");
            row2.createCell(3).setCellValue("john.smith@example.com");
            row2.createCell(4).setCellValue("Employee");
            row2.createCell(5).setCellValue("40");
            row2.createCell(6).setCellValue("No");
            row2.createCell(7).setCellValue("SUP-045");
            row2.createCell(8).setCellValue("Finance");
            row2.createCell(9).setCellValue("John Smith");
            row2.createCell(10).setCellValue("Global");
            row2.createCell(11).setCellValue("Corporate");
            row2.createCell(12).setCellValue("Finance & Ops");
            row2.createCell(13).setCellValue("Accounting");
            row2.createCell(14).setCellValue("General Accounting");
            row2.createCell(17).setCellValue("CC-3001");

            wb.write(out);
            return out.toByteArray();
        }
    }
}
