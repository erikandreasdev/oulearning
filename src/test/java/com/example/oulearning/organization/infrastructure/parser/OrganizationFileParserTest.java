package com.example.oulearning.organization.infrastructure.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.organization.exception.InvalidFileFormatException;
import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationTreeException;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationFileParserTest {

    private final OrganizationFileParser parser = new OrganizationFileParser();

    @Nested
    @DisplayName("CSV Parsing")
    class CsvParsing {

        @Test
        @DisplayName("should parse valid CSV into hierarchical tree")
        void should_parseValidCsv() {
            final var csv = """
                    name,parent,type,owners
                    Headquarters,,ORGANIZATION,CK0001
                    Engineering,Headquarters,AREA,CK0001;CK0002
                    Backend Team,Engineering,SUBAREA,CK0003
                    Frontend Team,Engineering,SUBAREA,CK0004
                    Sales,Headquarters,SUBAREA,CK0005
                    """;

            final var root = parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "org.csv");

            assertThat(root.name()).isEqualTo(OuName.of("Headquarters"));
            assertThat(root.type()).isEqualTo(OuType.ORGANIZATION);
            assertThat(root.owners()).containsExactly(CorporateKey.of("CK0001"));
            assertThat(root.loadedChildren()).hasSize(2); // Engineering and Sales

            final var engineering = root.loadedChildren().stream()
                    .filter(c -> c.name().equals(OuName.of("Engineering")))
                    .findFirst()
                    .orElseThrow();

            assertThat(engineering.type()).isEqualTo(OuType.AREA);
            assertThat(engineering.owners()).containsExactlyInAnyOrder(CorporateKey.of("CK0001"), CorporateKey.of("CK0002"));
            assertThat(engineering.loadedChildren()).hasSize(2); // Backend and Frontend
        }

        @Test
        @DisplayName("should throw when CSV has multiple roots")
        void should_throw_whenMultipleRoots() {
            final var csv = """
                    name,parent
                    Root1,
                    Root2,
                    """;

            assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "org.csv"))
                    .isInstanceOf(InvalidOrganizationTreeException.class)
                    .hasMessageContaining("Multiple root OUs");
        }

        @Test
        @DisplayName("should throw when CSV has cycle")
        void should_throw_whenCycleDetected() {
            final var csv = """
                    name,parent
                    Root,
                    A,B
                    B,A
                    """;

            // Root is disconnected or cycle
            assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "org.csv"))
                    .isInstanceOf(InvalidOrganizationTreeException.class);
        }

        @Test
        @DisplayName("should throw when CSV has missing parent")
        void should_throw_whenMissingParent() {
            final var csv = """
                    name,parent
                    Root,
                    A,UnknownParent
                    """;

            assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "org.csv"))
                    .isInstanceOf(InvalidOrganizationTreeException.class)
                    .hasMessageContaining("Parent OU 'UnknownParent' not found");
        }

        @Test
        @DisplayName("should throw when CSV has duplicate OU names")
        void should_throw_whenDuplicateNames() {
            final var csv = """
                    name,parent
                    Root,
                    Engineering,Root
                    Engineering,Root
                    """;

            assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), "org.csv"))
                    .isInstanceOf(InvalidOrganizationTreeException.class)
                    .hasMessageContaining("Duplicate OU name");
        }
    }

    @Nested
    @DisplayName("Excel Parsing")
    class ExcelParsing {

        @Test
        @DisplayName("should parse valid Excel (.xlsx) file into hierarchical tree")
        void should_parseValidExcel() throws Exception {
            final var workbook = new XSSFWorkbook();
            final var sheet = workbook.createSheet("OUs");

            final var header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("parent");
            header.createCell(2).setCellValue("type");
            header.createCell(3).setCellValue("owners");

            final var row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Company Corp");
            row1.createCell(1).setCellValue("");
            row1.createCell(2).setCellValue("ORGANIZATION");
            row1.createCell(3).setCellValue("CK0001");

            final var row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Technology");
            row2.createCell(1).setCellValue("Company Corp");
            row2.createCell(2).setCellValue("AREA");
            row2.createCell(3).setCellValue("CK0002");

            final var out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            final var root = parser.parse(new ByteArrayInputStream(out.toByteArray()), "organization.xlsx");

            assertThat(root.name()).isEqualTo(OuName.of("Company Corp"));
            assertThat(root.loadedChildren()).hasSize(1);
            assertThat(root.loadedChildren().iterator().next().name()).isEqualTo(OuName.of("Technology"));
        }

        @Test
        @DisplayName("should throw when unsupported file extension")
        void should_throw_whenUnsupportedExtension() {
            assertThatThrownBy(() -> parser.parse(new ByteArrayInputStream(new byte[0]), "data.pdf"))
                    .isInstanceOf(InvalidFileFormatException.class)
                    .hasMessageContaining("Unsupported file extension");
        }
    }
}
