package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOuException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OuIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid UUID, when creating OuId, then id is created successfully")
        void givenValidUuid_whenCreatingOuId_thenIdIsCreatedSuccessfully() {

            final var uuid = HierarchyTestFactory.randomUuid();


            final var id = OuId.of(uuid);


            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing OuId, then id is parsed successfully")
        void givenValidUuidString_whenParsingOuId_thenIdIsParsedSuccessfully() {

            final var uuid = HierarchyTestFactory.randomUuid();
            final var uuidString = " %s ".formatted(uuid);


            final var id = OuId.fromString(uuidString);


            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating OuId, then throw InvalidOuException")
        void givenNullUuid_whenCreatingOuId_thenThrowInvalidOuException() {





            assertThatThrownBy(() -> new OuId(null))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given blank UUID string, when parsing OuId, then throw InvalidOuException")
        void givenBlankUuidString_whenParsingOuId_thenThrowInvalidOuException() {





            assertThatThrownBy(() -> OuId.fromString("   "))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing OuId, then throw InvalidOuException")
        void givenInvalidUuidString_whenParsingOuId_thenThrowInvalidOuException() {

            final var invalidUuid = Instancio.create(String.class);




            assertThatThrownBy(() -> OuId.fromString(invalidUuid))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing OuIds, then they are equal")
        void givenIdenticalUuids_whenComparingOuIds_thenTheyAreEqual() {

            final var uuid = HierarchyTestFactory.randomUuid();
            final var id1 = OuId.of(uuid);
            final var id2 = OuId.of(uuid);




            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("given different UUIDs, when comparing OuIds, then they are not equal")
        void givenDifferentUuids_whenComparingOuIds_thenTheyAreNotEqual() {

            final var id1 = HierarchyTestFactory.randomOuId();
            final var id2 = HierarchyTestFactory.randomOuId();




            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
