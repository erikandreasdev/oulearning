package com.example.oulearning.training.domain;

import com.example.oulearning.budgeting.domain.BudgetingConstants;
import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.instancio.Instancio;

public final class TrainingTestFactory {

    private static final int MAX_HOURS = 100;
    private static final int DEFAULT_PHONE_DIGITS_LENGTH = 10;
    private static final String DEFAULT_PHONE_PREFIX = "+34";
    private static final double MIN_COST_AMOUNT = 0.0;
    private static final double MAX_COST_AMOUNT = 10000.0;
    private static final int MIN_PURPOSE_LENGTH = 5;
    private static final int MAX_PURPOSE_LENGTH = 50;
    private static final int MIN_COMMENTS_LENGTH = 5;
    private static final int MAX_COMMENTS_LENGTH = 50;
    private static final int DEFAULT_START_DAY_OFFSET = 1;
    private static final int DEFAULT_END_DAY_OFFSET = 3;

    private TrainingTestFactory() {
    }

    public static UUID randomUuid() {
        return Instancio.create(UUID.class);
    }

    public static TrainingId randomTrainingId() {
        return TrainingId.of(randomUuid());
    }

    public static TypeId randomTypeId() {
        return TypeId.of(randomUuid());
    }

    public static String randomTypeNameString() {
        return Instancio.gen()
                .string()
                .length(TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH)
                .get();
    }

    public static TypeName randomTypeName() {
        return TypeName.of(randomTypeNameString());
    }

    public static String randomTrainingNameString() {
        return Instancio.gen()
                .string()
                .length(TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH)
                .get();
    }

    public static TrainingName randomTrainingName() {
        return TrainingName.of(randomTrainingNameString());
    }

    public static int randomHoursValue() {
        return Instancio.gen().ints().range(TrainingConstants.MIN_HOURS, MAX_HOURS).get();
    }

    public static Hours randomHours() {
        return Hours.of(randomHoursValue());
    }

    public static BigDecimal randomBigDecimalCostAmount() {
        return Instancio.gen()
                .math()
                .bigDecimal()
                .min(BigDecimal.valueOf(MIN_COST_AMOUNT))
                .max(BigDecimal.valueOf(MAX_COST_AMOUNT))
                .scale(TrainingConstants.COST_SCALE)
                .get();
    }

    public static double randomDoubleCostAmount() {
        return Instancio.gen().doubles().range(MIN_COST_AMOUNT + 1.0, MAX_COST_AMOUNT).get();
    }

    public static Cost randomCost() {
        return Cost.of(randomBigDecimalCostAmount(), BudgetingConstants.DEFAULT_CURRENCY);
    }

    public static String randomPhoneDigits() {
        return Instancio.gen().string().digits().length(DEFAULT_PHONE_DIGITS_LENGTH).get();
    }

    public static String randomPhoneString() {
        return "%s%s".formatted(DEFAULT_PHONE_PREFIX, randomPhoneDigits());
    }

    public static Phone randomPhone() {
        return Phone.of(randomPhoneString());
    }

    public static ExternalProviderName randomExternalProviderName() {
        return ExternalProviderName.of(Instancio.gen()
                .string()
                .length(TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH)
                .get());
    }

    public static ExternalProviderContact randomExternalProviderContact() {
        return ExternalProviderContact.of(EmployeeTestFactory.randomEmail(), randomPhone());
    }

    public static ExternalProvider randomExternalProvider() {
        return ExternalProvider.of(randomExternalProviderName(), randomExternalProviderContact());
    }

    public static String randomPurposeDescription() {
        return Instancio.gen().string().length(MIN_PURPOSE_LENGTH, MAX_PURPOSE_LENGTH).get();
    }

    public static TrainingPurpose randomTrainingPurpose() {
        return TrainingPurpose.idp();
    }

    public static String randomComments() {
        return Instancio.gen().string().length(MIN_COMMENTS_LENGTH, MAX_COMMENTS_LENGTH).get();
    }

    public static Modality randomModality() {
        return Instancio.create(Modality.class);
    }

    public static Instant randomInstant() {
        return Instant.now();
    }

    public static ManagerReview randomManagerReview() {
        final var now = Instant.now();
        return new ManagerReview(
                randomComments(),
                Modality.BLENDED,
                now.plus(DEFAULT_START_DAY_OFFSET, ChronoUnit.DAYS),
                now.plus(DEFAULT_END_DAY_OFFSET, ChronoUnit.DAYS),
                randomExternalProvider(),
                now);
    }

    public static Type randomType() {
        return randomType(randomTypeId());
    }

    public static Type randomType(final TypeId id) {
        return Type.of(id, randomTypeName(), randomTypeId());
    }

    public static Training randomTraining() {
        return randomTraining(randomTrainingId());
    }

    public static Training randomTraining(final TrainingId id) {
        final var now = Instant.now();
        return Training.create(
                id,
                EmployeeTestFactory.randomEmployeeId(),
                HierarchyTestFactory.randomOuId(),
                randomTrainingName(),
                randomCost(),
                randomHours(),
                randomTrainingPurpose(),
                randomTypeId(),
                now);
    }
}
