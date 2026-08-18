package com.example.oulearning.organization.infrastructure.parser;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.organization.exception.InvalidFileFormatException;
import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationTreeException;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.example.oulearning.organization.application.port.out.OrganizationFileParserPort;
import org.springframework.stereotype.Component;

/**
 * Infrastructure parser for converting uploaded CSV or Excel files into an {@link OrganizationalUnit} tree.
 */
@Component
public class OrganizationFileParser implements OrganizationFileParserPort {

    private final DataFormatter dataFormatter = new DataFormatter();

    public record RawOuRow(
            UUID id,
            String name,
            String parentName,
            OuType ouType,
            Set<CorporateKey> owners) {}

    public OrganizationalUnit parse(InputStream inputStream, String filename) {
        Objects.requireNonNull(inputStream, "InputStream cannot be null");
        if (filename == null || filename.isBlank()) {
            throw new InvalidFileFormatException("Filename cannot be null or blank");
        }

        final var lower = filename.toLowerCase();
        final List<RawOuRow> rows;
        if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
            rows = parseExcel(inputStream);
        } else if (lower.endsWith(".csv") || lower.endsWith(".txt")) {
            rows = parseCsv(inputStream);
        } else {
            throw new InvalidFileFormatException("Unsupported file extension '%s'. Allowed: .csv, .xlsx, .xls".formatted(filename));
        }

