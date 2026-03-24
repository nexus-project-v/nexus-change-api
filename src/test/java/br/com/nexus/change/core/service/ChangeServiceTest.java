package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeServiceTest {

    @Mock
    private ChangeRepositoryPort changeRepositoryPort;

    @Mock
    private ChangeLogRepositoryPort changeLogRepositoryPort;

    @InjectMocks
    private ChangeService changeService;

    private Change change(UUID id, String title, UUID componentId, String status) {
        return Change.builder()
                .id(id)
                .title(title)
                .description("Descricao " + title)
                .componentId(componentId)
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    @Test
    @DisplayName("save should default status to DRAFT and create changelog")
    void save_shouldDefaultStatusAndCreateChangeLog() {
        UUID id = UUID.randomUUID();
        Change input = change(null, "Deploy API", UUID.randomUUID(), null);
        Change persisted = change(id, "Deploy API", input.getComponentId(), ChangeStatus.DRAFT.name());

        when(changeRepositoryPort.save(input)).thenReturn(persisted);

        Change result = changeService.save(input);

        assertThat(result).isEqualTo(persisted);
        assertThat(input.getChangeStatus()).isEqualTo(ChangeStatus.DRAFT.name());

        ArgumentCaptor<ChangeLog> captor = ArgumentCaptor.forClass(ChangeLog.class);
        verify(changeLogRepositoryPort, times(1)).save(captor.capture());
        assertThat(captor.getValue().getChangeId()).isEqualTo(id);
        assertThat(captor.getValue().getChangeStatus()).isEqualTo(ChangeStatus.DRAFT.name());
        verify(changeRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("save should keep informed status and create changelog")
    void save_shouldKeepInformedStatusAndCreateChangeLog() {
        UUID id = UUID.randomUUID();
        Change input = change(null, "Deploy API", UUID.randomUUID(), ChangeStatus.VALIDATED.name());
        Change persisted = change(id, "Deploy API", input.getComponentId(), ChangeStatus.VALIDATED.name());

        when(changeRepositoryPort.save(input)).thenReturn(persisted);

        Change result = changeService.save(input);

        assertThat(result).isEqualTo(persisted);
        assertThat(input.getChangeStatus()).isEqualTo(ChangeStatus.VALIDATED.name());

        ArgumentCaptor<ChangeLog> captor = ArgumentCaptor.forClass(ChangeLog.class);
        verify(changeLogRepositoryPort, times(1)).save(captor.capture());
        assertThat(captor.getValue().getChangeId()).isEqualTo(id);
        assertThat(captor.getValue().getChangeStatus()).isEqualTo(ChangeStatus.VALIDATED.name());
    }

    @Test
    @DisplayName("save should return null and not create changelog when repository returns null")
    void save_shouldReturnNullAndNotCreateChangeLogWhenRepositoryReturnsNull() {
        Change input = change(null, "Deploy API", UUID.randomUUID(), "");
        when(changeRepositoryPort.save(input)).thenReturn(null);

        Change result = changeService.save(input);

        assertThat(result).isNull();
        assertThat(input.getChangeStatus()).isEqualTo(ChangeStatus.DRAFT.name());
        verify(changeRepositoryPort, times(1)).save(input);
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
    }

    @Test
    @DisplayName("update should merge fields and save when change exists")
    void update_shouldMergeFieldsAndSaveWhenExists() {
        UUID id = UUID.randomUUID();
        Change existing = change(id, "Old title", UUID.randomUUID(), ChangeStatus.CREATED.name());
        Change patch = change(null, "New title", UUID.randomUUID(), ChangeStatus.ROLLBACK.name());

        when(changeRepositoryPort.findById(id)).thenReturn(existing);
        when(changeRepositoryPort.save(any(Change.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Change result = changeService.update(id, patch);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getDescription()).isEqualTo("Descricao New title");
        assertThat(result.getComponentId()).isEqualTo(patch.getComponentId());
        assertThat(result.getChangeStatus()).isEqualTo(ChangeStatus.CREATED.name());

        verify(changeRepositoryPort, times(1)).findById(id);
        verify(changeRepositoryPort, times(1)).save(existing);
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
    }

    @Test
    @DisplayName("update should return null when change does not exist")
    void update_shouldReturnNullWhenChangeNotFound() {
        UUID id = UUID.randomUUID();
        Change patch = change(null, "New title", UUID.randomUUID(), ChangeStatus.VALIDATED.name());

        when(changeRepositoryPort.findById(id)).thenReturn(null);

        Change result = changeService.update(id, patch);

        assertThat(result).isNull();
        verify(changeRepositoryPort, times(1)).findById(id);
        verify(changeRepositoryPort, never()).save(any(Change.class));
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
    }

    @Test
    @DisplayName("updateStatus should persist new status and create changelog")
    void updateStatus_shouldPersistNewStatusAndCreateChangeLog() {
        UUID id = UUID.randomUUID();
        String newStatus = ChangeStatus.DEPLOYED.name();
        Change existing = change(id, "Deploy API", UUID.randomUUID(), ChangeStatus.CREATED.name());
        Change persisted = change(id, "Deploy API", existing.getComponentId(), newStatus);

        when(changeRepositoryPort.findById(id)).thenReturn(existing);
        when(changeRepositoryPort.save(existing)).thenReturn(persisted);

        Change result = changeService.updateStatus(id, newStatus);

        assertThat(result).isEqualTo(persisted);
        assertThat(existing.getChangeStatus()).isEqualTo(newStatus);

        ArgumentCaptor<ChangeLog> captor = ArgumentCaptor.forClass(ChangeLog.class);
        verify(changeLogRepositoryPort, times(1)).save(captor.capture());
        assertThat(captor.getValue().getChangeId()).isEqualTo(id);
        assertThat(captor.getValue().getChangeStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("updateStatus should throw ResourceFoundException when change does not exist")
    void updateStatus_shouldThrowWhenChangeNotFound() {
        UUID id = UUID.randomUUID();
        when(changeRepositoryPort.findById(id)).thenReturn(null);

        ResourceFoundException ex = assertThrows(ResourceFoundException.class,
                () -> changeService.updateStatus(id, ChangeStatus.DEPLOYED.name()));

        assertThat(ex.getMessage()).isEqualTo("Change not found");
        verify(changeRepositoryPort, times(1)).findById(id);
        verify(changeRepositoryPort, never()).save(any(Change.class));
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
    }

    @Test
    @DisplayName("updateStatus should return null and not create changelog when save returns null")
    void updateStatus_shouldReturnNullWhenSaveReturnsNull() {
        UUID id = UUID.randomUUID();
        Change existing = change(id, "Deploy API", UUID.randomUUID(), ChangeStatus.CREATED.name());

        when(changeRepositoryPort.findById(id)).thenReturn(existing);
        when(changeRepositoryPort.save(existing)).thenReturn(null);

        Change result = changeService.updateStatus(id, ChangeStatus.DEPLOYED.name());

        assertThat(result).isNull();
        verify(changeRepositoryPort, times(1)).findById(id);
        verify(changeRepositoryPort, times(1)).save(existing);
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
    }

    @Test
    @DisplayName("findById should delegate to repository")
    void findById_shouldDelegateToRepository() {
        UUID id = UUID.randomUUID();
        Change existing = change(id, "Deploy API", UUID.randomUUID(), ChangeStatus.CREATED.name());
        when(changeRepositoryPort.findById(id)).thenReturn(existing);

        Change result = changeService.findById(id);

        assertThat(result).isEqualTo(existing);
        verify(changeRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("findAll should delegate to repository")
    void findAll_shouldDelegateToRepository() {
        List<Change> list = List.of(
                change(UUID.randomUUID(), "A", UUID.randomUUID(), ChangeStatus.CREATED.name()),
                change(UUID.randomUUID(), "B", UUID.randomUUID(), ChangeStatus.VALIDATED.name())
        );
        when(changeRepositoryPort.findAll()).thenReturn(list);

        List<Change> result = changeService.findAll();

        assertThat(result).isEqualTo(list);
        verify(changeRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("remove should delete and return true when change exists")
    void remove_shouldDeleteAndReturnTrueWhenExists() {
        UUID id = UUID.randomUUID();
        Change existing = change(id, "Deploy API", UUID.randomUUID(), ChangeStatus.CREATED.name());
        when(changeRepositoryPort.findById(id)).thenReturn(existing);

        boolean result = changeService.remove(id);

        assertThat(result).isTrue();
        verify(changeRepositoryPort, times(1)).findById(id);
        verify(changeRepositoryPort, times(1)).remove(id);
    }

    @Test
    @DisplayName("remove should throw ResourceFoundException when change does not exist")
    void remove_shouldThrowWhenChangeNotFound() {
        UUID id = UUID.randomUUID();
        when(changeRepositoryPort.findById(id)).thenReturn(null);

        ResourceFoundException ex = assertThrows(ResourceFoundException.class,
                () -> changeService.remove(id));

        assertThat(ex.getMessage()).isEqualTo("Change not found");
        verify(changeRepositoryPort, times(1)).findById(id);
        verify(changeRepositoryPort, never()).remove(id);
    }

    @Test
    @DisplayName("updateStatusById should return null because it is not implemented")
    void updateStatusById_shouldReturnNull() {
        Change result = changeService.updateStatusById(UUID.randomUUID(), ChangeStatus.DEPLOYED.name());

        assertThat(result).isNull();
    }
}

