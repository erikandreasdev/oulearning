package com.example.oulearning.shared.domain.fiscal;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object representing a Fiscal Year.
 */
public record FiscalYear(int value) implements Comparable<FiscalYear> {

    public FiscalYear {
        if (value < 2000 || value > 2100) {
            throw new InvalidFiscalYearException(
                    "FiscalYear must be between 2000 and 2100, received: %d".formatted(value));
        }
    }

    public static FiscalYear of(int value) {
        return new FiscalYear(value);
    }

    public static FiscalYear current(Clock clock) {
        Objects.requireNonNull(clock, "Clock cannot be null");
        return new FiscalYear(LocalDate.now(clock).getYear());
    }

    public FiscalYear previous() {
        return new FiscalYear(value - 1);
    }

    public FiscalYear next() {
        return new FiscalYear(value + 1);
    }

    /**
     * Checks if this fiscal year is the current fiscal year or the immediately preceding one.
     */
    public boolean isCurrentOrPrevious(FiscalYear current) {
        Objects.requireNonNull(current, "Current FiscalYear cannot be null");
        return this.value == current.value || this.value == current.value - 1;
    }

    @Override
    public int compareTo(FiscalYear o) {
        return Integer.compare(this.value, o.value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
