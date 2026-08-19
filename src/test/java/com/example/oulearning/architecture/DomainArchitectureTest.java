package com.example.oulearning.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
        packages = "com.example.oulearning",
        importOptions = {ImportOption.DoNotIncludeTests.class})
class DomainArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_frameworks =
            noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "jakarta.validation..",
                            "com.fasterxml.jackson..",
                            "org.hibernate..",
                            "lombok..");

    @ArchTest
    static final ArchRule organization_domain_should_not_depend_on_other_contexts =
            noClasses()
                    .that()
                    .resideInAPackage("..organization.domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..budgeting.domain..", "..training.domain..");

    @ArchTest
    static final ArchRule budgeting_domain_should_not_depend_on_training =
            noClasses()
                    .that()
                    .resideInAPackage("..budgeting.domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..training.domain..");
}
