package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TypeTest {

    private final TypeId id = TrainingTestFactory.randomTypeId();
    private final TypeName name = TrainingTestFactory.randomTypeName();
    private final TypeId parentId = TrainingTestFactory.randomTypeId();

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("given valid type with parent, when creating Type, then create successfully")
        void givenValidTypeWithParent_whenCreatingType_thenCreateSuccessfully() {
            // given

            // when
            final var type = Type.of(id, name, parentId);

            // then
            assertThat(type.id()).isEqualTo(id);
            assertThat(type.name()).isEqualTo(name);
            assertThat(type.parentTypeId()).contains(parentId);
        }

        @Test
        @DisplayName("given root type without parent, when creating Type, then parentTypeId is empty")
        void givenRootTypeWithoutParent_whenCreatingType_thenParentTypeIdIsEmpty() {
            // given

            // when
            final var type = Type.of(id, name);

            // then
            assertThat(type.id()).isEqualTo(id);
            assertThat(type.name()).isEqualTo(name);
            assertThat(type.parentTypeId()).isEmpty();
        }

        @Test
        @DisplayName("given null id or name, when creating Type, then throw InvalidTrainingOperationException")
        void givenNullIdOrName_whenCreatingType_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> Type.of(null, name, parentId))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Type.of(id, null, parentId))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("given types with same id, when comparing Type, then they are equal")
        void givenTypesWithSameId_whenComparingType_thenTheyAreEqual() {
            // given
            final var t1 = Type.of(id, name, parentId);
            final var t2 = Type.of(id, TrainingTestFactory.randomTypeName(), null);

            // when

            // then
            assertThat(t1).isEqualTo(t2).hasSameHashCodeAs(t2);
        }

        @Test
        @DisplayName("given types with different ids, when comparing Type, then they are not equal")
        void givenTypesWithDifferentIds_whenComparingType_thenTheyAreNotEqual() {
            // given
            final var t1 = Type.of(id, name, parentId);
            final var t2 = Type.of(TrainingTestFactory.randomTypeId(), name, parentId);

            // when

            // then
            assertThat(t1).isNotEqualTo(t2);
        }

        @Test
        @DisplayName("given same type instance, when comparing, then they are equal")
        void givenSameTypeInstance_whenComparing_thenTheyAreEqual() {
            // given
            final var type = Type.of(id, name, parentId);

            // when

            // then
            assertThat(type).isEqualTo(type);
        }

        @Test
        @DisplayName("given null or different object type, when comparing, then they are not equal")
        void givenNullOrDifferentType_whenComparing_thenTheyAreNotEqual() {
            // given
            final var type = Type.of(id, name, parentId);

            // when

            // then
            assertThat(type).isNotEqualTo(null).isNotEqualTo(new Object());
        }
    }
}
