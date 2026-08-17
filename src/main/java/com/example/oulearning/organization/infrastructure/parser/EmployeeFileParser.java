package com.example.oulearning.organization.infrastructure.parser;

import com.example.oulearning.organization.domain.organization.exception.InvalidFileFormatException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.example.oulearning.organization.application.port.out.EmployeeFileParserPort;
import org.springframework.stereotype.Component;

/**
 * Infrastructure parser for converting uploaded CSV or Excel employee lists into raw employee rows.
 */
@Component
public class EmployeeFileParser implements EmployeeFileParserPort {

    private final DataFormatter dataFormatter = new DataFormatter();

    @Override
    public List<ParsedEmployee> parse(InputStream inputStream, String filename) {
        Objects.requireNonNull(inputStream, "InputStream cannot be null");
        if (filename == null || filename.isBlank()) {
            throw new InvalidFileFormatException("Filename cannot be null or blank");
        }

        final var lower = filename.toLowerCase();
        if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
            return parseExcel(inputStream);
        } else if (lower.endsWith(".csv") || lower.endsWith(".txt")) {
            return parseCsv(inputStream);
        } else {
            throw new InvalidFileFormatException("Unsupported file extension '%s'. Allowed: .csv, .xlsx, .xls".formatted(filename));
        }
    }

    private List<ParsedEmployee> parseCsv(InputStream inputStream) {
        final var rows = new ArrayList<ParsedEmployee>();
        try (final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new InvalidFileFormatException("Uploaded employee CSV file is empty");
            }

            final var delimiter = headerLine.contains(";") ? ";" : ",";
            final var headers = Arrays.stream(headerLine.split(delimiter))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            final int ckIdx = findHeaderIndex(headers, "corporate_key", "corporatekey", "ck", "id");
            final int firstIdx = findHeaderIndex(headers, "first_name", "firstname", "name");
            final int lastIdx = findHeaderIndex(headers, "last_name", "lastname", "surname");
            final int emailIdx = findHeaderIndex(headers, "email", "mail");
            final int phoneIdx = findHeaderIndex(headers, "phone", "mobile", "telephone");
            final int roleIdx = findHeaderIndex(headers, "role", "position");
            final int ouIdx = findHeaderIndex(headers, "ou_name", "ouname", "ou", "department", "unit");

            if (ckIdx < 0 || firstIdx < 0 || lastIdx < 0 || emailIdx < 0 || ouIdx < 0) {
                throw new InvalidFileFormatException(
                        "Employee CSV must contain columns: 'corporateKey', 'firstName', 'lastName', 'email', 'ouName'");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                final var tokens = line.split(delimiter, -1);
                final var ck = getToken(tokens, ckIdx);
                final var first = getToken(tokens, firstIdx);
                final var last = getToken(tokens, lastIdx);
                final var email = getToken(tokens, emailIdx);
                final var phone = getToken(tokens, phoneIdx);
                final var role = getToken(tokens, roleIdx);
                final var ouName = getToken(tokens, ouIdx);

                if (ck == null || ck.isBlank()) continue;

                rows.add(new ParsedEmployee(ck, first, last, email, phone, role, ouName));
            }
        } catch (InvalidFileFormatException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFileFormatException("Failed to parse employee CSV: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            throw new InvalidFileFormatException("No valid employee rows found in CSV file");
        }

        return rows;
    }

    private List<ParsedEmployee> parseExcel(InputStream inputStream) {
        final var rows = new ArrayList<ParsedEmployee>();
        try (final var workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new InvalidFileFormatException("Excel workbook has no sheets");
            }
            final var sheet = workbook.getSheetAt(0);
            final var rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                throw new InvalidFileFormatException("Excel sheet is empty");
            }

            final var headerRow = rowIterator.next();
            final var headers = new ArrayList<String>();
            for (final Cell cell : headerRow) {
                headers.add(dataFormatter.formatCellValue(cell).trim().toLowerCase());
            }

            final int ckIdx = findHeaderIndex(headers, "corporate_key", "corporatekey", "ck", "id");
            final int firstIdx = findHeaderIndex(headers, "first_name", "firstname", "name");
            final int lastIdx = findHeaderIndex(headers, "last_name", "lastname", "surname");
            final int emailIdx = findHeaderIndex(headers, "email", "mail");
            final int phoneIdx = findHeaderIndex(headers, "phone", "mobile", "telephone");
            final int roleIdx = findHeaderIndex(headers, "role", "position");
            final int ouIdx = findHeaderIndex(headers, "ou_name", "ouname", "ou", "department", "unit");

            if (ckIdx < 0 || firstIdx < 0 || lastIdx < 0 || emailIdx < 0 || ouIdx < 0) {
                throw new InvalidFileFormatException(
                        "Employee Excel must contain columns: 'corporateKey', 'firstName', 'lastName', 'email', 'ouName'");
            }

            while (rowIterator.hasNext()) {
                final var row = rowIterator.next();
                final var ck = getCellString(row, ckIdx);
                final var first = getCellString(row, firstIdx);
                final var last = getCellString(row, lastIdx);
                final var email = getCellString(row, emailIdx);
                final var phone = getCellString(row, phoneIdx);
                final var role = getCellString(row, roleIdx);
                final var ouName = getCellString(row, ouIdx);

                if (ck == null || ck.isBlank()) continue;

                rows.add(new ParsedEmployee(ck, first, last, email, phone, role, ouName));
            }
        } catch (InvalidFileFormatException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFileFormatException("Failed to parse employee Excel: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            throw new InvalidFileFormatException("No valid employee rows found in Excel sheet");
        }

        return rows;
    }

    private int findHeaderIndex(List<String> headers, String... candidates) {
        for (int i = 0; i < headers.size(); i++) {
            final var h = headers.get(i);
            for (final var c : candidates) {
                if (h.equals(c) || h.replaceAll("[_\\-\\s]", "").equals(c.replaceAll("[_\\-\\s]", ""))) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String getToken(String[] tokens, int index) {
        if (index >= 0 && index < tokens.length) {
            return tokens[index].trim();
        }
        return null;
    }

    private String getCellString(Row row, int index) {
        if (index >= 0) {
            final var cell = row.getCell(index);
            if (cell != null) {
                return dataFormatter.formatCellValue(cell).trim();
            }
        }
        return null;
    }
}
