package com.example.oulearning.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
        packages = "com.example.oulearning",
        importOptions = {ImportOption.DoNotIncludeTests.class})
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application_or_infrastructure = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..application..", "..infrastructure..");

    @ArchTest
    static final ArchRule application_should_not_depend_on_infrastructure = noClasses()
            .that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domain_should_not_use_spring_annotations = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule controllers_should_only_reside_in_web_controller = classes()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .resideInAPackage("..infrastructure.web.controller..");

    @ArchTest
    static final ArchRule use_cases_should_reside_in_application_port_in = classes()
            .that()
            .haveSimpleNameEndingWith("UseCase")
            .should()
            .resideInAPackage("..application.port.in.usecase..");

    @ArchTest
    static final ArchRule commands_should_reside_in_application_port_in_command = classes()
            .that()
            .haveSimpleNameEndingWith("Command")
            .should()
            .resideInAPackage("..application.port.in.command..");

    @ArchTest
    static final ArchRule queries_should_reside_in_application_port_in_query = classes()
            .that()
            .haveSimpleNameEndingWith("Query")
            .should()
            .resideInAPackage("..application.port.in.query..");

    @ArchTest
    static final ArchRule services_in_application_should_reside_in_application_service = classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .and()
            .resideInAPackage("..application..")
            .should()
            .resideInAPackage("..application.service..");

    @ArchTest
    static final ArchRule repositories_should_be_named_correctly = classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .resideInAnyPackage("..domain..", "..infrastructure.persistence..");

    @ArchTest
    static final ArchRule entities_should_reside_in_infrastructure_persistence = classes()
            .that()
            .haveSimpleNameEndingWith("Entity")
            .should()
            .resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule responses_should_reside_in_web_response = classes()
            .that()
            .haveSimpleNameEndingWith("Response")
            .should()
            .resideInAPackage("..infrastructure.web.response..");
}
