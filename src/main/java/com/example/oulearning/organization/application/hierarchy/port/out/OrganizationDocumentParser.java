package com.example.oulearning.organization.application.hierarchy.port.out;

import com.example.oulearning.organization.application.hierarchy.port.in.model.ParsedEmployeeRecord;
import java.io.InputStream;
import java.util.List;

public interface OrganizationDocumentParser {
    List<ParsedEmployeeRecord> parse(InputStream inputStream);
}
