package com.example.oulearning.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

@AnalyzeClasses(
        packages = "com.example.oulearning",
        importOptions = {ImportOption.DoNotIncludeTests.class})
class DomainArchitectureTest {

    @ArchTest
    void givenDomainClasses_whenCheckingFrameworkDependencies_thenDomainMustNotDependOnFrameworks(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "jakarta.validation..",
                        "jakarta.enterprise..",
                        "jakarta.inject..",
                        "com.fasterxml.jackson..",
                        "org.hibernate..",
                        "lombok..")
                .as("Domain layer must be pure Java and not depend on external frameworks");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingOuterLayerDependencies_thenDomainMustNotDependOnOuterLayers(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..application..", "..infrastructure..", "..config..", "..bootstrap..")
                .as("Domain layer must not depend on application, infrastructure, or bootstrap layers");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenApplicationClasses_whenCheckingInfrastructureDependencies_thenApplicationMustNotDependOnInfrastructure(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..application..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "..config..", "..bootstrap..")
                .allowEmptyShould(true)
                .as("Application layer must not depend on infrastructure or bootstrap adapters");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenOrganizationContext_whenCheckingDependencies_thenOrganizationMustNotDependOnOtherContexts(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..organization..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..training..", "..budgeting..")
                .as("Organization context is upstream and must not depend on training or budgeting");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenTrainingContext_whenCheckingDependencies_thenTrainingMustNotDependOnBudgeting(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..training..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..budgeting..")
                .as("Training context must not depend on budgeting context");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenBudgetContext_whenCheckingDependencies_thenBudgetingMustNotDependOnTraining(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..budgeting..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..training..")
                .as("Budgeting context must not depend on training context");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenPackages_whenCheckingCycles_thenPackagesMustBeFreeOfCycles(final JavaClasses classes) {
        // given
        final var rule = slices()
                .matching("com.example.oulearning.(**)")
                .should()
                .beFreeOfCycles()
                .as("Package structure must be strictly acyclic");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenPackages_whenCheckingPackageStructure_thenNoClassesShouldResideInSharedPackage(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .should()
                .resideInAPackage("..shared..")
                .as("Shared package is prohibited; all classes must belong to a dedicated bounded context");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingMethods_thenDomainClassesMustNotHavePublicSetters(
            final JavaClasses classes) {
        // given
        final var rule = noMethods()
                .that()
                .areDeclaredInClassesThat()
                .resideInAPackage("..domain..")
                .and()
                .arePublic()
                .should()
                .haveNameStartingWith("set")
                .as("Domain models must protect invariants and not expose public setters");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingAnnotations_thenDomainClassesMustNotHaveFrameworkAnnotations(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould()
                .beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould()
                .beAnnotatedWith("org.springframework.stereotype.Repository")
                .orShould()
                .beAnnotatedWith("org.springframework.stereotype.Controller")
                .orShould()
                .beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .orShould()
                .beAnnotatedWith("org.springframework.context.annotation.Configuration")
                .orShould()
                .beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .orShould()
                .beAnnotatedWith("jakarta.persistence.Entity")
                .orShould()
                .beAnnotatedWith("jakarta.persistence.Table")
                .orShould()
                .beAnnotatedWith("jakarta.persistence.Id")
                .as("Domain classes must be free of framework annotations");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainExceptions_whenCheckingInheritance_thenDomainExceptionsMustExtendRuntimeException(
            final JavaClasses classes) {
        // given
        final var rule = classes()
                .that()
                .resideInAPackage("..domain..exception..")
                .should()
                .beAssignableTo(RuntimeException.class)
                .andShould()
                .haveSimpleNameEndingWith("Exception")
                .as("Domain exceptions must reside in exception packages and extend RuntimeException");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainFields_whenCheckingModifiers_thenDomainFieldsMustBePrivateAndFinal(
            final JavaClasses classes) {
        // given
        final var rule = fields()
                .that()
                .areDeclaredInClassesThat()
                .resideInAPackage("..domain..")
                .and()
                .areNotStatic()
                .should()
                .bePrivate()
                .andShould()
                .beFinal()
                .as("Instance fields in domain models must be private and final to guarantee immutability");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingCodingRules_thenDomainMustNotAccessStandardStreams(
            final JavaClasses classes) {
        // given
        final var rule = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
                .as("Domain classes must not write to standard streams");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingCodingRules_thenDomainMustNotThrowGenericExceptions(
            final JavaClasses classes) {
        // given
        final var rule = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
                .as("Domain methods must throw domain-specific exceptions, not generic exceptions");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingCodingRules_thenDomainMustNotUseJavaUtilLogging(
            final JavaClasses classes) {
        // given
        final var rule = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
                .as("Domain classes must not use java.util.logging");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainClasses_whenCheckingCodingRules_thenDomainMustNotUseLegacyDateAndTimeTypes(
            final JavaClasses classes) {
        // given
        final var rule = noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("java.util.Date", "java.util.Calendar")
                .as("Domain classes must use modern java.time types or value objects, not legacy Date or Calendar");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainConstants_whenCheckingClassModifiers_thenConstantsMustBeFinalWithPrivateConstructors(
            final JavaClasses classes) {
        // given
        final var rule = classes()
                .that()
                .resideInAPackage("..domain..")
                .and()
                .haveSimpleNameEndingWith("Constants")
                .should()
                .haveModifier(JavaModifier.FINAL)
                .as("Domain constants classes must be final");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainGuards_whenCheckingClassModifiers_thenGuardsMustBeFinal(final JavaClasses classes) {
        // given
        final var rule = classes()
                .that()
                .resideInAPackage("..domain..")
                .and()
                .haveSimpleNameEndingWith("Guard")
                .should()
                .haveModifier(JavaModifier.FINAL)
                .as("Domain guards classes must be final");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainIds_whenCheckingType_thenIdsMustBeRecords(final JavaClasses classes) {
        // given
        final var rule = classes()
                .that()
                .resideInAPackage("..domain..")
                .and()
                .haveSimpleNameEndingWith("Id")
                .and()
                .doNotHaveSimpleName("IdGenerator")
                .should()
                .beRecords()
                .as("Domain ID types must be immutable typed records");

        // when

        // then
        rule.check(classes);
    }

    @ArchTest
    void givenDomainRepositories_whenCheckingType_thenRepositoriesMustBeInterfaces(final JavaClasses classes) {
        // given
        final var rule = classes()
                .that()
                .resideInAPackage("..domain..")
                .and()
                .haveSimpleNameEndingWith("Repository")
                .should()
                .beInterfaces()
                .allowEmptyShould(true)
                .as("Domain repository ports must be interfaces");

        // when

        // then
        rule.check(classes);
    }
}
