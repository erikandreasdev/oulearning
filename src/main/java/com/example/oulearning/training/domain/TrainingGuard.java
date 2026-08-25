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

    private static final String FIELD_TRAINING_ID = "Training id";
    private static final String FIELD_TYPE_ID = "TypeId";
    private static final String FIELD_REQUESTED_BY = "RequestedBy employee id";
    private static final String FIELD_ATTENDEE = "Attendee";
    private static final String FIELD_OU_ID = "Organizational unit id";
    private static final String FIELD_TRAINING_NAME_VO = "Name";
    private static final String FIELD_TRAINING_NAME = "Training name";
    private static final String FIELD_COST = "Cost";
    private static final String FIELD_COST_AMOUNT = "Cost amount";
    private static final String FIELD_CURRENCY = "Currency";
    private static final String FIELD_HOURS = "Hours";
    private static final String FIELD_PURPOSE = "Purpose";
    private static final String FIELD_PURPOSE_TYPE = "TrainingPurposeType";
    private static final String FIELD_PURPOSE_DESC = "Purpose description";
    private static final String FIELD_TYPE_NAME_VO = "Name";
    private static final String FIELD_TYPE_NAME = "TypeName";
    private static final String FIELD_STATUS = "Status";
    private static final String FIELD_CREATED_AT = "CreatedAt";
    private static final String FIELD_UPDATED_AT = "UpdatedAt";
    private static final String FIELD_REVIEWED_AT = "ReviewedAt";
    private static final String FIELD_MODALITY = "Modality";
    private static final String FIELD_COMMENTS = "Comments";
    private static final String FIELD_START_DATE = "Start date";
    private static final String FIELD_END_DATE = "End date";
    private static final String FIELD_EXT_PROVIDER_ID = "ExternalProviderId";
    private static final String FIELD_EXT_PROVIDER_NAME_VO = "ExternalProviderName";
    private static final String FIELD_EXT_PROVIDER_NAME = "External provider name";
    private static final String FIELD_EXT_PROVIDER_CONTACT = "ExternalProviderContact";
    private static final String FIELD_EMAIL = "Email";
    private static final String FIELD_PHONE = "Phone";

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(TrainingConstants.PHONE_REGEX);

    private TrainingGuard() {
    }

    static void requireTrainingId(final TrainingId id) {
        requireNonNull(id, FIELD_TRAINING_ID);
    }

    static void requirePositiveTrainingId(final long value) {
        requirePositiveId(value, FIELD_TRAINING_ID);
    }

    static long requireValidTrainingId(final String value) {
        return requireValidId(value, FIELD_TRAINING_ID);
    }

    static void requireRequestedBy(final EmployeeId requestedBy) {
        requireNonNull(requestedBy, FIELD_REQUESTED_BY);
    }

    static void requireAttendee(final EmployeeId attendee) {
        requireNonNull(attendee, FIELD_ATTENDEE);
    }

    static void requireOrganizationalUnitId(final OrganizationalUnitId organizationalUnitId) {
        requireNonNull(organizationalUnitId, FIELD_OU_ID);
    }

    static void requireTrainingName(final TrainingName name) {
        requireNonNull(name, FIELD_TRAINING_NAME_VO);
    }

    static String requireValidTrainingName(final String value) {
        return requireLengthBetween(
                value,
                FIELD_TRAINING_NAME,
                TrainingConstants.MIN_NAME_LENGTH,
                TrainingConstants.MAX_NAME_LENGTH);
    }

    static void requireCost(final Cost cost) {
        requireNonNull(cost, FIELD_COST);
    }

    static BigDecimal requireNonNegativeCost(final BigDecimal amount) {
        requireNonNull(amount, FIELD_COST_AMOUNT);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw InvalidTrainingOperationException.negativeCost(amount);
        }
        return amount;
    }

    static String requireValidCurrency(final String currency) {
        final var stripped = requireNonBlank(currency, FIELD_CURRENCY).toUpperCase();
        try {
            Currency.getInstance(stripped);
        } catch (final IllegalArgumentException e) {
            throw InvalidTrainingOperationException.invalidCurrency(stripped, e);
        }
        return stripped;
    }

    static void requireHours(final Hours hours) {
        requireNonNull(hours, FIELD_HOURS);
    }

    static void requireHoursAtLeast(final int value, final int minHours) {
        if (value < minHours) {
            throw InvalidTrainingOperationException.invalidHours(minHours, value);
        }
    }

    static void requirePurpose(final TrainingPurpose purpose) {
        requireNonNull(purpose, FIELD_PURPOSE);
    }

    static void requirePurposeType(final TrainingPurposeType type) {
        requireNonNull(type, FIELD_PURPOSE_TYPE);
    }

    static String requireValidOtherPurposeDescription(final String description) {
        return requireLengthBetween(
                description,
                FIELD_PURPOSE_DESC,
                TrainingConstants.MIN_PURPOSE_LENGTH,
                TrainingConstants.MAX_PURPOSE_LENGTH);
    }

    static void requireTypeId(final TypeId typeId) {
        requireNonNull(typeId, FIELD_TYPE_ID);
    }

    static void requirePositiveTypeId(final long value) {
        requirePositiveId(value, FIELD_TYPE_ID);
    }

    static long requireValidTypeId(final String value) {
        return requireValidId(value, FIELD_TYPE_ID);
    }

    static void requireTypeName(final TypeName name) {
        requireNonNull(name, FIELD_TYPE_NAME_VO);
    }

    static String requireValidTypeName(final String value) {
        return requireLengthBetween(
                value,
                FIELD_TYPE_NAME,
                TrainingConstants.MIN_NAME_LENGTH,
                TrainingConstants.MAX_NAME_LENGTH);
    }

    static void requireStatus(final TrainingStatus status) {
        requireNonNull(status, FIELD_STATUS);
    }

    static void requireCreatedAt(final Instant createdAt) {
        requireNonNull(createdAt, FIELD_CREATED_AT);
    }

    static void requireUpdatedAt(final Instant updatedAt) {
        requireNonNull(updatedAt, FIELD_UPDATED_AT);
    }

    static void requireReviewedAt(final Instant reviewedAt) {
        requireNonNull(reviewedAt, FIELD_REVIEWED_AT);
    }

    static void requireModality(final Modality modality) {
        requireNonNull(modality, FIELD_MODALITY);
    }

    static String requireValidComments(final String comments) {
        return requireLengthBetween(
                comments,
                FIELD_COMMENTS,
                TrainingConstants.MIN_COMMENTS_LENGTH,
                TrainingConstants.MAX_COMMENTS_LENGTH);
    }

    static void requireStartDate(final Instant startDate) {
        requireNonNull(startDate, FIELD_START_DATE);
    }

    static void requireEndDate(final Instant endDate) {
        requireNonNull(endDate, FIELD_END_DATE);
    }

    static void requireDateRange(final Instant startDate, final Instant endDate) {
        if (endDate.isBefore(startDate)) {
            throw InvalidTrainingOperationException.invalidDateRange(startDate, endDate);
        }
    }

    static void requireExternalProviderId(final ExternalProviderId id) {
        requireNonNull(id, FIELD_EXT_PROVIDER_ID);
    }

    static void requirePositiveExternalProviderId(final long value) {
        requirePositiveId(value, FIELD_EXT_PROVIDER_ID);
    }

    static long requireValidExternalProviderId(final String value) {
        return requireValidId(value, FIELD_EXT_PROVIDER_ID);
    }

    static void requireExternalProviderName(final ExternalProviderName name) {
        requireNonNull(name, FIELD_EXT_PROVIDER_NAME_VO);
    }

    static String requireValidExternalProviderName(final String value) {
        return requireLengthBetween(
                value,
                FIELD_EXT_PROVIDER_NAME,
                TrainingConstants.MIN_NAME_LENGTH,
                TrainingConstants.MAX_NAME_LENGTH);
    }

    static void requireExternalProviderContact(final ExternalProviderContact contact) {
        requireNonNull(contact, FIELD_EXT_PROVIDER_CONTACT);
    }

    static void requireContactEmail(final Email email) {
        requireNonNull(email, FIELD_EMAIL);
    }

    static void requireContactPhone(final Phone phone) {
        requireNonNull(phone, FIELD_PHONE);
    }

    static String requireValidPhone(final String value) {
        final var stripped = requireNonBlank(value, FIELD_PHONE);
        final var normalized = stripped.replaceAll("[\\s\\-\\(\\)\\.]", "");
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw InvalidTrainingOperationException.invalidPhoneFormat(
                    stripped,
                    TrainingConstants.PHONE_DIGITS_MIN,
                    TrainingConstants.PHONE_DIGITS_MAX);
        }
        return normalized;
    }

    private static <T> void requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidTrainingOperationException.nullField(fieldName);
        }
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        requireNonNull(value, fieldName);
        final var notNull = value.strip();
        if (notNull.isBlank()) {
            throw InvalidTrainingOperationException.blankField(fieldName);
        }
        return notNull;
    }

    private static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidTrainingOperationException.lengthOutOfRange(
                    fieldName, min, max, stripped);
        }
        return stripped;
    }

    private static void requirePositiveId(final long value, final String fieldName) {
        if (value < TrainingConstants.MIN_ID) {
            throw InvalidTrainingOperationException.nonPositiveId(fieldName, value);
        }
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidTrainingOperationException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            requirePositiveId(parsed, fieldName);
            return parsed;
        } catch (final NumberFormatException e) {
            throw InvalidTrainingOperationException.invalidId(fieldName, value, e);
        }
    }
}
