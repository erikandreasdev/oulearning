package com.example.oulearning.training.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.training.application.port.in.command.ListTrainingRequestsQuery;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.repository.TrainingFilterCriteria;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ListTrainingRequestsServiceTest {

    private final TrainingRepository trainingRepository = mock(TrainingRepository.class);
    private final ListTrainingRequestsService service = new ListTrainingRequestsService(trainingRepository);

    @Test
    @DisplayName("given filter query, when listing training requests, then return paginated results")
    void givenFilterQuery_whenListingTrainingRequests_thenReturnPaginatedResults() {
        // given
        final var training = TrainingTestFactory.randomTraining();
        when(trainingRepository.findAll(any(TrainingFilterCriteria.class), eq(0), eq(10)))
                .thenReturn(List.of(training));
        when(trainingRepository.count(any(TrainingFilterCriteria.class))).thenReturn(1L);

        final var query = new ListTrainingRequestsQuery(
                "Java",
                BigDecimal.valueOf(500),
                HierarchyTestFactory.randomOrganizationalUnitId(),
                TrainingPurposeType.DEPARTMENT_GOALS,
                TrainingTestFactory.randomTypeId(),
                20,
                TrainingStatus.REQUESTED,
                0,
                10);

        // when
        final var result = service.execute(query);

        // then
        assertThat(result.items()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
    }
}
