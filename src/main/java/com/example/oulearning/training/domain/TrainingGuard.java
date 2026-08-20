package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.regex.Pattern;

final class TrainingGuard {

    private static final Pattern PHONE_PATTERN = Pattern.compile(TrainingConstants.PHONE_REGEX);

    private TrainingGuard() {
    }

    static TrainingId requireTrainingId(final TrainingId id) {
        return requireNonNull(id, "Training id");
    }

    static long requireTrainingId(final long value) {
        return requirePositiveId(value, "Training id");
    }

    static long requireValidTrainingId(final String value) {
        return requireValidId(value, "Training id");
    }

    static EmployeeId requireRequestedBy(final EmployeeId requestedBy) {
        return requireNonNull(requestedBy, "RequestedBy employee id");
    }

    static EmployeeId requireAttendee(final EmployeeId attendee) {
        return requireNonNull(attendee, "Attendee");
    }

    static OrganizationalUnitId requireOrganizationalUnitId(final OrganizationalUnitId organizationalUnitId) {
        return requireNonNull(organizationalUnitId, "Organizational unit id");
    }

    static TrainingName requireTrainingName(final TrainingName name) {
        return requireNonNull(name, "Name");
    }

    static String requireTrainingName(final String value) {
        return requireLengthBetween(
                value, "Training name", TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH);
    }

    static Cost requireCost(final Cost cost) {
        return requireNonNull(cost, "Cost");
    }

    static BigDecimal requireNonNegativeCost(final BigDecimal amount) {
        requireNonNull(amount, "Cost amount");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw InvalidTrainingOperationException.negativeCost(amount);
        }
        return amount;
    }

    static String requireValidCurrency(final String currency) {
        final var stripped = requireNonBlank(currency, "Currency").toUpperCase();
        try {
            Currency.getInstance(stripped);
        } catch (final IllegalArgumentException e) {
            throw InvalidTrainingOperationException.invalidCurrency(stripped, e);
        }
        return stripped;
    }

    static Hours requireHours(final Hours hours) {
        return requireNonNull(hours, "Hours");
    }

    static int requireHoursAtLeast(final int value, final int minHours) {
        if (value < minHours) {
            throw InvalidTrainingOperationException.invalidHours(minHours, value);
        }
        return value;
    }

    static TrainingPurpose requirePurpose(final TrainingPurpose purpose) {
        return requireNonNull(purpose, "Purpose");
    }

    static TrainingPurposeType requirePurposeType(final TrainingPurposeType type) {
        return requireNonNull(type, "TrainingPurposeType");
    }

    static String requireOtherPurposeDescription(final String description) {
        return requireLengthBetween(
                description,
                "Purpose description",
                TrainingConstants.MIN_PURPOSE_LENGTH,
                TrainingConstants.MAX_PURPOSE_LENGTH);
    }

    static TypeId requireTypeId(final TypeId typeId) {
        return requireNonNull(typeId, "TypeId");
    }

    static long requireTypeId(final long value) {
        return requirePositiveId(value, "TypeId");
    }

    static long requireValidTypeId(final String value) {
        return requireValidId(value, "TypeId");
    }

    static TypeName requireTypeName(final TypeName name) {
        return requireNonNull(name, "Name");
    }

    static String requireTypeName(final String value) {
        return requireLengthBetween(
                value, "TypeName", TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH);
    }

    static TrainingStatus requireStatus(final TrainingStatus status) {
        return requireNonNull(status, "Status");
    }

    static Instant requireCreatedAt(final Instant createdAt) {
        return requireNonNull(createdAt, "CreatedAt");
    }

    static Instant requireUpdatedAt(final Instant updatedAt) {
        return requireNonNull(updatedAt, "UpdatedAt");
    }

    static Instant requireReviewedAt(final Instant reviewedAt) {
        return requireNonNull(reviewedAt, "ReviewedAt");
    }

    static Modality requireModality(final Modality modality) {
        return requireNonNull(modality, "Modality");
    }

    static String requireComments(final String comments) {
        return requireLengthBetween(
                comments, "Comments", TrainingConstants.MIN_COMMENTS_LENGTH, TrainingConstants.MAX_COMMENTS_LENGTH);
    }

    static Instant requireStartDate(final Instant startDate) {
        return requireNonNull(startDate, "Start date");
    }

    static Instant requireEndDate(final Instant endDate) {
        return requireNonNull(endDate, "End date");
    }

    static void requireDateRange(final Instant startDate, final Instant endDate) {
        if (endDate.isBefore(startDate)) {
            throw InvalidTrainingOperationException.invalidDateRange(startDate, endDate);
        }
    }

    static ExternalProviderName requireExternalProviderName(final ExternalProviderName name) {
        return requireNonNull(name, "ExternalProviderName");
    }

    static String requireExternalProviderName(final String value) {
        return requireLengthBetween(
                value,
                "External provider name",
                TrainingConstants.MIN_NAME_LENGTH,
                TrainingConstants.MAX_NAME_LENGTH);
    }

    static ExternalProviderContact requireExternalProviderContact(final ExternalProviderContact contact) {
        return requireNonNull(contact, "ExternalProviderContact");
    }

    static Email requireContactEmail(final Email email) {
        return requireNonNull(email, "Email");
    }

    static Phone requireContactPhone(final Phone phone) {
        return requireNonNull(phone, "Phone");
    }

    static String requireValidPhone(final String value) {
        final var stripped = requireNonBlank(value, "Phone");
        final var normalized = stripped.replaceAll("[\\s\\-\\(\\)\\.]", "");
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw InvalidTrainingOperationException.invalidPhoneFormat(
                    stripped, TrainingConstants.PHONE_DIGITS_MIN, TrainingConstants.PHONE_DIGITS_MAX);
        }
        return normalized;
    }

    private static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidTrainingOperationException.nullField(fieldName);
        }
        return value;
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidTrainingOperationException.blankField(fieldName);
        }
        return notNull;
    }

    private static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidTrainingOperationException.lengthOutOfRange(fieldName, min, max, stripped);
        }
        return stripped;
    }

    private static long requirePositiveId(final long value, final String fieldName) {
        if (value < TrainingConstants.MIN_ID) {
            throw InvalidTrainingOperationException.nonPositiveId(fieldName, value);
        }
        return value;
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidTrainingOperationException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            return requirePositiveId(parsed, fieldName);
        } catch (final NumberFormatException e) {
            throw InvalidTrainingOperationException.invalidId(fieldName, value, e);
        }
    }
}
