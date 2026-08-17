package com.example.oulearning.shared.domain.fiscal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FiscalYearTest {

    @Test
    @DisplayName("should create FiscalYear when in valid range")
    void should_create_whenValid() {
        final var fy = FiscalYear.of(2026);
        assertThat(fy.value()).isEqualTo(2026);
        assertThat(fy.toString()).isEqualTo("2026");
    }

    @Test
    @DisplayName("should throw InvalidFiscalYearException when outside 2000-2100 range")
    void should_throw_whenOutOfRange() {
        assertThatThrownBy(() -> FiscalYear.of(1999))
                .isInstanceOf(InvalidFiscalYearException.class);

        assertThatThrownBy(() -> FiscalYear.of(2101))
                .isInstanceOf(InvalidFiscalYearException.class);
    }

    @Test
    @DisplayName("should create FiscalYear from Clock")
    void should_createFromClock() {
        final var fixedClock = Clock.fixed(Instant.parse("2026-08-17T10:00:00Z"), ZoneId.of("UTC"));
        final var current = FiscalYear.current(fixedClock);
        assertThat(current.value()).isEqualTo(2026);
    }

    @Test
    @DisplayName("should identify current or previous fiscal year")
    void should_identifyCurrentOrPrevious() {
        final var current2026 = FiscalYear.of(2026);
        final var year2026 = FiscalYear.of(2026);
        final var year2025 = FiscalYear.of(2025);
        final var year2024 = FiscalYear.of(2024);
        final var year2027 = FiscalYear.of(2027);

        assertThat(year2026.isCurrentOrPrevious(current2026)).isTrue();
        assertThat(year2025.isCurrentOrPrevious(current2026)).isTrue();
        assertThat(year2024.isCurrentOrPrevious(current2026)).isFalse();
        assertThat(year2027.isCurrentOrPrevious(current2026)).isFalse();
    }
}
