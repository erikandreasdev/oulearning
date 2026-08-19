package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OuTest {

    private final OuId id = OuId.of(UUID.randomUUID());
    private final Name name = Name.of("Software Engineering");
    private final OuId parentId = OuId.of(UUID.randomUUID());
    private final EmployeeId emp1 = EmployeeId.of("EMP-001");
    private final EmployeeId emp2 = EmployeeId.of("EMP-002");

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create OU with all fields")
        void should_createOu_withAllFields() {
            OuId childId = OuId.of(UUID.randomUUID());
            Ou ou = Ou.of(id, name, parentId, Set.of(childId), Set.of(emp1), Set.of(emp2));

            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).contains(parentId);
            assertThat(ou.childIds()).containsExactly(childId);
            assertThat(ou.owners()).containsExactly(emp1);
            assertThat(ou.members()).containsExactly(emp2);
        }

        @Test
        @DisplayName("should create root OU with empty collections")
        void should_createRootOu() {
            Ou ou = Ou.of(id, name);

            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).isEmpty();
            assertThat(ou.childIds()).isEmpty();
            assertThat(ou.owners()).isEmpty();
            assertThat(ou.members()).isEmpty();
        }

        @Test
        @DisplayName("should throw NullPointerException when required parameters are null")
        void should_throwException_when_requiredParamsNull() {
            assertThatThrownBy(() -> new Ou(null, name, parentId, Set.of(), Set.of(), Set.of()))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Ou(id, null, parentId, Set.of(), Set.of(), Set.of()))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Ou ou1 = Ou.of(id, name, parentId, Set.of(), Set.of(), Set.of());
            Ou ou2 = Ou.of(id, Name.of("Other Name"), null, Set.of(), Set.of(), Set.of());

            assertThat(ou1).isEqualTo(ou2);
            assertThat(ou1.hashCode()).isEqualTo(ou2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Ou ou1 = Ou.of(id, name, parentId, Set.of(), Set.of(), Set.of());
            Ou ou2 = Ou.of(OuId.of(UUID.randomUUID()), name, parentId, Set.of(), Set.of(), Set.of());

            assertThat(ou1).isNotEqualTo(ou2);
        }
    }
}
