package com.example.oulearning.training.domain;


public record Phone(String value) {

    public Phone {
        value = TrainingGuard.requireValidPhone(value);
    }

    public static Phone of(final String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
