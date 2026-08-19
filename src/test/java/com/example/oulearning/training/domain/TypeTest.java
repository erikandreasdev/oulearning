package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TypeTest {

    private final TypeId id = TypeId.of(UUID.randomUUID());
    private final TypeName name = TypeName.of("Power Skills");
    private final TypeId parentTypeId = TypeId.of(UUID.randomUUID());

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create Type with parent")
        void should_createType_withParent() {
            Type type = Type.of(id, name, parentTypeId);

            assertThat(type.id()).isEqualTo(id);
            assertThat(type.name()).isEqualTo(name);
            assertThat(type.parentTypeId()).contains(parentTypeId);
        }

        @Test
        @DisplayName("should create root Type")
        void should_createRootType() {
            Type type = Type.of(id, name);

            assertThat(type.id()).isEqualTo(id);
            assertThat(type.parentTypeId()).isEmpty();
        }

        @Test
        @DisplayName("should throw NullPointerException when required parameters are null")
        void should_throwException_when_requiredNull() {
            assertThatThrownBy(() -> new Type(null, name, parentTypeId))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Type(id, null, parentTypeId))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Type t1 = Type.of(id, name, parentTypeId);
            Type t2 = Type.of(id, TypeName.of("Other"), null);

            assertThat(t1).isEqualTo(t2);
            assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Type t1 = Type.of(id, name, parentTypeId);
            Type t2 = Type.of(TypeId.of(UUID.randomUUID()), name, parentTypeId);

            assertThat(t1).isNotEqualTo(t2);
        }
    }
}
