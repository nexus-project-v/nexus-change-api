package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ChangeLogMapper;
import br.com.nexus.change.core.domain.change.ChangeLog;
import br.com.nexus.change.infrastructure.entity.change.ChangeLogEntity;
import br.com.nexus.change.infrastructure.entity.change.ChangeStatus;
import br.com.nexus.change.infrastructure.repository.ChangeLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
class ChangeLogRepositoryAdapterTest {

    @Mock
    private ChangeLogRepository changeLogRepository;

    @Mock
    private ChangeLogMapper changeLogMapper;

    @InjectMocks
    private ChangeLogRepositoryAdapter changeLogRepositoryAdapter;

    private ChangeLog model(UUID id, UUID changeId, String changeStatus) {
        return ChangeLog.builder()
                .id(id)
                .changeId(changeId)
                .changeStatus(changeStatus)
                .createdDate(LocalDateTime.of(2026, 3, 24, 14, 0))
                .build();
    }

    private ChangeLogEntity entity(UUID id, ChangeStatus changeStatus) {
        return ChangeLogEntity.builder()
                .id(id)
                .changeStatus(changeStatus)
                .build();
    }

    @Test
    @DisplayName("save should map, persist and return mapped model")
    void save_shouldMapPersistAndReturnMappedModel() {
        UUID changeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        ChangeLog input = model(null, changeId, ChangeStatus.CREATED.name());
        ChangeLogEntity mappedEntity = entity(null, ChangeStatus.CREATED);
        ChangeLogEntity savedEntity = entity(id, ChangeStatus.CREATED);
        ChangeLog expected = model(id, changeId, ChangeStatus.CREATED.name());

        when(changeLogMapper.fromModelTpEntity(input)).thenReturn(mappedEntity);
        when(changeLogRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(changeLogMapper.fromEntityToModel(savedEntity)).thenReturn(expected);

        ChangeLog result = changeLogRepositoryAdapter.save(input);

        assertThat(result).isEqualTo(expected);
        verify(changeLogMapper, times(1)).fromModelTpEntity(input);
        verify(changeLogRepository, times(1)).save(mappedEntity);
        verify(changeLogMapper, times(1)).fromEntityToModel(savedEntity);
    }

    @Test
    @DisplayName("save should return null when mapper returns null entity")
    void save_shouldReturnNullWhenMapperReturnsNullEntity() {
        ChangeLog input = model(null, UUID.randomUUID(), ChangeStatus.CREATED.name());

        when(changeLogMapper.fromModelTpEntity(input)).thenReturn(null);

        ChangeLog result = changeLogRepositoryAdapter.save(input);

        assertThat(result).isNull();
        verify(changeLogMapper, times(1)).fromModelTpEntity(input);
        verify(changeLogRepository, never()).save(any(ChangeLogEntity.class));
        verify(changeLogMapper, never()).fromEntityToModel(any(ChangeLogEntity.class));
    }

    @Test
    @DisplayName("save should return null when repository returns null")
    void save_shouldReturnNullWhenRepositoryReturnsNull() {
        ChangeLog input = model(null, UUID.randomUUID(), ChangeStatus.CREATED.name());
        ChangeLogEntity mappedEntity = entity(null, ChangeStatus.CREATED);

        when(changeLogMapper.fromModelTpEntity(input)).thenReturn(mappedEntity);
        when(changeLogRepository.save(mappedEntity)).thenReturn(null);
        when(changeLogMapper.fromEntityToModel(null)).thenReturn(null);

        ChangeLog result = changeLogRepositoryAdapter.save(input);

        assertThat(result).isNull();
        verify(changeLogMapper, times(1)).fromModelTpEntity(input);
        verify(changeLogRepository, times(1)).save(mappedEntity);
        verify(changeLogMapper, times(1)).fromEntityToModel(null);
    }

    @Test
    @DisplayName("findByChangeId should return mapped list")
    void findByChangeId_shouldReturnMappedList() {
        UUID changeId = UUID.randomUUID();
        List<ChangeLogEntity> entities = List.of(
                entity(UUID.randomUUID(), ChangeStatus.CREATED),
                entity(UUID.randomUUID(), ChangeStatus.DEPLOYED)
        );
        List<ChangeLog> expected = List.of(
                model(entities.get(0).getId(), changeId, ChangeStatus.CREATED.name()),
                model(entities.get(1).getId(), changeId, ChangeStatus.DEPLOYED.name())
        );

        when(changeLogRepository.findByChangeEntityId(changeId)).thenReturn(entities);
        when(changeLogMapper.map(entities)).thenReturn(expected);

        List<ChangeLog> result = changeLogRepositoryAdapter.findByChangeId(changeId);

        assertThat(result).isEqualTo(expected);
        verify(changeLogRepository, times(2)).findByChangeEntityId(changeId);
        verify(changeLogMapper, times(1)).map(entities);
    }

    @Test
    @DisplayName("findByChangeId should return empty list when repository returns empty")
    void findByChangeId_shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
        UUID changeId = UUID.randomUUID();
        List<ChangeLogEntity> entities = List.of();
        List<ChangeLog> expected = List.of();

        when(changeLogRepository.findByChangeEntityId(changeId)).thenReturn(entities);
        when(changeLogMapper.map(entities)).thenReturn(expected);

        List<ChangeLog> result = changeLogRepositoryAdapter.findByChangeId(changeId);

        assertThat(result).isEmpty();
        verify(changeLogRepository, times(2)).findByChangeEntityId(changeId);
        verify(changeLogMapper, times(1)).map(entities);
    }

    @Test
    @DisplayName("findById should return mapped model when found")
    void findById_shouldReturnMappedModelWhenFound() {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ChangeLogEntity foundEntity = entity(id, ChangeStatus.CREATED);
        ChangeLog expected = model(id, changeId, ChangeStatus.CREATED.name());

        when(changeLogRepository.findById(id)).thenReturn(Optional.of(foundEntity));
        when(changeLogMapper.fromEntityToModel(foundEntity)).thenReturn(expected);

        ChangeLog result = changeLogRepositoryAdapter.findById(id);

        assertThat(result).isEqualTo(expected);
        verify(changeLogRepository, times(1)).findById(id);
        verify(changeLogMapper, times(1)).fromEntityToModel(foundEntity);
    }

    @Test
    @DisplayName("findById should return null when id does not exist")
    void findById_shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(changeLogRepository.findById(id)).thenReturn(Optional.empty());

        ChangeLog result = changeLogRepositoryAdapter.findById(id);

        assertThat(result).isNull();
        verify(changeLogRepository, times(1)).findById(id);
        verify(changeLogMapper, never()).fromEntityToModel(any(ChangeLogEntity.class));
    }
}

