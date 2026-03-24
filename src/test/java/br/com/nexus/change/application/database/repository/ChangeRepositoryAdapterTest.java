package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ChangeMapper;
import br.com.nexus.change.core.domain.change.Change;
import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.entity.change.ChangeType;
import br.com.nexus.change.infrastructure.entity.change.Environment;
import br.com.nexus.change.infrastructure.entity.component.ComponentEntity;
import br.com.nexus.change.infrastructure.repository.ChangeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeRepositoryAdapterTest {

    @Mock
    private ChangeRepository changeRepository;

    @Mock
    private ChangeMapper changeMapper;

    @InjectMocks
    private ChangeRepositoryAdapter changeRepositoryAdapter;

    private Change model(UUID id, String title, String status) {
        return Change.builder()
                .id(id)
                .title(title)
                .description("Descricao de " + title)
                .componentId(UUID.randomUUID())
                .environment("PROD")
                .changeType("NORMAL")
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    private ChangeEntity entity(UUID id, String title, ChangeStatus status) {
        return ChangeEntity.builder()
                .id(id)
                .title(title)
                .description("Descricao de " + title)
                .componentEntity(ComponentEntity.builder().id(UUID.randomUUID()).name("core-api").build())
                .environment(Environment.PROD)
                .changeType(ChangeType.NORMAL)
                .changeStatus(status)
                .requestBy("user@nexus.com")
                .build();
    }

    @Test
    @DisplayName("save should map, persist and return mapped model")
    void save_shouldMapPersistAndReturnMappedModel() {
        Change input = model(null, "Deploy API", "DRAFT");
        UUID id = UUID.randomUUID();
        ChangeEntity mappedEntity = entity(null, "Deploy API", ChangeStatus.DRAFT);
        ChangeEntity savedEntity = entity(id, "Deploy API", ChangeStatus.DRAFT);
        Change expected = model(id, "Deploy API", "DRAFT");

        when(changeMapper.fromModelTpEntity(input)).thenReturn(mappedEntity);
        when(changeRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(changeMapper.fromEntityToModel(savedEntity)).thenReturn(expected);

        Change result = changeRepositoryAdapter.save(input);

        assertThat(result).isEqualTo(expected);
        verify(changeMapper, times(1)).fromModelTpEntity(input);
        verify(changeRepository, times(1)).save(mappedEntity);
        verify(changeMapper, times(1)).fromEntityToModel(savedEntity);
    }

    @Test
    @DisplayName("save should return null when mapper returns null entity")
    void save_shouldReturnNullWhenMapperReturnsNullEntity() {
        Change input = model(null, "Deploy API", "DRAFT");

        when(changeMapper.fromModelTpEntity(input)).thenReturn(null);

        Change result = changeRepositoryAdapter.save(input);

        assertThat(result).isNull();
        verify(changeMapper, times(1)).fromModelTpEntity(input);
        verify(changeRepository, never()).save(any(ChangeEntity.class));
        verify(changeMapper, never()).fromEntityToModel(any(ChangeEntity.class));
    }

    @Test
    @DisplayName("remove should delegate deleteById")
    void remove_shouldDelegateDeleteById() {
        UUID id = UUID.randomUUID();

        changeRepositoryAdapter.remove(id);

        verify(changeRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("findById should return mapped model when found")
    void findById_shouldReturnMappedModelWhenFound() {
        UUID id = UUID.randomUUID();
        ChangeEntity found = entity(id, "Deploy API", ChangeStatus.CREATED);
        Change expected = model(id, "Deploy API", "CREATED");

        when(changeRepository.findById(id)).thenReturn(Optional.of(found));
        when(changeMapper.fromEntityToModel(found)).thenReturn(expected);

        Change result = changeRepositoryAdapter.findById(id);

        assertThat(result).isEqualTo(expected);
        verify(changeRepository, times(1)).findById(id);
        verify(changeMapper, times(1)).fromEntityToModel(found);
    }

    @Test
    @DisplayName("findById should return null when id does not exist")
    void findById_shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(changeRepository.findById(id)).thenReturn(Optional.empty());

        Change result = changeRepositoryAdapter.findById(id);

        assertThat(result).isNull();
        verify(changeRepository, times(1)).findById(id);
        verify(changeMapper, never()).fromEntityToModel(any(ChangeEntity.class));
    }

    @Test
    @DisplayName("findAll should return mapped list")
    void findAll_shouldReturnMappedList() {
        List<ChangeEntity> entities = List.of(
                entity(UUID.randomUUID(), "Deploy API", ChangeStatus.CREATED),
                entity(UUID.randomUUID(), "Rollback", ChangeStatus.ROLLBACK)
        );
        List<Change> expected = List.of(
                model(entities.get(0).getId(), "Deploy API", "CREATED"),
                model(entities.get(1).getId(), "Rollback", "ROLLBACK")
        );

        when(changeRepository.findAll()).thenReturn(entities);
        when(changeMapper.map(entities)).thenReturn(expected);

        List<Change> result = changeRepositoryAdapter.findAll();

        assertThat(result).isEqualTo(expected);
        verify(changeRepository, times(1)).findAll();
        verify(changeMapper, times(1)).map(entities);
    }

    @Test
    @DisplayName("update should apply changes, save and return mapped model when id exists")
    void update_shouldApplyChangesSaveAndReturnMappedModelWhenIdExists() {
        UUID id = UUID.randomUUID();
        Change inputPatch = model(null, "New Title", "VALIDATED");

        ChangeEntity existing = entity(id, "Old Title", ChangeStatus.CREATED);
        ChangeEntity mappedPatch = entity(null, "New Title", ChangeStatus.VALIDATED);
        ChangeEntity saved = entity(id, "New Title", ChangeStatus.VALIDATED);
        Change expected = model(id, "New Title", "VALIDATED");

        when(changeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(changeMapper.fromModelTpEntity(inputPatch)).thenReturn(mappedPatch);
        when(changeRepository.save(existing)).thenReturn(saved);
        when(changeMapper.fromEntityToModel(saved)).thenReturn(expected);

        Change result = changeRepositoryAdapter.update(id, inputPatch);

        assertThat(result).isEqualTo(expected);
        assertThat(existing.getTitle()).isEqualTo("New Title");
        assertThat(existing.getChangeStatus()).isEqualTo(ChangeStatus.VALIDATED);

        verify(changeRepository, times(1)).findById(id);
        verify(changeMapper, times(1)).fromModelTpEntity(inputPatch);
        verify(changeRepository, times(1)).save(existing);
        verify(changeMapper, times(1)).fromEntityToModel(saved);
    }

    @Test
    @DisplayName("update should return null when id does not exist")
    void update_shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        Change inputPatch = model(null, "New Title", "VALIDATED");

        when(changeRepository.findById(id)).thenReturn(Optional.empty());

        Change result = changeRepositoryAdapter.update(id, inputPatch);

        assertThat(result).isNull();
        verify(changeRepository, times(1)).findById(id);
        verify(changeMapper, never()).fromModelTpEntity(inputPatch);
        verify(changeRepository, never()).save(any(ChangeEntity.class));
        verify(changeMapper, never()).fromEntityToModel(any(ChangeEntity.class));
    }
}

