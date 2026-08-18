package com.example.oulearning.training.domain.request.vo.identity;

import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing the Target Organizational Unit in Training domain.
 */
public record OuId(UUID value) {

    public OuId {
        if (value == null) {
            throw new InvalidTrainingRequestException("OuId cannot be null");
        }
    }

    public static OuId of(UUID value) {
        return new OuId(value);
    }

    public static OuId of(String value) {
        Objects.requireNonNull(value, "OuId string cannot be null");
        return new OuId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
