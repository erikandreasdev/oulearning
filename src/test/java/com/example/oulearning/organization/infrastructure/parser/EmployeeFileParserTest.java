package com.example.oulearning.organization.infrastructure.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.organization.exception.InvalidFileFormatException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeFileParserTest {

    private final EmployeeFileParser parser = new EmployeeFileParser();

    @Nested
    @DisplayName("CSV Parsing")
    class CsvParsing {

        @Test
        @DisplayName("should parse valid employee CSV")
        void should_parseValidCsv() {
            final var csv = """
                    corporateKey,firstName,lastName,email,phone,role,ouName
                    CK0001,Alice,Smith,alice@example.com,+34911223344,MANAGER,Engineering
                    CK0002,Bob,Jones,bob@example.com,,EMPLOYEE,Engineering
                    """;

            final var list = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "employees.csv");

            assertThat(list).hasSize(2);
            assertThat(list.get(0).corporateKey()).isEqualTo("CK0001");
            assertThat(list.get(0).firstName()).isEqualTo("Alice");
            assertThat(list.get(0).lastName()).isEqualTo("Smith");
            assertThat(list.get(0).role()).isEqualTo("MANAGER");
            assertThat(list.get(0).ouName()).isEqualTo("Engineering");

            assertThat(list.get(1).corporateKey()).isEqualTo("CK0002");
            assertThat(list.get(1).firstName()).isEqualTo("Bob");
            assertThat(list.get(1).role()).isEqualTo("EMPLOYEE");
        }

        @Test
        @DisplayName("should parse sample 100 employees CSV file")
        void should_parseSample100EmployeesCsv() throws Exception {
            final var file = new java.io.File("bruno/samples/employees-100.csv");
            try (final var in = new java.io.FileInputStream(file)) {
                final var list = parser.parse(in, file.getName());
                assertThat(list).hasSize(100);
            }
        }

        @Test
        @DisplayName("should parse sample 5000 employees CSV file")
        void should_parseSample5000EmployeesCsv() throws Exception {
            final var file = new java.io.File("bruno/samples/employees-5000.csv");
            try (final var in = new java.io.FileInputStream(file)) {
                final var list = parser.parse(in, file.getName());
                assertThat(list).hasSize(5000);
            }
        }

        @Test
        @DisplayName("should throw when mandatory column is missing")
        void should_throw_whenMissingColumn() {
            final var csv = """
                    corporateKey,firstName,lastName
                    CK0001,Alice,Smith
                    """;

            assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "employees.csv"))
                    .isInstanceOf(InvalidFileFormatException.class)
                    .hasMessageContaining("must contain columns");
        }
    }

    @Nested
    @DisplayName("Excel Parsing")
    class ExcelParsing {

        @Test
        @DisplayName("should parse valid employee Excel (.xlsx) file")
        void should_parseValidExcel() throws Exception {
            final var workbook = new XSSFWorkbook();
            final var sheet = workbook.createSheet("Employees");

            final var header = sheet.createRow(0);
            header.createCell(0).setCellValue("corporateKey");
            header.createCell(1).setCellValue("firstName");
            header.createCell(2).setCellValue("lastName");
            header.createCell(3).setCellValue("email");
            header.createCell(4).setCellValue("phone");
            header.createCell(5).setCellValue("role");
            header.createCell(6).setCellValue("ouName");

            final var row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("CK0001");
            row1.createCell(1).setCellValue("Carol");
            row1.createCell(2).setCellValue("White");
            row1.createCell(3).setCellValue("carol@example.com");
            row1.createCell(4).setCellValue("+1234567890");
            row1.createCell(5).setCellValue("MANAGER");
            row1.createCell(6).setCellValue("Operations");

            final var out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            final var list = parser.parse(new ByteArrayInputStream(out.toByteArray()), "employees.xlsx");

            assertThat(list).hasSize(1);
            assertThat(list.get(0).corporateKey()).isEqualTo("CK0001");
            assertThat(list.get(0).firstName()).isEqualTo("Carol");
            assertThat(list.get(0).ouName()).isEqualTo("Operations");
        }
    }
}
