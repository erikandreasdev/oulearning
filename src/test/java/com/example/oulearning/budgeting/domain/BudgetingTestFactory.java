package com.example.oulearning.budgeting.domain;

import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.math.BigDecimal;
import java.util.UUID;
import org.instancio.Instancio;

public final class BudgetingTestFactory {

    private static final double MIN_AMOUNT = 1.0;
    private static final double MAX_AMOUNT = 1_000_000.0;

    private BudgetingTestFactory() {
    }

    public static UUID randomUuid() {
        return Instancio.create(UUID.class);
    }

    public static BudgetId randomBudgetId() {
        return BudgetId.of(randomUuid());
    }

    public static int randomFiscalYearValue() {
        return Instancio.gen()
                .ints()
                .range(BudgetingConstants.MIN_FISCAL_YEAR, BudgetingConstants.MAX_FISCAL_YEAR)
                .get();
    }

    public static FiscalYear randomFiscalYear() {
        return FiscalYear.of(randomFiscalYearValue());
    }

    public static BigDecimal randomBigDecimalAmount() {
        return Instancio.gen()
                .math()
                .bigDecimal()
                .min(BigDecimal.valueOf(MIN_AMOUNT))
                .max(BigDecimal.valueOf(MAX_AMOUNT))
                .scale(BudgetingConstants.MONEY_SCALE)
                .get();
    }

    public static double randomDoubleAmount() {
        return Instancio.gen().doubles().range(MIN_AMOUNT, MAX_AMOUNT).get();
    }

    public static Money randomMoney() {
        return Money.of(randomBigDecimalAmount());
    }

    public static Budget randomBudget() {
        return randomBudget(randomBudgetId(), HierarchyTestFactory.randomOrganizationalUnitId());
    }

    public static Budget randomBudget(final BudgetId id, final OrganizationalUnitId organizationalUnitId) {
        final var total = randomMoney();
        final var reserved = randomMoney();
        final var available = randomMoney();
        return Budget.of(id, organizationalUnitId, randomFiscalYear(), total, reserved, available);
    }
}
