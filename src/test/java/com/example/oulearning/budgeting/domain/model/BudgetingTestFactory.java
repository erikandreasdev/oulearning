package com.example.oulearning.budgeting.domain.model;

import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.math.BigDecimal;
import org.instancio.Instancio;

public final class BudgetingTestFactory {

    private static final double MIN_AMOUNT = 1.0;
    private static final double MAX_AMOUNT = 1_000_000.0;

    private BudgetingTestFactory() {
    }

    public static long randomId() {
        return Instancio.gen().longs().range(BudgetingConstants.MIN_ID, Long.MAX_VALUE).get();
    }

    public static BudgetId randomBudgetId() {
        return BudgetId.of(randomId());
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
