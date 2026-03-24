package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.out.ComponentRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {

    @Mock
    private ComponentRepositoryPort componentRepositoryPort;

    @InjectMocks
    private ComponentService componentService;

    private ChangeComponent component(UUID id, String name, String version) {
        return ChangeComponent.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }

    @Test
    @DisplayName("save should return persisted component")
    void save_shouldReturnPersistedComponent() {
        ChangeComponent input = component(null, "core-api", "1.0.0");
        ChangeComponent persisted = component(UUID.randomUUID(), "core-api", "1.0.0");

        when(componentRepositoryPort.save(input)).thenReturn(persisted);

        ChangeComponent result = componentService.save(input);

        assertThat(result).isEqualTo(persisted);
        verify(componentRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("save should return null when repository returns null")
    void save_shouldReturnNullWhenRepositoryReturnsNull() {
        ChangeComponent input = component(null, "core-api", "1.0.0");
        when(componentRepositoryPort.save(input)).thenReturn(null);

        ChangeComponent result = componentService.save(input);

        assertThat(result).isNull();
        verify(componentRepositoryPort, times(1)).save(input);
    }

    @Test
    @DisplayName("update should apply changes and save when component exists")
    void update_shouldApplyChangesAndSaveWhenComponentExists() {
        UUID id = UUID.randomUUID();
        ChangeComponent existing = component(id, "core-api", "1.0.0");
        ChangeComponent patch = component(null, "core-api", "1.1.0");

        when(componentRepositoryPort.findById(id)).thenReturn(existing);
        when(componentRepositoryPort.save(any(ChangeComponent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChangeComponent result = componentService.update(id, patch);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("core-api");
        assertThat(result.getVersion()).isEqualTo("1.1.0");
        verify(componentRepositoryPort, times(1)).findById(id);
        verify(componentRepositoryPort, times(1)).save(existing);
    }

    @Test
    @DisplayName("update should return null when component is not found")
    void update_shouldReturnNullWhenComponentNotFound() {
        UUID id = UUID.randomUUID();
        ChangeComponent patch = component(null, "core-api", "1.1.0");

        when(componentRepositoryPort.findById(id)).thenReturn(null);

        ChangeComponent result = componentService.update(id, patch);

        assertThat(result).isNull();
        verify(componentRepositoryPort, times(1)).findById(id);
        verify(componentRepositoryPort, never()).save(any(ChangeComponent.class));
    }

    @Test
    @DisplayName("findById should return component when found")
    void findById_shouldReturnComponentWhenFound() {
        UUID id = UUID.randomUUID();
        ChangeComponent existing = component(id, "core-api", "1.0.0");

        when(componentRepositoryPort.findById(id)).thenReturn(existing);

        ChangeComponent result = componentService.findById(id);

        assertThat(result).isEqualTo(existing);
        verify(componentRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById should return null when not found")
    void findById_shouldReturnNullWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(componentRepositoryPort.findById(id)).thenReturn(null);

        ChangeComponent result = componentService.findById(id);

        assertThat(result).isNull();
        verify(componentRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("findAll should return component list")
    void findAll_shouldReturnComponentList() {
        List<ChangeComponent> components = List.of(
                component(UUID.randomUUID(), "core-api", "1.0.0"),
                component(UUID.randomUUID(), "payments", "2.0.0")
        );

        when(componentRepositoryPort.findAll()).thenReturn(components);

        List<ChangeComponent> result = componentService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(components);
        verify(componentRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll should return null when repository throws")
    void findAll_shouldReturnNullWhenRepositoryThrows() {
        when(componentRepositoryPort.findAll()).thenThrow(new RuntimeException("db error"));

        List<ChangeComponent> result = componentService.findAll();

        assertThat(result).isNull();
        verify(componentRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("remove should return true when component exists")
    void remove_shouldReturnTrueWhenComponentExists() {
        UUID id = UUID.randomUUID();
        ChangeComponent existing = component(id, "core-api", "1.0.0");

        when(componentRepositoryPort.findById(id)).thenReturn(existing);
        doNothing().when(componentRepositoryPort).remove(id);

        boolean result = componentService.remove(id);

        assertThat(result).isTrue();
        verify(componentRepositoryPort, times(1)).findById(id);
        verify(componentRepositoryPort, times(1)).remove(id);
    }

    @Test
    @DisplayName("remove should return false when component does not exist")
    void remove_shouldReturnFalseWhenComponentDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(componentRepositoryPort.findById(id)).thenReturn(null);

        boolean result = componentService.remove(id);

        assertThat(result).isFalse();
        verify(componentRepositoryPort, times(1)).findById(id);
        verify(componentRepositoryPort, never()).remove(id);
    }

    @Test
    @DisplayName("remove should propagate unexpected runtime exception")
    void remove_shouldPropagateUnexpectedRuntimeException() {
        UUID id = UUID.randomUUID();
        ChangeComponent existing = component(id, "core-api", "1.0.0");

        when(componentRepositoryPort.findById(id)).thenReturn(existing);
        doThrow(new RuntimeException("infra error")).when(componentRepositoryPort).remove(id);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> componentService.remove(id));

        assertThat(ex).hasMessage("infra error");
        verify(componentRepositoryPort, times(1)).findById(id);
        verify(componentRepositoryPort, times(1)).remove(id);
    }

    @Test
    @DisplayName("remove should handle ResourceFoundException from repository")
    void remove_shouldHandleResourceFoundExceptionFromRepository() {
        UUID id = UUID.randomUUID();
        ChangeComponent existing = component(id, "core-api", "1.0.0");

        when(componentRepositoryPort.findById(id)).thenReturn(existing);
        doThrow(new ResourceFoundException("Component not found")).when(componentRepositoryPort).remove(id);

        boolean result = componentService.remove(id);

        assertThat(result).isFalse();
        verify(componentRepositoryPort, times(1)).findById(id);
        verify(componentRepositoryPort, times(1)).remove(id);
    }
}

