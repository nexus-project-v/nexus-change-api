package br.com.nexus.change.service;

import br.com.nexus.change.application.event.dto.ChangePreparedPayload;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.out.ChangeLogRepositoryPort;
import br.com.nexus.change.core.ports.out.ChangeRepositoryPort;
import br.com.nexus.change.core.ports.out.event.ChangeEventPublisher;
import br.com.nexus.change.core.service.ChangeService;
import br.com.nexus.change.core.service.ComponentService;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeServiceTest {

    @InjectMocks
    private ChangeService changeService;

    @Mock
    private ChangeRepositoryPort changeRepositoryPort;

    @Mock
    private ChangeLogRepositoryPort changeLogRepositoryPort;

    @Mock
    private ChangeEventPublisher changeEventPublisher;

    @Mock
    private ComponentService componentService;

    @Test
    void saveShouldRejectMissingRequiredFieldsBeforePersisting() {
        Change invalidChange = Change.builder()
                .componentId(UUID.randomUUID())
                .build();

        ResourceFoundException exception = assertThrows(ResourceFoundException.class,
                () -> changeService.save(invalidChange));

        assertEquals("Campos obrigatórios da change não informados: title, environment, changeType, requestBy", exception.getMessage());
        verify(changeRepositoryPort, never()).save(any(Change.class));
        verify(changeEventPublisher, never()).publish(any(ChangePreparedPayload.class));
    }

    @Test
    void updateShouldRejectMissingRequiredFieldsBeforePersisting() {
        UUID changeId = UUID.randomUUID();
        Change invalidChange = Change.builder()
                .title("   ")
                .componentId(null)
                .environment("PROD")
                .changeType("STANDARD")
                .requestBy("roger")
                .build();

        ResourceFoundException exception = assertThrows(ResourceFoundException.class,
                () -> changeService.update(changeId, invalidChange));

        assertEquals("Campos obrigatórios da change não informados: title, componentId", exception.getMessage());
        verify(changeRepositoryPort, never()).findById(changeId);
        verify(changeRepositoryPort, never()).save(any(Change.class));
    }

    @Test
    void saveShouldSetDraftStatusAndPersistValidChange() {
        UUID changeId = UUID.randomUUID();
        UUID componentId = UUID.randomUUID();

        Change change = Change.builder()
                .title("Atualização cluster Kubernetes")
                .description("Upgrade do cluster")
                .componentId(componentId)
                .environment("PROD")
                .changeType("STANDARD")
                .requestBy("roger")
                .build();

        Change savedChange = Change.builder()
                .id(changeId)
                .title(change.getTitle())
                .description(change.getDescription())
                .componentId(componentId)
                .environment(change.getEnvironment())
                .changeType(change.getChangeType())
                .changeStatus(ChangeStatus.DRAFT.name())
                .requestBy(change.getRequestBy())
                .build();

        ChangeComponent component = ChangeComponent.builder()
                .id(componentId)
                .name("nexus-change-api")
                .version("1.0.0")
                .build();

        when(changeRepositoryPort.save(change)).thenReturn(savedChange);
        when(componentService.findById(componentId)).thenReturn(component);
        when(changeLogRepositoryPort.save(any(ChangeLog.class))).thenReturn(ChangeLog.builder().changeId(changeId).build());

        Change result = changeService.save(change);

        assertNotNull(result);
        assertEquals(ChangeStatus.DRAFT.name(), change.getChangeStatus());
        assertEquals(ChangeStatus.DRAFT.name(), result.getChangeStatus());
        verify(changeRepositoryPort).save(change);
        verify(componentService).findById(componentId);
        verify(changeEventPublisher).publish(any(ChangePreparedPayload.class));
        verify(changeLogRepositoryPort).save(any(ChangeLog.class));
    }
}

