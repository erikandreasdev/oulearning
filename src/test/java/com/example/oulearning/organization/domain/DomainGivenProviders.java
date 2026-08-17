package com.example.oulearning.organization.domain;

import org.instancio.Instancio;
import org.instancio.junit.GivenProvider;

/**
 * Instancio GivenProvider implementations to dynamically generate valid and mutated invalid domain values.
 * All values are generated dynamically using Instancio generators and combinators without hardcoded static lists.
 */
public final class DomainGivenProviders {

    private DomainGivenProviders() {
        // Utility class
    }

    public static final class BlankStringProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var length = context.random().intRange(0, 8);
            final var whitespaceChars = new Character[] {' ', '\t', '\n', '\r'};
            final var sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(context.random().oneOf(whitespaceChars));
            }
            return sb.toString();
        }
    }

    public static final class ValidNameProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            return Instancio.gen().string().mixedCase().length(3, 15).get();
        }
    }

    public static final class InvalidNameProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var base = Instancio.gen().string().mixedCase().length(3, 8).get();
            final var mutationType = context.random().intRange(1, 4);
            return switch (mutationType) {
                case 1 -> base + Instancio.gen().string().digits().length(1, 3).get();
                case 2 -> base + context.random().oneOf('!', '@', '#', '$', '%', '&', '_');
                case 3 -> "-" + base;
                default -> base + "-";
            };
        }
    }

    public static final class ValidSurnameProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            return Instancio.gen().string().mixedCase().length(3, 15).get();
        }
    }

    public static final class InvalidSurnameProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var base = Instancio.gen().string().mixedCase().length(3, 8).get();
            final var mutationType = context.random().intRange(1, 4);
            return switch (mutationType) {
                case 1 -> base + Instancio.gen().string().digits().length(1, 3).get();
                case 2 -> base + context.random().oneOf('!', '@', '#', '$', '%', '&', '_');
                case 3 -> "'" + base;
                default -> base + "'";
            };
        }
    }

    public static final class ValidEmailProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var local = Instancio.gen().string().lowerCase().length(4, 8).get();
            final var domain = Instancio.gen().string().lowerCase().length(4, 8).get();
            final var withPadding = context.random().trueOrFalse();
            final var email = "%s@%s.com".formatted(local, domain);
            return withPadding ? "  %s  ".formatted(email) : email;
        }
    }

    public static final class InvalidEmailProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var local = Instancio.gen().string().lowerCase().length(4, 8).get();
            final var domain = Instancio.gen().string().lowerCase().length(4, 8).get();
            final var mutationType = context.random().intRange(1, 5);
            return switch (mutationType) {
                case 1 -> local + domain; // missing @
                case 2 -> "@%s.com".formatted(domain); // missing local part
                case 3 -> "%s@.com".formatted(local); // missing domain name
                case 4 -> "%s@%s..com".formatted(local, domain); // consecutive dots
                default -> "%s@%s.c".formatted(local, domain); // single character TLD
            };
        }
    }

    public static final class ValidPhoneProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var digits = Instancio.gen().longs().range(1000000000L, 9999999999L).get();
            final var withPlus = context.random().trueOrFalse();
            return withPlus ? "+%d".formatted(digits) : "%d".formatted(digits);
        }
    }

    public static final class InvalidPhoneProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var mutationType = context.random().intRange(1, 5);
            return switch (mutationType) {
                case 1 -> "%d".formatted(Instancio.gen().ints().range(10000, 99999).get()); // too short (< 7 digits)
                case 2 -> "+%s".formatted(
                        Instancio.gen().string().digits().length(16, 20).get()); // too long (> 15 digits)
                case 3 -> "0%d".formatted(
                        Instancio.gen().longs().range(1000000L, 9999999L).get()); // leading zero
                case 4 -> "+%s".formatted(Instancio.gen().string().mixedCase().length(7, 10).get()); // letters
                default -> "+%d@%d".formatted(
                        Instancio.gen().ints().range(100, 999).get(),
                        Instancio.gen().ints().range(100, 999).get()); // illegal symbols
            };
        }
    }

    public static final class ValidCorporateKeyProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var number = Instancio.gen().ints().range(0, 9999).get();
            final var prefix = context.random().trueOrFalse() ? "CK" : "ck";
            final var withPadding = context.random().trueOrFalse();
            final var key = "%s%04d".formatted(prefix, number);
            return withPadding ? "  %s  ".formatted(key) : key;
        }
    }

    public static final class InvalidCorporateKeyProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var mutationType = context.random().intRange(1, 5);
            return switch (mutationType) {
                case 1 -> "CK%03d".formatted(Instancio.gen().ints().range(0, 999).get()); // 3 digits
                case 2 -> "CK%05d".formatted(Instancio.gen().ints().range(10000, 99999).get()); // 5 digits
                case 3 -> "%s%04d".formatted(
                        Instancio.gen().string().upperCase().length(2).get().replace("CK", "AK").replace("ck", "ak"),
                        Instancio.gen().ints().range(0, 9999).get()); // wrong prefix
                case 4 -> "CK%s".formatted(Instancio.gen().string().mixedCase().length(4).get()); // letters
                default -> "CK %04d".formatted(Instancio.gen().ints().range(0, 9999).get()); // space inside
            };
        }
    }

    public static final class ValidEmployeeRoleProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var role = Instancio.gen().enumOf(EmployeeRole.class).get();
            final var casing = context.random().intRange(1, 3);
            return switch (casing) {
                case 1 -> role.name().toLowerCase();
                case 2 -> "  %s  ".formatted(role.name());
                default -> role.name();
            };
        }
    }

    public static final class InvalidEmployeeRoleProvider implements GivenProvider {
        @Override
        public Object provide(ElementContext context) {
            final var randomText = Instancio.gen().string().mixedCase().length(5, 12).get();
            final var randomDigits = Instancio.gen().string().digits().length(2, 4).get();
            return "%s_%s".formatted(randomText, randomDigits);
        }
    }
}
