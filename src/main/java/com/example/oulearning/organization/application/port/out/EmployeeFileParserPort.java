package com.example.oulearning.organization.application.port.out;

import java.io.InputStream;
import java.util.List;

/**
 * Output port for parsing uploaded employee list files (CSV, Excel) into intermediate employee transfer records.
 */
public interface EmployeeFileParserPort {

    record ParsedEmployee(
            String corporateKey,
            String firstName,
            String lastName,
            String email,
            String phone,
            String role,
            String ouName) {}

    List<ParsedEmployee> parse(InputStream inputStream, String filename);
}
