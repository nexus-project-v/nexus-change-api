package br.com.nexus.change.core.service;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.core.ports.out.ScheduledWindowRepositoryPort;
import br.com.nexus.change.core.ports.out.event.ChangeEventPublisher;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledWindowServiceTest {

    @Mock
    private ScheduledWindowRepositoryPort scheduledWindowRepositoryPort;

    @Mock
    private ChangeRepositoryPort changeRepositoryPort;

    @Mock
    private ChangeLogRepositoryPort changeLogRepositoryPort;

    @Mock
    private ComponentService componentService;

    @Mock
    private ChangeEventPublisher changeEventPublisher;

    @InjectMocks
    private ScheduledWindowService scheduledWindowService;

    private ScheduledWindow scheduledWindow(UUID id, UUID changeId) {
        return ScheduledWindow.builder()
                .id(id)
                .responsible("time-infra@nexus.com")
                .start(LocalDateTime.of(2026, 3, 25, 22, 0))
                .end(LocalDateTime.of(2026, 3, 26, 0, 0))
                .changeId(changeId)
                .build();
    }

    private Change change(UUID id, UUID componentId, String status) {
        return Change.builder()
                .id(id)
                .title("Deploy API")
                .description("Deploy API v2")
                .componentId(componentId)
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    private ChangeComponent component(UUID id, String name, String version) {
        return ChangeComponent.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }

    @Test
    @DisplayName("save should update change, create changelog, publish event and save scheduled window")
    void save_shouldUpdateChangeCreateLogPublishAndSaveWindow() {
        UUID changeId = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        UUID windowId = UUID.randomUUID();

        ScheduledWindow input = scheduledWindow(null, changeId);
        ScheduledWindow persistedWindow = scheduledWindow(windowId, changeId);

        Change change = change(changeId, componentId, "DRAFT");
        Change updated = change(changeId, componentId, ChangeStatus.CREATED.name());
        ChangeComponent component = component(componentId, "core-api", "1.2.3");

        when(changeRepositoryPort.findById(changeId)).thenReturn(change);
        when(changeRepositoryPort.update(eq(changeId), any(Change.class))).thenReturn(updated);
        when(componentService.findById(componentId)).thenReturn(component);
        when(scheduledWindowRepositoryPort.save(input)).thenReturn(persistedWindow);

        ScheduledWindow result = scheduledWindowService.save(input);

        assertThat(result).isEqualTo(persistedWindow);

        verify(changeRepositoryPort, times(1)).findById(changeId);
        verify(changeRepositoryPort, times(1)).update(eq(changeId), any(Change.class));

        ArgumentCaptor<ChangeLog> logCaptor = ArgumentCaptor.forClass(ChangeLog.class);
        verify(changeLogRepositoryPort, times(1)).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getChangeId()).isEqualTo(changeId);
        assertThat(logCaptor.getValue().getChangeStatus()).isEqualTo(ChangeStatus.CREATED.name());

        ArgumentCaptor<ChangePreparedPayload> payloadCaptor = ArgumentCaptor.forClass(ChangePreparedPayload.class);
        verify(changeEventPublisher, times(1)).publish(payloadCaptor.capture());
        assertThat(payloadCaptor.getValue().getChangeId()).isEqualTo(changeId);
        assertThat(payloadCaptor.getValue().getComponent()).isEqualTo("core-api");
        assertThat(payloadCaptor.getValue().getComponentVersion()).isEqualTo("1.2.3");
        assertThat(payloadCaptor.getValue().getEnvironment()).isEqualTo("PROD");
        assertThat(payloadCaptor.getValue().getChangeType()).isEqualTo("NORMAL");
        assertThat(payloadCaptor.getValue().getChangeStatus()).isEqualTo(ChangeStatus.CREATED.name());

        verify(scheduledWindowRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("save should still persist scheduled window when change is not found")
    void save_shouldPersistWindowWhenChangeNotFound() {
        UUID changeId = UUID.randomUUID();
        ScheduledWindow input = scheduledWindow(null, changeId);
        ScheduledWindow persisted = scheduledWindow(UUID.randomUUID(), changeId);

        when(changeRepositoryPort.findById(changeId)).thenReturn(null);
        when(scheduledWindowRepositoryPort.save(input)).thenReturn(persisted);

        ScheduledWindow result = scheduledWindowService.save(input);

        assertThat(result).isEqualTo(persisted);
        verify(changeRepositoryPort, times(1)).findById(changeId);
        verify(changeRepositoryPort, never()).update(eq(changeId), any(Change.class));
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
        verify(changeEventPublisher, never()).publish(any(ChangePreparedPayload.class));
        verify(scheduledWindowRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("save should create changelog but not publish event when component is not found")
    void save_shouldCreateChangeLogButNotPublishWhenComponentNotFound() {
        UUID changeId = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        ScheduledWindow input = scheduledWindow(null, changeId);

        Change found = change(changeId, componentId, "DRAFT");
        Change updated = change(changeId, componentId, ChangeStatus.CREATED.name());

        when(changeRepositoryPort.findById(changeId)).thenReturn(found);
        when(changeRepositoryPort.update(eq(changeId), any(Change.class))).thenReturn(updated);
        when(componentService.findById(componentId)).thenReturn(null);
        when(scheduledWindowRepositoryPort.save(input)).thenReturn(scheduledWindow(UUID.randomUUID(), changeId));

        ScheduledWindow result = scheduledWindowService.save(input);

        assertThat(result).isNotNull();
        verify(changeLogRepositoryPort, times(1)).save(any(ChangeLog.class));
        verify(changeEventPublisher, never()).publish(any(ChangePreparedPayload.class));
    }

    @Test
    @DisplayName("save should not create changelog or publish event when change update returns null")
    void save_shouldNotCreateSideEffectsWhenChangeUpdateReturnsNull() {
        UUID changeId = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();
        ScheduledWindow input = scheduledWindow(null, changeId);

        Change found = change(changeId, componentId, "DRAFT");

        when(changeRepositoryPort.findById(changeId)).thenReturn(found);
        when(changeRepositoryPort.update(eq(changeId), any(Change.class))).thenReturn(null);
        when(scheduledWindowRepositoryPort.save(input)).thenReturn(scheduledWindow(UUID.randomUUID(), changeId));

        ScheduledWindow result = scheduledWindowService.save(input);

        assertThat(result).isNotNull();
        verify(componentService, never()).findById(any(UUID.class));
        verify(changeLogRepositoryPort, never()).save(any(ChangeLog.class));
        verify(changeEventPublisher, never()).publish(any(ChangePreparedPayload.class));
    }

    @Test
    @DisplayName("update should merge and save when scheduled window exists")
    void update_shouldMergeAndSaveWhenExists() {
        UUID id = UUID.randomUUID();
        UUID oldChangeId = UUID.randomUUID();
        UUID newChangeId = UUID.randomUUID();

        ScheduledWindow existing = scheduledWindow(id, oldChangeId);
        ScheduledWindow patch = ScheduledWindow.builder()
                .responsible("time-security@nexus.com")
                .start(LocalDateTime.of(2026, 4, 1, 10, 0))
                .end(LocalDateTime.of(2026, 4, 1, 12, 0))
                .changeId(newChangeId)
                .build();

        when(scheduledWindowRepositoryPort.findById(id)).thenReturn(existing);
        when(scheduledWindowRepositoryPort.save(existing)).thenReturn(existing);

        ScheduledWindow result = scheduledWindowService.update(id, patch);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getResponsible()).isEqualTo("time-security@nexus.com");
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2026, 4, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2026, 4, 1, 12, 0));
        assertThat(result.getChangeId()).isEqualTo(newChangeId);

        verify(scheduledWindowRepositoryPort, times(1)).findById(id);
        verify(scheduledWindowRepositoryPort, times(1)).save(existing);
    }

    @Test
    @DisplayName("update should return null when scheduled window does not exist")
    void update_shouldReturnNullWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(scheduledWindowRepositoryPort.findById(id)).thenReturn(null);

        ScheduledWindow result = scheduledWindowService.update(id, scheduledWindow(null, UUID.randomUUID()));

        assertThat(result).isNull();
        verify(scheduledWindowRepositoryPort, times(1)).findById(id);
        verify(scheduledWindowRepositoryPort, never()).save(any(ScheduledWindow.class));
    }

    @Test
    @DisplayName("findById should delegate to repository")
    void findById_shouldDelegate() {
        UUID id = UUID.randomUUID();
        ScheduledWindow existing = scheduledWindow(id, UUID.randomUUID());
        when(scheduledWindowRepositoryPort.findById(id)).thenReturn(existing);

        ScheduledWindow result = scheduledWindowService.findById(id);

        assertThat(result).isEqualTo(existing);
        verify(scheduledWindowRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("findAll should delegate to repository")
    void findAll_shouldDelegate() {
        List<ScheduledWindow> windows = List.of(
                scheduledWindow(UUID.randomUUID(), UUID.randomUUID()),
                scheduledWindow(UUID.randomUUID(), UUID.randomUUID())
        );
        when(scheduledWindowRepositoryPort.findAll()).thenReturn(windows);

        List<ScheduledWindow> result = scheduledWindowService.findAll();

        assertThat(result).isEqualTo(windows);
        verify(scheduledWindowRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("remove should return true when scheduled window exists")
    void remove_shouldReturnTrueWhenExists() {
        UUID id = UUID.randomUUID();
        when(scheduledWindowRepositoryPort.findById(id)).thenReturn(scheduledWindow(id, UUID.randomUUID()));

        boolean result = scheduledWindowService.remove(id);

        assertThat(result).isTrue();
        verify(scheduledWindowRepositoryPort, times(1)).findById(id);
        verify(scheduledWindowRepositoryPort, times(1)).remove(id);
    }

    @Test
    @DisplayName("remove should return false when scheduled window does not exist")
    void remove_shouldReturnFalseWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(scheduledWindowRepositoryPort.findById(id)).thenReturn(null);

        boolean result = scheduledWindowService.remove(id);

        assertThat(result).isFalse();
        verify(scheduledWindowRepositoryPort, times(1)).findById(id);
        verify(scheduledWindowRepositoryPort, never()).remove(id);
    }
}

