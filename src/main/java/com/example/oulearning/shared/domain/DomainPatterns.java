package com.example.oulearning.shared.domain;

import java.util.regex.Pattern;

/**
 * Centralized repository of regular expression patterns used across domain models and value objects.
 */
public final class DomainPatterns {

    private DomainPatterns() {
        // Utility class
    }

    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-z0-9]+(?:[._%+-][a-z0-9]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z]{2,}$");

    public static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    public static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}]+(?:[\\s'-][\\p{L}]+)*$");

    public static final Pattern CORPORATE_KEY_PATTERN =
            Pattern.compile("^CK\\d{4}$");
}