        return buildTree(rows);
    }

    private List<RawOuRow> parseCsv(InputStream inputStream) {
        final var rows = new ArrayList<RawOuRow>();
        try (final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new InvalidFileFormatException("Uploaded CSV file is empty");
            }

            final var delimiter = headerLine.contains(";") ? ";" : ",";
            final var headers = Arrays.stream(headerLine.split(delimiter))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            final int nameIdx = findHeaderIndex(headers, "name", "ou_name", "ouname");
            final int parentIdx = findHeaderIndex(headers, "parent", "parent_name", "parentname");
            final int typeIdx = findHeaderIndex(headers, "type", "ou_type", "outype");
            final int ownersIdx = findHeaderIndex(headers, "owners", "owner_corporate_keys", "ownercorporatekeys", "owner_keys");
            final int idIdx = findHeaderIndex(headers, "id", "ou_id", "ouid");

            if (nameIdx < 0) {
                throw new InvalidFileFormatException("CSV must contain a 'name' column");
            }

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.isBlank()) continue;

                final var tokens = line.split(delimiter, -1);
                final var name = getToken(tokens, nameIdx);
                if (name == null || name.isBlank()) {
                    continue; // skip blank row
                }

                final var parent = getToken(tokens, parentIdx);
                final var typeStr = getToken(tokens, typeIdx);
                final var ownersStr = getToken(tokens, ownersIdx);
                final var idStr = getToken(tokens, idIdx);

                rows.add(createRawRow(idStr, name, parent, typeStr, ownersStr));
            }
        } catch (InvalidFileFormatException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFileFormatException("Failed to parse CSV file: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            throw new InvalidFileFormatException("No valid OU rows found in CSV file");
        }

        return rows;
    }

    private List<RawOuRow> parseExcel(InputStream inputStream) {
        final var rows = new ArrayList<RawOuRow>();
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

            final int nameIdx = findHeaderIndex(headers, "name", "ou_name", "ouname");
            final int parentIdx = findHeaderIndex(headers, "parent", "parent_name", "parentname");
            final int typeIdx = findHeaderIndex(headers, "type", "ou_type", "outype");
            final int ownersIdx = findHeaderIndex(headers, "owners", "owner_corporate_keys", "ownercorporatekeys", "owner_keys");
            final int idIdx = findHeaderIndex(headers, "id", "ou_id", "ouid");

            if (nameIdx < 0) {
                throw new InvalidFileFormatException("Excel sheet must contain a 'name' column");
            }

            while (rowIterator.hasNext()) {
                final var row = rowIterator.next();
                final var name = getCellString(row, nameIdx);
                if (name == null || name.isBlank()) {
                    continue;
                }

                final var parent = getCellString(row, parentIdx);
                final var typeStr = getCellString(row, typeIdx);
                final var ownersStr = getCellString(row, ownersIdx);
                final var idStr = getCellString(row, idIdx);

                rows.add(createRawRow(idStr, name, parent, typeStr, ownersStr));
            }
        } catch (InvalidFileFormatException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFileFormatException("Failed to parse Excel file: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            throw new InvalidFileFormatException("No valid OU rows found in Excel sheet");
        }

        return rows;
    }

    private RawOuRow createRawRow(String idStr, String name, String parent, String typeStr, String ownersStr) {
        final var uuid = (idStr != null && !idStr.isBlank()) ? UUID.fromString(idStr.trim()) : UUID.randomUUID();
        final var cleanName = name.trim();
        final var cleanParent = (parent != null && !parent.isBlank()) ? parent.trim() : null;

        OuType type = OuType.AREA;
        if (typeStr != null && !typeStr.isBlank()) {
            try {
                type = OuType.valueOf(typeStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                type = cleanParent == null ? OuType.ORGANIZATION : OuType.AREA;
            }
        } else if (cleanParent == null) {
            type = OuType.ORGANIZATION;
        }

        final var owners = new HashSet<CorporateKey>();
        if (ownersStr != null && !ownersStr.isBlank()) {
            final var parts = ownersStr.split("[,;]");
            for (final var part : parts) {
                if (!part.isBlank()) {
                    owners.add(CorporateKey.of(part.trim()));
                }
            }
        }

        return new RawOuRow(uuid, cleanName, cleanParent, type, Set.copyOf(owners));
    }

    private OrganizationalUnit buildTree(List<RawOuRow> rows) {
        // Validate unique names
        final var nameMap = new HashMap<String, RawOuRow>();
        for (final var row : rows) {
            if (nameMap.putIfAbsent(row.name().toLowerCase(), row) != null) {
                throw new InvalidOrganizationTreeException("Duplicate OU name '%s' in file".formatted(row.name()));
            }
        }

        // Identify root (parent is null or empty)
        final var rootRows = rows.stream()
                .filter(r -> r.parentName() == null || r.parentName().isBlank())
                .toList();

        if (rootRows.isEmpty()) {
            throw new InvalidOrganizationTreeException("Organization tree must have at least one root OU with no parent");
        }
        if (rootRows.size() > 1) {
            final var rootNames = rootRows.stream().map(RawOuRow::name).collect(Collectors.joining(", "));
            throw new InvalidOrganizationTreeException("Multiple root OUs found (%s). Exactly 1 root is required".formatted(rootNames));
        }

        final var rootRow = rootRows.get(0);

        // Group children by parent name (lowercase)
        final var childrenByParent = new HashMap<String, List<RawOuRow>>();
        for (final var row : rows) {
            if (row.parentName() != null && !row.parentName().isBlank()) {
                final var parentKey = row.parentName().toLowerCase();
                if (!nameMap.containsKey(parentKey)) {
                    throw new InvalidOrganizationTreeException("Parent OU '%s' not found for child OU '%s'"
                            .formatted(row.parentName(), row.name()));
                }
                childrenByParent.computeIfAbsent(parentKey, k -> new ArrayList<>()).add(row);
            }
        }
        final var rootOu = buildSubtree(rootRow, childrenByParent, Set.of(), new HashSet<>());

        final int treeSize = countTreeNodes(rootOu);
        if (treeSize != rows.size()) {
            throw new InvalidOrganizationTreeException(
                    "Disconnected units or cyclic dependencies detected. Expected %d OUs, but only %d reachable from root"
                            .formatted(rows.size(), treeSize));
        }

        return rootOu;
    }

    private int countTreeNodes(OrganizationalUnit unit) {
        int count = 1;
        for (final var child : unit.loadedChildren()) {
            count += countTreeNodes(child);
        }
        return count;
    }

    private OrganizationalUnit buildSubtree(
            RawOuRow current,
            Map<String, List<RawOuRow>> childrenByParent,
            Set<OuId> parentIds,
            Set<String> visited) {
        if (!visited.add(current.name().toLowerCase())) {
            throw new InvalidOrganizationTreeException("Cycle detected at OU '%s'".formatted(current.name()));
        }

        final var currentOuId = OuId.of(current.id());
        final var childRows = childrenByParent.getOrDefault(current.name().toLowerCase(), List.of());
        final var loadedChildren = new HashSet<OrganizationalUnit>();

        final var nextParentIds = Set.of(currentOuId);
        for (final var childRow : childRows) {
            loadedChildren.add(buildSubtree(childRow, childrenByParent, nextParentIds, new HashSet<>(visited)));
        }

        if (loadedChildren.isEmpty()) {
            return OrganizationalUnit.leaf(
                    currentOuId,
                    OuName.of(current.name()),
                    current.owners(),
                    parentIds);
        } else {
            return OrganizationalUnit.withChildren(
                    currentOuId,
                    OuName.of(current.name()),
                    current.ouType(),
                    current.owners(),
                    parentIds,
                    Set.copyOf(loadedChildren));
        }
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
