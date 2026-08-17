package com.example.oulearning.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.instancio.Instancio;
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
        @DisplayName("should create positive money in EUR by default")
        void should_createMoney_when_validAmountProvided() {
            // given
            final var amount = BigDecimal.valueOf(Instancio.gen().doubles().range(1.0, 1000.0).get());

            // when
            final var money = Money.euros(amount);

            // then
            assertThat(money.amount()).isEqualByComparingTo(amount);
            assertThat(money.currency().getCurrencyCode()).isEqualTo("EUR");
            assertThat(money.isPositive()).isTrue();
            assertThat(money.isZero()).isFalse();
        }

        @Test
        @DisplayName("should create zero money")
        void should_createZeroMoney() {
            // when
            final var zero = Money.zero();

            // then
            assertThat(zero.isZero()).isTrue();
            assertThat(zero.amount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(zero.currency().getCurrencyCode()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("should throw InvalidMoneyException when amount is negative")
        void should_throwException_when_amountIsNegative() {
            final var negativeAmount = BigDecimal.valueOf(-10.0);
            assertThatThrownBy(() -> Money.euros(negativeAmount))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("cannot be negative");
        }

        @Test
        @DisplayName("should throw InvalidMoneyException when amount is null")
        void should_throwException_when_amountIsNull() {
            assertThatThrownBy(() -> Money.of(null, Money.DEFAULT_CURRENCY))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidMoneyException when currency is null")
        void should_throwException_when_currencyIsNull() {
            assertThatThrownBy(() -> Money.of(BigDecimal.TEN, null))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticOperations {

        @Test
        @DisplayName("should add two money values correctly")
        void should_addMoney_correctly() {
            // given
            final var m1 = Money.euros(100.50);
            final var m2 = Money.euros(49.50);

            // when
            final var sum = m1.plus(m2);

            // then
            assertThat(sum).isEqualTo(Money.euros(150.00));
        }

        @Test
        @DisplayName("should subtract two money values correctly")
        void should_subtractMoney_correctly() {
            // given
            final var m1 = Money.euros(100.50);
            final var m2 = Money.euros(50.50);

            // when
            final var diff = m1.minus(m2);

            // then
            assertThat(diff).isEqualTo(Money.euros(50.00));
        }

        @Test
        @DisplayName("should throw InvalidMoneyException when subtraction results in negative")
        void should_throwException_when_subtractionResultsInNegative() {
            final var m1 = Money.euros(10.00);
            final var m2 = Money.euros(20.00);

            assertThatThrownBy(() -> m1.minus(m2))
                    .isInstanceOf(InvalidMoneyException.class)
                    .hasMessageContaining("cannot be negative");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when amounts and currencies match")
        void should_beEqual_when_amountsAndCurrenciesMatch() {
            final var m1 = Money.euros(100.00);
            final var m2 = Money.euros(100.0);

            assertThat(m1).isEqualTo(m2);
            assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when amounts differ")
        void should_notBeEqual_when_amountsDiffer() {
            final var m1 = Money.euros(100.00);
            final var m2 = Money.euros(200.00);

            assertThat(m1).isNotEqualTo(m2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var money = Money.euros(50.0);
            assertThat(money.getClass().isRecord()).isTrue();
        }
    }
}
