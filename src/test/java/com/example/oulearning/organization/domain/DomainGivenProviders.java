package com.example.oulearning.organization.domain;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.employee.EmployeeRole;
import java.util.stream.Stream;
import org.instancio.Instancio;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/**
 * JUnit 5 ArgumentsProviders utilizing Instancio generators dynamically without fixed value lists.
 */
public final class DomainGivenProviders {

    private DomainGivenProviders() {
        // Utility class
    }

    private static String randomAlphabet(int len) {
        return Instancio.gen().string().mixedCase().length(len).get();
    }

    public static final class InvalidCorporateKeys implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final var lowerLetters = Instancio.gen().string().lowerCase().length(2).get();
            final var threeDigits = Instancio.gen().string().digits().length(3).get();
            final var fiveDigits = Instancio.gen().string().digits().length(5).get();
            final var fourLetters = Instancio.gen().string().mixedCase().length(4).get();

            return Stream.of(
                    Arguments.of(Named.of("lowercase prefix", "%s%s".formatted(lowerLetters, threeDigits + "1"))),
                    Arguments.of(Named.of("too short digits", "CK%s".formatted(threeDigits))),
                    Arguments.of(Named.of("too long digits", "CK%s".formatted(fiveDigits))),
                    Arguments.of(Named.of("alphabetic suffix", "CK%s".formatted(fourLetters))),
                    Arguments.of(Named.of("empty string", "")),
                    Arguments.of(Named.of("blank spaces", "   \t\n  ")),
                    Arguments.of(Named.of("missing prefix", "%s".formatted(threeDigits + "9"))),
                    Arguments.of(Named.of("special characters", "CK%s!#".formatted(threeDigits.substring(0, 2)))));
        }
    }

    public static final class ValidCorporateKeys implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.generate(() -> {
                        final var number = Instancio.gen().ints().range(0, 9999).get();
                        final var ck = "CK%04d".formatted(number);
                        return Arguments.of(CorporateKey.of(ck), ck);
                    })
                    .limit(5);
        }
    }

    public static final class InvalidEmails implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final var s1 = randomAlphabet(5);
            final var s2 = randomAlphabet(4);
            return Stream.of(
                    Arguments.of(Named.of("missing at-sign", "%s.%s.com".formatted(s1, s2))),
                    Arguments.of(Named.of("missing domain", "%s@".formatted(s1))),
                    Arguments.of(Named.of("missing username", "@%s.com".formatted(s2))),
                    Arguments.of(Named.of("missing top-level domain", "%s@%s".formatted(s1, s2))),
                    Arguments.of(Named.of("whitespace in middle", "%s %s@domain.com".formatted(s1, s2))),
                    Arguments.of(Named.of("empty string", "")),
                    Arguments.of(Named.of("blank spaces", "   ")),
                    Arguments.of(Named.of("double at-sign", "%s@@%s.com".formatted(s1, s2))));
        }
    }

    public static final class ValidEmails implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.generate(() -> {
                        final var user = Instancio.gen().string().lowerCase().length(5, 10).get();
                        final var domain = Instancio.gen().string().lowerCase().length(4, 8).get();
                        final var raw = "%s@%s.com".formatted(user, domain);
                        return Arguments.of(raw, raw.toLowerCase());
                    })
                    .limit(5);
        }
    }

    public static final class InvalidPhones implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final var shortDigits = Instancio.gen().string().digits().length(5).get();
            final var longDigits = Instancio.gen().string().digits().length(17).get();
            final var letters = randomAlphabet(8);
            return Stream.of(
                    Arguments.of(Named.of("too short", "+%s".formatted(shortDigits))),
                    Arguments.of(Named.of("too long", "+%s".formatted(longDigits))),
                    Arguments.of(Named.of("contains letters", "+346%s".formatted(letters))),
                    Arguments.of(Named.of("empty string", "")),
                    Arguments.of(Named.of("blank spaces", "   ")),
                    Arguments.of(Named.of("leading double plus", "++34612345678")),
                    Arguments.of(Named.of("special characters", "+34#612*345@678")));
        }
    }

    public static final class ValidPhones implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.generate(() -> {
                        final var prefix = Instancio.gen().ints().range(10, 99).get();
                        final var part1 = Instancio.gen().ints().range(100, 999).get();
                        final var part2 = Instancio.gen().ints().range(100, 999).get();
                        final var part3 = Instancio.gen().ints().range(100, 999).get();
                        final var formatted = "+%d (%d) %d-%d".formatted(prefix, part1, part2, part3);
                        final var normalized = "+%d%d%d%d".formatted(prefix, part1, part2, part3);
                        return Arguments.of(formatted, normalized);
                    })
                    .limit(5);
        }
    }

    public static final class InvalidNames implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final var digits = Instancio.gen().string().digits().length(4).get();
            final var longString = Instancio.gen().string().mixedCase().length(101).get();
            return Stream.of(
                    Arguments.of(Named.of("empty string", "")),
                    Arguments.of(Named.of("blank spaces", "   ")),
                    Arguments.of(Named.of("too long > 100 chars", longString)),
                    Arguments.of(Named.of("contains digits", "John%s".formatted(digits))),
                    Arguments.of(Named.of("contains special symbols", "Jane@Doe!")),
                    Arguments.of(Named.of("contains underscore", "Jane_Doe")));
        }
    }

    public static final class ValidNames implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.generate(() -> {
                        final var name = randomAlphabet(6);
                        return Arguments.of("  %s  ".formatted(name), name);
                    })
                    .limit(5);
        }
    }

    public static final class InvalidOuNames implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            final var longString = Instancio.gen().string().mixedCase().length(101).get();
            return Stream.of(
                    Arguments.of(Named.of("empty string", "")),
                    Arguments.of(Named.of("blank spaces", "   ")),
                    Arguments.of(Named.of("too long > 100 chars", longString)),
                    Arguments.of(Named.of("contains illegal symbols", "IT-Dept@HQ!")),
                    Arguments.of(Named.of("contains hash symbol", "Engineering#1")));
        }
    }

    public static final class ValidOuNames implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.generate(() -> {
                        final var name = "Area_" + randomAlphabet(5) + " 01";
                        return Arguments.of("  %s  ".formatted(name), name);
                    })
                    .limit(5);
        }
    }

    public static final class InvalidEmployeeRoles implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(Named.of("unknown role", "CEO")),
                    Arguments.of(Named.of("unknown role", "DIRECTOR")),
                    Arguments.of(Named.of("empty string", "")),
                    Arguments.of(Named.of("blank spaces", "   ")),
                    Arguments.of(Named.of("random string", randomAlphabet(8))));
        }
    }

    public static final class ValidEmployeeRoles implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("employee", EmployeeRole.EMPLOYEE),
                    Arguments.of("EMPLOYEE", EmployeeRole.EMPLOYEE),
                    Arguments.of("Manager", EmployeeRole.MANAGER),
                    Arguments.of("TRAINER", EmployeeRole.TRAINER),
                    Arguments.of("admin", EmployeeRole.ADMIN),
                    Arguments.of("  ADMIN  ", EmployeeRole.ADMIN));
        }
    }
}
