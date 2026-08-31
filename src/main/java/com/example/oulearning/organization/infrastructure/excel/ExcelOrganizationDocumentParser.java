package com.example.oulearning.organization.infrastructure.excel;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationImportException;
import com.example.oulearning.organization.application.hierarchy.port.in.model.ParsedEmployeeRecord;
import com.example.oulearning.organization.application.hierarchy.port.out.OrganizationDocumentParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class ExcelOrganizationDocumentParser implements OrganizationDocumentParser {

    private final DataFormatter formatter = new DataFormatter();

    @Override
    public List<ParsedEmployeeRecord> parse(final InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            final var sheet = findEmployeesSheet(workbook);
            return parseSheet(sheet);
        } catch (final OrganizationImportException ex) {
            throw ex;
        } catch (final Exception ex) {
            throw new OrganizationImportException("Failed to parse organization Excel document: %s".formatted(ex.getMessage()), ex);
        }
    }

    private Sheet findEmployeesSheet(final Workbook workbook) {
        if (workbook.getNumberOfSheets() == 0) {
            throw new OrganizationImportException("Excel workbook is empty");
        }
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            final var name = workbook.getSheetName(i);
            if ("employees".equalsIgnoreCase(name.trim())) {
                return workbook.getSheetAt(i);
            }
        }
        return workbook.getSheetAt(0);
    }

    private List<ParsedEmployeeRecord> parseSheet(final Sheet sheet) {
        final var headerRow = findHeaderRow(sheet);
        final var columnMapping = resolveColumnMapping(headerRow);

        final var records = new ArrayList<ParsedEmployeeRecord>();
        for (int rowNum = headerRow.getRowNum() + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            final var row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            final var workerVal = getCellValue(row, columnMapping.workerCol());
            if (workerVal.isEmpty()) {
                continue;
            }
            final var emailVal = getCellValue(row, columnMapping.emailCol());
            if (emailVal.isEmpty()) {
                throw new OrganizationImportException("Email is required for worker: %s at row %d".formatted(workerVal, rowNum + 1));
            }
            final var managerVal = getCellValue(row, columnMapping.managerCol());
            final var isManager = "yes".equalsIgnoreCase(managerVal);

            final String name;
            final String surname;
            final var spaceIndex = workerVal.indexOf(' ');
            if (spaceIndex > 0) {
                name = workerVal.substring(0, spaceIndex).trim();
                surname = workerVal.substring(spaceIndex + 1).trim();
            } else {
                name = workerVal;
                surname = workerVal;
            }

            final var hierarchyPath = new ArrayList<String>();
            for (final var colIdx : columnMapping.hierarchyCols()) {
                final var val = getCellValue(row, colIdx);
                if (!val.isEmpty()) {
                    hierarchyPath.add(val);
                }
            }

            records.add(new ParsedEmployeeRecord(name, surname, emailVal, isManager, hierarchyPath));
        }

        if (records.isEmpty()) {
            throw new OrganizationImportException("No employee records found in Excel sheet");
        }
        return List.copyOf(records);
    }

    private Row findHeaderRow(final Sheet sheet) {
        for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 10); i++) {
            final var row = sheet.getRow(i);
            if (row != null) {
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    final var cellVal = getCellValue(row, c).toLowerCase();
                    if (cellVal.contains("worker") || cellVal.contains("employee")) {
                        return row;
                    }
                }
            }
        }
        throw new OrganizationImportException("Could not find header row in Excel sheet");
    }

    private ColumnMapping resolveColumnMapping(final Row headerRow) {
        var workerCol = -1;
        var emailCol = -1;
        var managerCol = -1;
        final var hierarchyCols = new ArrayList<Integer>();

        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            final var header = getCellValue(headerRow, c).toLowerCase();
            if (header.isEmpty()) {
                continue;
            }
            if (header.contains("worker is manager") || "manager".equals(header) || "is manager".equals(header)) {
                managerCol = c;
            } else if (header.contains("email")) {
                emailCol = c;
            } else if (header.contains("worker") && !header.contains("worker type")) {
                workerCol = c;
            } else if (header.contains("hlm") || header.contains("hierarchy")) {
                if (!header.contains("supervisory")) {
                    hierarchyCols.add(c);
                }
            }
        }

        if (workerCol < 0) {
            throw new OrganizationImportException("Required 'Worker' header column not found in Excel sheet");
        }
        if (emailCol < 0) {
            throw new OrganizationImportException("Required 'Email' header column not found in Excel sheet");
        }

        return new ColumnMapping(workerCol, emailCol, managerCol, List.copyOf(hierarchyCols));
    }

    private String getCellValue(final Row row, final int colIndex) {
        if (colIndex < 0) {
            return "";
        }
        final var cell = row.getCell(colIndex);
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }

    private record ColumnMapping(int workerCol, int emailCol, int managerCol, List<Integer> hierarchyCols) {}
}
