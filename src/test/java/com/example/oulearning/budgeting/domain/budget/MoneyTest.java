package com.example.oulearning.budgeting.domain.budget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.budget.exception.InvalidMoneyException;
import java.math.BigDecimal;
import javax.money.Monetary;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class MoneyTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create Money in EUR with 2 decimal scale by default")
        void should_createMoney_inEurByDefault() {
            final var money = Money.euros(100.50);

            assertThat(money.currency().getCurrencyCode()).isEqualTo("EUR");
            assertThat(money.amount()).isEqualTo(new BigDecimal("100.50"));
            assertThat(money.toString()).isEqualTo("EUR 100.50");
        }

        @Test
        @DisplayName("should normalize scale to 2 decimals using HALF_UP")
        void should_normalizeScale_usingHalfUp() {
            final var money = Money.euros(100.555);

            assertThat(money.amount()).isEqualTo(new BigDecimal("100.56"));
        }

        @Test
        @DisplayName("should create zero Money in default currency")
        void should_createZeroMoney() {
            final var zero = Money.zero();

            assertThat(zero.isZero()).isTrue();
            assertThat(zero.amount()).isEqualTo(new BigDecimal("0.00"));
            assertThat(zero.currency().getCurrencyCode()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("should throw InvalidMoneyException when amount is negative")
        void should_throwException_when_amountIsNegative() {
            assertThatThrownBy(() -> Money.euros(-10.00))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("cannot be negative");
        }

        @Test
        @DisplayName("should throw InvalidMoneyException when amount or currency is null")
        void should_throwException_when_nullPassed() {
            assertThatThrownBy(() -> new Money(null))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> Money.of(null, Money.DEFAULT_CURRENCY))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> Money.of(BigDecimal.TEN, null))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticOperations {

        @Test
        @DisplayName("should add two Money instances of same currency")
        void should_addMoney_sameCurrency() {
            final var m1 = Money.euros(100.25);
            final var m2 = Money.euros(50.75);

            final var result = m1.plus(m2);

            assertThat(result.amount()).isEqualTo(new BigDecimal("151.00"));
            assertThat(result.currency()).isEqualTo(Money.DEFAULT_CURRENCY);
        }

        @Test
        @DisplayName("should subtract Money instances and prevent negative balance")
        void should_subtractMoney_andPreventNegativeBalance() {
            final var m1 = Money.euros(100.00);
            final var m2 = Money.euros(40.00);

            final var result = m1.minus(m2);
            assertThat(result.amount()).isEqualTo(new BigDecimal("60.00"));

            assertThatThrownBy(() -> m2.minus(m1))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("result would be negative");
        }

        @Test
        @DisplayName("should throw InvalidMoneyException on currency mismatch during arithmetic")
        void should_throwException_onCurrencyMismatch() {
            final var eur = Money.euros(100.00);
            final var usd = Money.of(BigDecimal.valueOf(100), Monetary.getCurrency("USD"));

            assertThatThrownBy(() -> eur.plus(usd))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("Currency mismatch");
            assertThatThrownBy(() -> eur.minus(usd))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("Currency mismatch");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when amounts and currencies match")
        void should_beEqual_when_amountsAndCurrenciesMatch() {
            final var m1 = Money.euros(250.00);
            final var m2 = Money.euros(250.00);

            assertThat(m1).isEqualTo(m2);
            assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
        }

        @Test
        @DisplayName("should compare correctly")
        void should_compareCorrectly() {
            final var m1 = Money.euros(100.00);
            final var m2 = Money.euros(200.00);

            assertThat(m1.compareTo(m2)).isNegative();
            assertThat(m2.compareTo(m1)).isPositive();
            assertThat(m1.compareTo(Money.euros(100.00))).isZero();
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var money = Money.euros(50.00);
            assertThat(money.getClass().isRecord()).isTrue();
        }
    }
}
