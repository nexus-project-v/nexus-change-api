package br.com.nexus.change.core.service;

import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeLogServiceTest {

    @Mock
    private ChangeLogRepositoryPort changeLogRepositoryPort;

    @InjectMocks
    private ChangeLogService changeLogService;

    private ChangeLog changeLog(UUID id, UUID changeId, String status) {
        return ChangeLog.builder()
                .id(id)
                .changeId(changeId)
                .changeStatus(status)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("save should return persisted changelog")
    void save_shouldReturnPersistedChangeLog() {
        ChangeLog input = changeLog(null, UUID.randomUUID(), "CREATED");
        ChangeLog persisted = changeLog(UUID.randomUUID(), input.getChangeId(), "CREATED");

        when(changeLogRepositoryPort.save(input)).thenReturn(persisted);

        ChangeLog result = changeLogService.save(input);

        assertThat(result).isEqualTo(persisted);
        verify(changeLogRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("save should return null when repository returns null")
    void save_shouldReturnNullWhenRepositoryReturnsNull() {
        ChangeLog input = changeLog(null, UUID.randomUUID(), "CREATED");

        when(changeLogRepositoryPort.save(input)).thenReturn(null);

        ChangeLog result = changeLogService.save(input);

        assertThat(result).isNull();
        verify(changeLogRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("save should handle null input")
    void save_shouldHandleNullInput() {
        when(changeLogRepositoryPort.save(null)).thenReturn(null);

        ChangeLog result = changeLogService.save(null);

        assertThat(result).isNull();
        verify(changeLogRepositoryPort, times(1)).save(null);
    }

    @Test
    @DisplayName("findById should return changelog when found")
    void findById_shouldReturnChangeLogWhenFound() {
        UUID id = UUID.randomUUID();
        ChangeLog existing = changeLog(id, UUID.randomUUID(), "DEPLOYED");

        when(changeLogRepositoryPort.findById(id)).thenReturn(existing);

        ChangeLog result = changeLogService.findById(id);

        assertThat(result).isEqualTo(existing);
        verify(changeLogRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById should return null when not found")
    void findById_shouldReturnNullWhenNotFound() {
        UUID id = UUID.randomUUID();

        when(changeLogRepositoryPort.findById(id)).thenReturn(null);

        ChangeLog result = changeLogService.findById(id);

        assertThat(result).isNull();
        verify(changeLogRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById should handle null id")
    void findById_shouldHandleNullId() {
        when(changeLogRepositoryPort.findById(null)).thenReturn(null);

        ChangeLog result = changeLogService.findById(null);

        assertThat(result).isNull();
        verify(changeLogRepositoryPort, times(1)).findById(null);
    }

    @Test
    @DisplayName("findByStatus should return changelog list")
    void findByStatus_shouldReturnChangeLogList() {
        UUID changeId = UUID.randomUUID();
        List<ChangeLog> logs = List.of(
                changeLog(UUID.randomUUID(), changeId, "CREATED"),
                changeLog(UUID.randomUUID(), changeId, "DEPLOYED")
        );

        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(logs);

        List<ChangeLog> result = changeLogService.findByStatus(changeId);

        assertThat(result).isEqualTo(logs);
        assertThat(result).hasSize(2);
        verify(changeLogRepositoryPort, times(1)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("findByStatus should return empty list when repository returns empty")
    void findByStatus_shouldReturnEmptyList() {
        UUID changeId = UUID.randomUUID();

        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(List.of());

        List<ChangeLog> result = changeLogService.findByStatus(changeId);

        assertThat(result).isEmpty();
        verify(changeLogRepositoryPort, times(1)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("findByStatus should return null when repository returns null")
    void findByStatus_shouldReturnNullWhenRepositoryReturnsNull() {
        UUID changeId = UUID.randomUUID();

        when(changeLogRepositoryPort.findByChangeId(changeId)).thenReturn(null);

        List<ChangeLog> result = changeLogService.findByStatus(changeId);

        assertThat(result).isNull();
        verify(changeLogRepositoryPort, times(1)).findByChangeId(changeId);
    }

    @Test
    @DisplayName("findByStatus should handle null id")
    void findByStatus_shouldHandleNullId() {
        when(changeLogRepositoryPort.findByChangeId(null)).thenReturn(List.of());

        List<ChangeLog> result = changeLogService.findByStatus(null);

        assertThat(result).isEmpty();
        verify(changeLogRepositoryPort, times(1)).findByChangeId(null);
    }
}

