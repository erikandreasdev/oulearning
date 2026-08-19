package com.example.oulearning.training.domain;

/**
 * Constants for the training bounded context.
 */
public final class TrainingConstants {

    public static final String DEFAULT_CURRENCY = "EUR";
    public static final int MIN_HOURS = 1;
    public static final int PHONE_DIGITS_MIN = 7;
    public static final int PHONE_DIGITS_MAX = 15;
    public static final String PHONE_REGEX = "^\\+?[1-9]\\d{6,14}$";
    public static final int COST_SCALE = 2;
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 150;
    public static final int MIN_PURPOSE_LENGTH = 1;
    public static final int MAX_PURPOSE_LENGTH = 500;
    public static final int MIN_COMMENTS_LENGTH = 1;
    public static final int MAX_COMMENTS_LENGTH = 1000;

    private TrainingConstants() {}
}
