package com.example.oulearning.organization.domain.hierarchy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.event.MemberAdded;
import com.example.oulearning.organization.domain.hierarchy.event.MemberRemoved;
import com.example.oulearning.organization.domain.hierarchy.event.OuCreated;
import com.example.oulearning.organization.domain.hierarchy.event.OuMoved;
import com.example.oulearning.organization.domain.hierarchy.event.OuNameChanged;
import com.example.oulearning.organization.domain.hierarchy.event.OwnerAdded;
import com.example.oulearning.organization.domain.hierarchy.event.OwnerRemoved;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OuTest {

    private final Id id = Id.of(UUID.randomUUID());
    private final Name name = Name.of("Software Engineering");
    private final Id parentId = Id.of(UUID.randomUUID());
    private final Instant now = Instant.parse("2026-08-19T10:00:00Z");
    private final com.example.oulearning.organization.domain.employee.Id emp1 =
            com.example.oulearning.organization.domain.employee.Id.of("EMP-001");
    private final com.example.oulearning.organization.domain.employee.Id emp2 =
            com.example.oulearning.organization.domain.employee.Id.of("EMP-002");

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create OU with parent and register OuCreated event")
        void should_createOu_withParent_and_registerEvent() {
            Ou ou = Ou.create(id, name, parentId, now);

            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).contains(parentId);
            assertThat(ou.childIds()).isEmpty();
            assertThat(ou.owners()).isEmpty();
            assertThat(ou.members()).isEmpty();

            List<Object> events = ou.pullDomainEvents();
            assertThat(events).containsExactly(new OuCreated(id, name, parentId, now));
            assertThat(ou.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should create root OU and register OuCreated event with null parent")
        void should_createRootOu_and_registerEvent() {
            Ou ou = Ou.createRoot(id, name, now);

            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).isEmpty();

            List<Object> events = ou.pullDomainEvents();
            assertThat(events).containsExactly(new OuCreated(id, name, null, now));
        }

        @Test
        @DisplayName("should throw CyclicHierarchyException when creating OU with itself as parent")
        void should_throwException_when_parentIsSelf() {
            assertThatThrownBy(() -> Ou.create(id, name, id, now))
                    .isInstanceOf(CyclicHierarchyException.class)
                    .hasMessageContaining("cannot be its own parent");
        }

        @Test
        @DisplayName("should throw CyclicHierarchyException when reconstituting OU with itself as child")
        void should_throwException_when_childIsSelf() {
            assertThatThrownBy(() -> Ou.reconstitute(id, name, parentId, Set.of(id), Set.of(), Set.of()))
                    .isInstanceOf(CyclicHierarchyException.class)
                    .hasMessageContaining("cannot be its own child");
        }

        @Test
        @DisplayName("should reconstitute OU without registering events")
        void should_reconstituteOu_withoutEvents() {
            Id childId = Id.of(UUID.randomUUID());
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(childId), Set.of(emp1), Set.of(emp2));

            assertThat(ou.id()).isEqualTo(id);
            assertThat(ou.name()).isEqualTo(name);
            assertThat(ou.parentId()).contains(parentId);
            assertThat(ou.childIds()).containsExactly(childId);
            assertThat(ou.owners()).containsExactly(emp1);
            assertThat(ou.members()).containsExactly(emp2);
            assertThat(ou.pullDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Hierarchy State Changes")
    class HierarchyStateChanges {

        @Test
        @DisplayName("should change name and register OuNameChanged event")
        void should_changeName_and_registerEvent() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());
            Name newName = Name.of("Platform Engineering");
            Instant changeTime = now.plusSeconds(3600);

            ou.changeName(newName, changeTime);

            assertThat(ou.name()).isEqualTo(newName);
            assertThat(ou.pullDomainEvents()).containsExactly(new OuNameChanged(id, name, newName, changeTime));
        }

        @Test
        @DisplayName("should move to new parent and register OuMoved event")
        void should_moveToParent_and_registerEvent() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());
            Id newParent = Id.of(UUID.randomUUID());
            Instant changeTime = now.plusSeconds(3600);

            ou.moveToParent(newParent, changeTime);

            assertThat(ou.parentId()).contains(newParent);
            assertThat(ou.pullDomainEvents()).containsExactly(new OuMoved(id, parentId, newParent, changeTime));
        }

        @Test
        @DisplayName("should throw CyclicHierarchyException when moving OU under itself")
        void should_throwException_when_movingUnderSelf() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());

            assertThatThrownBy(() -> ou.moveToParent(id, now))
                    .isInstanceOf(CyclicHierarchyException.class)
                    .hasMessageContaining("Cannot move organizational unit under itself");
        }

        @Test
        @DisplayName("should add and remove child OU")
        void should_addAndRemoveChild() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());
            Id childId = Id.of(UUID.randomUUID());

            ou.addChild(childId);
            assertThat(ou.childIds()).containsExactly(childId);

            ou.removeChild(childId);
            assertThat(ou.childIds()).isEmpty();
        }

        @Test
        @DisplayName("should throw CyclicHierarchyException when adding self as child")
        void should_throwException_when_addingSelfAsChild() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());

            assertThatThrownBy(() -> ou.addChild(id))
                    .isInstanceOf(CyclicHierarchyException.class)
                    .hasMessageContaining("Cannot add organizational unit as a child of itself");
        }
    }

    @Nested
    @DisplayName("Owner and Member Management")
    class OwnerAndMemberManagement {

        @Test
        @DisplayName("should add owner and register OwnerAdded event")
        void should_addOwner_and_registerEvent() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());

            ou.addOwner(emp1, now);

            assertThat(ou.owners()).containsExactly(emp1);
            assertThat(ou.pullDomainEvents()).containsExactly(new OwnerAdded(id, emp1, now));
        }

        @Test
        @DisplayName("should not register event when adding already existing owner")
        void should_notRegisterEvent_when_addingExistingOwner() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(emp1), Set.of());

            ou.addOwner(emp1, now);

            assertThat(ou.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should remove owner and register OwnerRemoved event")
        void should_removeOwner_and_registerEvent() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(emp1), Set.of());

            ou.removeOwner(emp1, now);

            assertThat(ou.owners()).isEmpty();
            assertThat(ou.pullDomainEvents()).containsExactly(new OwnerRemoved(id, emp1, now));
        }

        @Test
        @DisplayName("should add member and register MemberAdded event")
        void should_addMember_and_registerEvent() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());

            ou.addMember(emp2, now);

            assertThat(ou.members()).containsExactly(emp2);
            assertThat(ou.pullDomainEvents()).containsExactly(new MemberAdded(id, emp2, now));
        }

        @Test
        @DisplayName("should remove member and register MemberRemoved event")
        void should_removeMember_and_registerEvent() {
            Ou ou = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of(emp2));

            ou.removeMember(emp2, now);

            assertThat(ou.members()).isEmpty();
            assertThat(ou.pullDomainEvents()).containsExactly(new MemberRemoved(id, emp2, now));
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Ou ou1 = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());
            Ou ou2 = Ou.reconstitute(id, Name.of("Other Name"), null, Set.of(), Set.of(), Set.of());

            assertThat(ou1).isEqualTo(ou2);
            assertThat(ou1.hashCode()).isEqualTo(ou2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Ou ou1 = Ou.reconstitute(id, name, parentId, Set.of(), Set.of(), Set.of());
            Ou ou2 = Ou.reconstitute(Id.of(UUID.randomUUID()), name, parentId, Set.of(), Set.of(), Set.of());

            assertThat(ou1).isNotEqualTo(ou2);
        }
    }
}
