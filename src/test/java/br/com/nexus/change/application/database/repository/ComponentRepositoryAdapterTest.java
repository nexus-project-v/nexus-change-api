package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ComponentMapper;
import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.ComponentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentRepositoryAdapterTest {

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private ComponentMapper componentMapper;

    @InjectMocks
    private ComponentRepositoryAdapter componentRepositoryAdapter;

    private ChangeComponent model(UUID id, String name, String version) {
        return ChangeComponent.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }

    private ComponentEntity entity(UUID id, String name, String version) {
        return ComponentEntity.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }

    @Test
    @DisplayName("save should map, persist and return mapped model")
    void save_shouldMapPersistAndReturnMappedModel() {
        ChangeComponent input = model(null, "core-api", "1.0.0");
        UUID id = UUID.randomUUID();
        ComponentEntity mappedEntity = entity(null, "core-api", "1.0.0");
        ComponentEntity savedEntity = entity(id, "core-api", "1.0.0");
        ChangeComponent expected = model(id, "core-api", "1.0.0");

        when(componentMapper.fromModelTpEntity(input)).thenReturn(mappedEntity);
        when(componentRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(componentMapper.fromEntityToModel(savedEntity)).thenReturn(expected);

        ChangeComponent result = componentRepositoryAdapter.save(input);

        assertThat(result).isEqualTo(expected);
        verify(componentMapper, times(1)).fromModelTpEntity(input);
        verify(componentRepository, times(1)).save(mappedEntity);
        verify(componentMapper, times(1)).fromEntityToModel(savedEntity);
    }

    @Test
    @DisplayName("save should throw ResourceFoundException when repository returns null")
    void save_shouldThrowWhenSavedEntityIsNull() {
        ChangeComponent input = model(null, "core-api", "1.0.0");
        ComponentEntity mappedEntity = entity(null, "core-api", "1.0.0");

        when(componentMapper.fromModelTpEntity(input)).thenReturn(mappedEntity);
        when(componentRepository.save(mappedEntity)).thenReturn(null);

        ResourceFoundException ex = assertThrows(ResourceFoundException.class,
                () -> componentRepositoryAdapter.save(input));

        assertThat(ex.getMessage()).contains("entidade salva");
        verify(componentMapper, times(1)).fromModelTpEntity(input);
        verify(componentRepository, times(1)).save(mappedEntity);
        verify(componentMapper, never()).fromEntityToModel(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("remove should delegate deleteById")
    void remove_shouldDelegateDeleteById() {
        UUID id = UUID.randomUUID();

        componentRepositoryAdapter.remove(id);

        verify(componentRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("findById should return mapped model when found")
    void findById_shouldReturnMappedModelWhenFound() {
        UUID id = UUID.randomUUID();
        ComponentEntity foundEntity = entity(id, "core-api", "1.0.0");
        ChangeComponent expected = model(id, "core-api", "1.0.0");

        when(componentRepository.findById(id)).thenReturn(Optional.of(foundEntity));
        when(componentMapper.fromEntityToModel(foundEntity)).thenReturn(expected);

        ChangeComponent result = componentRepositoryAdapter.findById(id);

        assertThat(result).isEqualTo(expected);
        verify(componentRepository, times(1)).findById(id);
        verify(componentMapper, times(1)).fromEntityToModel(foundEntity);
    }

    @Test
    @DisplayName("findById should return null when not found")
    void findById_shouldReturnNullWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(componentRepository.findById(id)).thenReturn(Optional.empty());

        ChangeComponent result = componentRepositoryAdapter.findById(id);

        assertThat(result).isNull();
        verify(componentRepository, times(1)).findById(id);
        verify(componentMapper, never()).fromEntityToModel(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("findAll should return mapped list")
    void findAll_shouldReturnMappedList() {
        List<ComponentEntity> entities = List.of(
                entity(UUID.randomUUID(), "core-api", "1.0.0"),
                entity(UUID.randomUUID(), "payments", "2.0.0")
        );
        List<ChangeComponent> expected = List.of(
                model(entities.get(0).getId(), "core-api", "1.0.0"),
                model(entities.get(1).getId(), "payments", "2.0.0")
        );

        when(componentRepository.findAll()).thenReturn(entities);
        when(componentMapper.map(entities)).thenReturn(expected);

        List<ChangeComponent> result = componentRepositoryAdapter.findAll();

        assertThat(result).isEqualTo(expected);
        verify(componentRepository, times(1)).findAll();
        verify(componentMapper, times(1)).map(entities);
    }

    @Test
    @DisplayName("update should save existing entity and return mapped model when id exists")
    void update_shouldSaveAndReturnMappedModelWhenIdExists() {
        UUID id = UUID.randomUUID();
        ComponentEntity existing = entity(id, "core-api", "1.0.0");
        ChangeComponent patch = model(null, "core-api-v2", "2.0.0");
        ComponentEntity saved = entity(id, "core-api", "1.0.0");
        ChangeComponent expected = model(id, "core-api", "1.0.0");

        when(componentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(componentRepository.save(existing)).thenReturn(saved);
        when(componentMapper.fromEntityToModel(saved)).thenReturn(expected);

        ChangeComponent result = componentRepositoryAdapter.update(id, patch);

        assertThat(result).isEqualTo(expected);
        verify(componentRepository, times(1)).findById(id);
        verify(componentRepository, times(1)).save(existing);
        verify(componentMapper, times(1)).fromEntityToModel(saved);
        verify(componentMapper, never()).fromModelTpEntity(patch);
    }

    @Test
    @DisplayName("update should return null when id does not exist")
    void update_shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        ChangeComponent patch = model(null, "core-api-v2", "2.0.0");

        when(componentRepository.findById(id)).thenReturn(Optional.empty());

        ChangeComponent result = componentRepositoryAdapter.update(id, patch);

        assertThat(result).isNull();
        verify(componentRepository, times(1)).findById(id);
        verify(componentRepository, never()).save(org.mockito.ArgumentMatchers.any(ComponentEntity.class));
        verify(componentMapper, never()).fromEntityToModel(org.mockito.ArgumentMatchers.any());
    }
}

