package com.example.oulearning.training;

public final class TrainingApiEndpoints {
    private TrainingApiEndpoints() {}
    public static final String TRAINING_REQUESTS = "/api/v1/trainings/requests";
    public static final String TRAINING_REQUESTS_BY_OU = "/api/v1/trainings/requests?organizationalUnitId=%s";
    public static final String TRAINING_BY_ID = "/api/v1/trainings/%s";
    public static final String TRAINING_REVIEW = "/api/v1/trainings/%s/review";
    public static final String TRAINING_DETAILS = "/api/v1/trainings/%s/details";
    public static final String AREA_TRAININGS = "/api/v1/areas/%s/trainings";
}
