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
            Type type = Type.create(id, name, parentTypeId);

            assertThat(type.id()).isEqualTo(id);
            assertThat(type.name()).isEqualTo(name);
            assertThat(type.parentTypeId()).contains(parentTypeId);
        }

        @Test
        @DisplayName("should create root Type")
        void should_createRootType() {
            Type type = Type.createRoot(id, name);

            assertThat(type.id()).isEqualTo(id);
            assertThat(type.parentTypeId()).isEmpty();
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when type is its own parent")
        void should_throwException_when_parentIsSelf() {
            assertThatThrownBy(() -> Type.create(id, name, id))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be its own parent");
        }
    }

    @Nested
    @DisplayName("State Changes")
    class StateChanges {

        @Test
        @DisplayName("should change type name")
        void should_changeTypeName() {
            Type type = Type.createRoot(id, name);
            TypeName newName = TypeName.of("Leadership & Communication");

            type.changeName(newName);

            assertThat(type.name()).isEqualTo(newName);
        }

        @Test
        @DisplayName("should change parent type")
        void should_changeParentType() {
            Type type = Type.createRoot(id, name);
            TypeId newParent = TypeId.of(UUID.randomUUID());

            type.changeParent(newParent);

            assertThat(type.parentTypeId()).contains(newParent);
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when changing parent to self")
        void should_throwException_when_changeParentToSelf() {
            Type type = Type.createRoot(id, name);

            assertThatThrownBy(() -> type.changeParent(id))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("Cannot set training type parent to itself");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Type t1 = Type.create(id, name, parentTypeId);
            Type t2 = Type.create(id, TypeName.of("Other"), null);

            assertThat(t1).isEqualTo(t2);
            assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Type t1 = Type.create(id, name, parentTypeId);
            Type t2 = Type.create(TypeId.of(UUID.randomUUID()), name, parentTypeId);

            assertThat(t1).isNotEqualTo(t2);
        }
    }
}
