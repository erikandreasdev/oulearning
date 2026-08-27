package com.example.oulearning.organization.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.oulearning.organization.application.hierarchy.port.in.command.AssignMemberCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.command.CreateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.command.UpdateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.AssignMemberUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.AssignOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.CreateOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.DeleteOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.RemoveMemberUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.RemoveOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.UpdateOrganizationalUnitUseCase;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.infrastructure.web.dto.AssignEmployeeRequest;
import com.example.oulearning.organization.infrastructure.web.dto.CreateOrganizationalUnitRequest;
import com.example.oulearning.organization.infrastructure.web.dto.UpdateOrganizationalUnitRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrganizationalUnitController.class)
class OrganizationalUnitControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateOrganizationalUnitUseCase createOrganizationalUnitUseCase;

    @MockitoBean
    private GetOrganizationalUnitUseCase getOrganizationalUnitUseCase;

    @MockitoBean
    private UpdateOrganizationalUnitUseCase updateOrganizationalUnitUseCase;

    @MockitoBean
    private DeleteOrganizationalUnitUseCase deleteOrganizationalUnitUseCase;

    @MockitoBean
    private AssignMemberUseCase assignMemberUseCase;

    @MockitoBean
    private RemoveMemberUseCase removeMemberUseCase;

    @MockitoBean
    private AssignOwnerUseCase assignOwnerUseCase;

    @MockitoBean
    private RemoveOwnerUseCase removeOwnerUseCase;

    @Test
    @DisplayName("given valid request, when creating OU, then returns 201")
    void givenValidRequest_whenCreatingOu_thenReturns201() throws Exception {
        // given
        final var request = new CreateOrganizationalUnitRequest();
        request.setName("Engineering");

        final var ouId = new OrganizationalUnitId(1L);
        final var ou = OrganizationalUnit.reconstitute(
                ouId,
                new Name("Engineering"),
                null,
                Set.of(),
                Set.of(),
                Set.of(),
                true);

        given(createOrganizationalUnitUseCase.execute(any(CreateOrganizationalUnitCommand.class))).willReturn(ouId);
        given(getOrganizationalUnitUseCase.execute(ouId)).willReturn(ou);

        // when
        final var result = mockMvc.perform(post("/api/v1/organizational-units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("given existing OU id, when getting OU, then returns 200")
    void givenExistingOuId_whenGettingOu_thenReturns200() throws Exception {
        // given
        final var ouId = new OrganizationalUnitId(1L);
        final var ou = OrganizationalUnit.reconstitute(
                ouId,
                new Name("Engineering"),
                null,
                Set.of(new OrganizationalUnitId(2L)),
                Set.of(new EmployeeId(10L)),
                Set.of(new EmployeeId(20L)),
                true);

        given(getOrganizationalUnitUseCase.execute(ouId)).willReturn(ou);

        // when
        final var result = mockMvc.perform(get("/api/v1/organizational-units/{id}", 1L));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.childIds[0]").value(2))
                .andExpect(jsonPath("$.owners[0]").value(10))
                .andExpect(jsonPath("$.members[0]").value(20));
    }

    @Test
    @DisplayName("given valid request, when updating OU, then returns 200")
    void givenValidRequest_whenUpdatingOu_thenReturns200() throws Exception {
        // given
        final var request = new UpdateOrganizationalUnitRequest();
        request.setName("Sales");

        final var ouId = new OrganizationalUnitId(1L);
        final var ou = OrganizationalUnit.reconstitute(
                ouId,
                new Name("Sales"),
                null,
                Set.of(),
                Set.of(),
                Set.of(),
                true);

        given(getOrganizationalUnitUseCase.execute(ouId)).willReturn(ou);

        // when
        final var result = mockMvc.perform(put("/api/v1/organizational-units/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sales"));

        verify(updateOrganizationalUnitUseCase).execute(any(UpdateOrganizationalUnitCommand.class));
    }

    @Test
    @DisplayName("given existing OU id, when deleting OU, then returns 204")
    void givenExistingOuId_whenDeletingOu_thenReturns204() throws Exception {
        // given
        final var ouId = new OrganizationalUnitId(1L);

        // when
        final var result = mockMvc.perform(delete("/api/v1/organizational-units/{id}", 1L));

        // then
        result.andExpect(status().isNoContent());
        verify(deleteOrganizationalUnitUseCase).execute(ouId);
    }

    @Test
    @DisplayName("given assign member request, when assigning member, then returns 204")
    void givenAssignMemberRequest_whenAssigningMember_thenReturns204() throws Exception {
        // given
        final var request = new AssignEmployeeRequest();
        request.setEmployeeId(20L);

        // when
        final var result = mockMvc.perform(post("/api/v1/organizational-units/{id}/members", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent());
        verify(assignMemberUseCase).execute(any(AssignMemberCommand.class));
    }
}
