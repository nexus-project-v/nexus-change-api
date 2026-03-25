package br.com.nexus.change.core.service;

import br.com.nexus.change.application.api.dto.response.ChangeStatusResponse;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeStatusServiceTest {

    @Mock
    private ChangeRepository changeRepository;

    @Mock
    private ChangeLogRepositoryPort changeLogRepositoryPort;

    @InjectMocks
    private ChangeStatusService changeStatusService;

    private ChangeEntity changeEntity(UUID id, ChangeStatus status, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        ChangeEntity entity = ChangeEntity.builder()
                .id(id)
                .title("Deploy API")
                .description("Deploy v2")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
        entity.setCreatedDate(createdDate);
        entity.setLastModifiedDate(lastModifiedDate);
        return entity;
    }

    private ChangeLog changeLog(UUID changeId, String status, LocalDateTime createdDate) {
        return ChangeLog.builder()
                .id(UUID.randomUUID())
                .changeId(changeId)
                .changeStatus(status)
                .createdDate(createdDate)
                .build();
    }

    @Test
    @DisplayName("getStatus should use lastModifiedDate when present")
    void getStatus_shouldUseLastModifiedDateWhenPresent() {
        UUID changeId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.of(2026, 3, 24, 10, 0);
        LocalDateTime lastModifiedDate = LocalDateTime.of(2026, 3, 24, 12, 0);

        ChangeEntity entity = changeEntity(changeId, ChangeStatus.DEPLOYED, createdDate, lastModifiedDate);
        when(changeRepository.findById(changeId)).thenReturn(Optional.of(entity));
        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(List.of(
                changeLog(changeId, "CREATED", LocalDateTime.of(2026, 3, 24, 10, 15)),
                changeLog(changeId, "DEPLOYED", LocalDateTime.of(2026, 3, 24, 11, 30))
        ));

        ChangeStatusResponse result = changeStatusService.getStatus(changeId);

        Instant expectedUpdatedAt = lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant();
        assertThat(result.getChangeId()).isEqualTo(changeId);
        assertThat(result.getChangeStatus()).isEqualTo(ChangeStatus.DEPLOYED.name());
        assertThat(result.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
        assertThat(result.getTimeline()).hasSize(2);
        assertThat(result.getTimeline().get(0).getEvent()).isEqualTo("CREATED");
        assertThat(result.getTimeline().get(1).getEvent()).isEqualTo("DEPLOYED");
        assertThat(result.getLog_change()).hasSize(2);
        assertThat(result.getLog_change().get(0).getLevel()).isEqualTo("INFO");
        assertThat(result.getLog_change().get(1).getMessage()).isEqualTo("Status alterado para DEPLOYED");
        verify(changeRepository, times(1)).findById(changeId);
        verify(changeLogRepositoryPort, times(1)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("getStatus should fallback to createdDate when lastModifiedDate is null")
    void getStatus_shouldFallbackToCreatedDateWhenLastModifiedIsNull() {
        UUID changeId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.of(2026, 3, 24, 9, 30);

        ChangeEntity entity = changeEntity(changeId, ChangeStatus.CREATED, createdDate, null);
        when(changeRepository.findById(changeId)).thenReturn(Optional.of(entity));
        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(List.of());

        ChangeStatusResponse result = changeStatusService.getStatus(changeId);

        Instant expectedUpdatedAt = createdDate.atZone(ZoneId.systemDefault()).toInstant();
        assertThat(result.getChangeId()).isEqualTo(changeId);
        assertThat(result.getChangeStatus()).isEqualTo(ChangeStatus.CREATED.name());
        assertThat(result.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
        assertThat(result.getTimeline()).isEmpty();
        assertThat(result.getLog_change()).isEmpty();
        verify(changeRepository, times(1)).findById(changeId);
        verify(changeLogRepositoryPort, times(1)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("getStatus should return null changeStatus when entity status is null")
    void getStatus_shouldReturnNullChangeStatusWhenEntityStatusIsNull() {
        UUID changeId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.of(2026, 3, 24, 8, 0);

        ChangeEntity entity = changeEntity(changeId, null, createdDate, null);
        when(changeRepository.findById(changeId)).thenReturn(Optional.of(entity));
        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(null);

        ChangeStatusResponse result = changeStatusService.getStatus(changeId);

        assertThat(result.getChangeId()).isEqualTo(changeId);
        assertThat(result.getChangeStatus()).isNull();
        assertThat(result.getTimeline()).isEmpty();
        assertThat(result.getLog_change()).isEmpty();
        verify(changeRepository, times(1)).findById(changeId);
        verify(changeLogRepositoryPort, times(1)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("getStatus should throw when change id does not exist")
    void getStatus_shouldThrowWhenChangeIdDoesNotExist() {
        UUID changeId = UUID.randomUUID();
        when(changeRepository.findById(changeId)).thenReturn(Optional.empty());

        assertThrows(ResourceFoundException.class, () -> changeStatusService.getStatus(changeId));
        verify(changeRepository, times(1)).findById(changeId);
        verify(changeLogRepositoryPort, times(0)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("getStatus should use fallback timestamp and WARN level for rejected logs")
    void getStatus_shouldUseFallbackTimestampAndWarnLevelForRejectedLogs() {
        UUID changeId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.of(2026, 3, 24, 9, 0);
        LocalDateTime lastModifiedDate = LocalDateTime.of(2026, 3, 24, 10, 0);

        ChangeEntity entity = changeEntity(changeId, ChangeStatus.REJECTED, createdDate, lastModifiedDate);
        when(changeRepository.findById(changeId)).thenReturn(Optional.of(entity));
        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(List.of(
                changeLog(changeId, "REJECTED", null)
        ));

        ChangeStatusResponse result = changeStatusService.getStatus(changeId);

        Instant expectedUpdatedAt = lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant();
        assertThat(result.getUpdatedAt()).isEqualTo(expectedUpdatedAt);
        assertThat(result.getTimeline()).hasSize(1);
        assertThat(result.getTimeline().get(0).getTimestamp()).isEqualTo(expectedUpdatedAt);
        assertThat(result.getLog_change()).hasSize(1);
        assertThat(result.getLog_change().get(0).getLevel()).isEqualTo("WARN");
        assertThat(result.getLog_change().get(0).getMessage()).isEqualTo("Status alterado para REJECTED");
    }

    @Test
    @DisplayName("getStatus should default log message and level when change log status is null")
    void getStatus_shouldDefaultLogMessageAndLevelWhenChangeLogStatusIsNull() {
        UUID changeId = UUID.randomUUID();
        LocalDateTime createdDate = LocalDateTime.of(2026, 3, 24, 7, 30);

        ChangeEntity entity = changeEntity(changeId, ChangeStatus.CREATED, createdDate, null);
        when(changeRepository.findById(changeId)).thenReturn(Optional.of(entity));
        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(List.of(
                changeLog(changeId, null, createdDate.plusMinutes(5))
        ));

        ChangeStatusResponse result = changeStatusService.getStatus(changeId);

        assertThat(result.getTimeline()).hasSize(1);
        assertThat(result.getTimeline().get(0).getEvent()).isNull();
        assertThat(result.getLog_change()).hasSize(1);
        assertThat(result.getLog_change().get(0).getLevel()).isEqualTo("INFO");
        assertThat(result.getLog_change().get(0).getMessage()).isEqualTo("Status não informado");
    }
}

