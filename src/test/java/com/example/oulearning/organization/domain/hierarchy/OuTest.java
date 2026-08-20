package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.employee.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOuException;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OuTest {

    private final OuId id = HierarchyTestFactory.randomOuId();
    private final Name name = HierarchyTestFactory.randomName();
    private final OuId parentId = HierarchyTestFactory.randomOuId();
    private final EmployeeId emp1 = EmployeeTestFactory.randomEmployeeId();
    private final EmployeeId emp2 = EmployeeTestFactory.randomEmployeeId();

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("given all valid fields, when creating Ou, then Ou is created successfully")
        void givenAllValidFields_whenCreatingOu_thenOuIsCreatedSuccessfully() {
            // given
            final var childId = HierarchyTestFactory.randomOuId();

            // when
            final var ou = Ou.of(id, name, parentId, Set.of(childId), Set.of(emp1), Set.of(emp2));

            // then
            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).contains(parentId);
            assertThat(ou.childIds()).containsExactly(childId);
            assertThat(ou.owners()).containsExactly(emp1);
            assertThat(ou.members()).containsExactly(emp2);
            assertThat(ou)
                    .hasToString("Ou[id=%s, name=%s, parentId=%s]".formatted(id, name, parentId));
        }

        @Test
        @DisplayName("given root OU parameters, when creating root Ou, then collections are empty")
        void givenRootOuParams_whenCreatingRootOu_thenCollectionsAreEmpty() {
            // given

            // when
            final var ou = Ou.of(id, name);

            // then
            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).isEmpty();
            assertThat(ou.childIds()).isEmpty();
            assertThat(ou.owners()).isEmpty();
            assertThat(ou.members()).isEmpty();
        }

        @Test
        @DisplayName("given null required parameters, when creating Ou, then throw InvalidOuException")
        void givenNullRequiredParams_whenCreatingOu_thenThrowInvalidOuException() {
            // given
            final var emptyOuSet = Set.<OuId>of();
            final var emptyEmployeeSet = Set.<com.example.oulearning.organization.domain.employee.EmployeeId>of();

            // when

            // then
            assertThatThrownBy(() -> new Ou(null, name, parentId, emptyOuSet, emptyEmployeeSet, emptyEmployeeSet))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> new Ou(id, null, parentId, emptyOuSet, emptyEmployeeSet, emptyEmployeeSet))
                    .isInstanceOf(InvalidOuException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("given OUs with same id, when comparing, then they are equal")
        void givenOusWithSameId_whenComparing_thenTheyAreEqual() {
            // given
            final var ou1 = Ou.of(id, name, parentId, Set.of(), Set.of(), Set.of());
            final var ou2 = Ou.of(id, HierarchyTestFactory.randomName(), null, Set.of(), Set.of(), Set.of());

            // when

            // then
            assertThat(ou1).isEqualTo(ou2).hasSameHashCodeAs(ou2);
        }

        @Test
        @DisplayName("given OUs with different ids, when comparing, then they are not equal")
        void givenOusWithDifferentIds_whenComparing_thenTheyAreNotEqual() {
            // given
            final var ou1 = Ou.of(id, name, parentId, Set.of(), Set.of(), Set.of());
            final var ou2 = Ou.of(
                    HierarchyTestFactory.randomOuId(), name, parentId, Set.of(), Set.of(), Set.of());

            // when

            // then
            assertThat(ou1).isNotEqualTo(ou2);
        }

        @Test
        @DisplayName("given same OU instance, when comparing, then they are equal")
        void givenSameOuInstance_whenComparing_thenTheyAreEqual() {
            // given
            final var ou = Ou.of(id, name);

            // when

            // then
            assertThat(ou).isEqualTo(ou);
        }

        @Test
        @DisplayName("given null or different object type, when comparing, then they are not equal")
        void givenNullOrDifferentType_whenComparing_thenTheyAreNotEqual() {
            // given
            final var ou = Ou.of(id, name);

            // when

            // then
            assertThat(ou).isNotEqualTo(null).isNotEqualTo(new Object());
        }
    }
}
