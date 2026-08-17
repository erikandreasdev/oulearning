package com.example.oulearning.organization.application.port.out;

import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import java.io.InputStream;

/**
 * Output port for parsing uploaded organization hierarchy files (CSV, Excel) into an {@link OrganizationalUnit} tree.
 */
public interface OrganizationFileParserPort {

    OrganizationalUnit parse(InputStream inputStream, String filename);
}
