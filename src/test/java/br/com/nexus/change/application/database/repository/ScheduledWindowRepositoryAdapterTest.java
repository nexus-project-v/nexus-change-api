package br.com.nexus.change.application.database.repository;

import br.com.nexus.change.application.database.mapper.ScheduledWindowMapper;
import br.com.nexus.change.commons.exception.ResourceNotRemoveException;
import br.com.nexus.change.core.domain.schedule.ScheduledWindow;
import br.com.nexus.change.infrastructure.entity.change.ChangeEntity;
import br.com.nexus.change.infrastructure.entity.scheduled.ScheduledWindowEntity;
import br.com.nexus.change.infrastructure.repository.ScheduledWindowRepository;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledWindowRepositoryAdapterTest {

    @Mock
    private ScheduledWindowRepository scheduledWindowRepository;

    @Mock
    private ScheduledWindowMapper scheduledWindowMapper;

    @InjectMocks
    private ScheduledWindowRepositoryAdapter scheduledWindowRepositoryAdapter;

    private ScheduledWindow model(UUID id, UUID changeId, String responsible) {
        return ScheduledWindow.builder()
                .id(id)
                .responsible(responsible)
                .start(LocalDateTime.of(2026, 3, 25, 22, 0))
                .end(LocalDateTime.of(2026, 3, 26, 0, 0))
                .changeId(changeId)
                .build();
    }

    private ScheduledWindowEntity entity(UUID id, UUID changeId, String responsible) {
        return ScheduledWindowEntity.builder()
                .id(id)
                .responsible(responsible)
                .start(LocalDateTime.of(2026, 3, 25, 22, 0))
                .end(LocalDateTime.of(2026, 3, 26, 0, 0))
                .changeEntity(ChangeEntity.builder().id(changeId).build())
                .build();
    }

    @Test
    @DisplayName("save should map, persist and return mapped model")
    void save_shouldMapPersistAndReturnMappedModel() {
        UUID changeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        ScheduledWindow input = model(null, changeId, "time-infra@nexus.com");
        ScheduledWindowEntity mapped = entity(null, changeId, "time-infra@nexus.com");
        ScheduledWindowEntity saved = entity(id, changeId, "time-infra@nexus.com");
        ScheduledWindow expected = model(id, changeId, "time-infra@nexus.com");

        when(scheduledWindowMapper.fromModelTpEntity(input)).thenReturn(mapped);
        when(scheduledWindowRepository.save(mapped)).thenReturn(saved);
        when(scheduledWindowMapper.fromEntityToModel(saved)).thenReturn(expected);

        ScheduledWindow result = scheduledWindowRepositoryAdapter.save(input);

        assertThat(result).isEqualTo(expected);
        verify(scheduledWindowMapper, times(1)).fromModelTpEntity(input);
        verify(scheduledWindowRepository, times(1)).save(mapped);
        verify(scheduledWindowMapper, times(1)).fromEntityToModel(saved);
    }

    @Test
    @DisplayName("save should return null when repository returns null")
    void save_shouldReturnNullWhenRepositoryReturnsNull() {
        UUID changeId = UUID.randomUUID();
        ScheduledWindow input = model(null, changeId, "time-infra@nexus.com");
        ScheduledWindowEntity mapped = entity(null, changeId, "time-infra@nexus.com");

        when(scheduledWindowMapper.fromModelTpEntity(input)).thenReturn(mapped);
        when(scheduledWindowRepository.save(mapped)).thenReturn(null);

        ScheduledWindow result = scheduledWindowRepositoryAdapter.save(input);

        assertThat(result).isNull();
        verify(scheduledWindowMapper, times(1)).fromModelTpEntity(input);
        verify(scheduledWindowRepository, times(1)).save(mapped);
        verify(scheduledWindowMapper, never()).fromEntityToModel(any(ScheduledWindowEntity.class));
    }

    @Test
    @DisplayName("save should return null when saved responsible is null")
    void save_shouldReturnNullWhenSavedResponsibleIsNull() {
        UUID changeId = UUID.randomUUID();
        ScheduledWindow input = model(null, changeId, "time-infra@nexus.com");
        ScheduledWindowEntity mapped = entity(null, changeId, "time-infra@nexus.com");
        ScheduledWindowEntity savedWithoutResponsible = entity(UUID.randomUUID(), changeId, null);

        when(scheduledWindowMapper.fromModelTpEntity(input)).thenReturn(mapped);
        when(scheduledWindowRepository.save(mapped)).thenReturn(savedWithoutResponsible);

        ScheduledWindow result = scheduledWindowRepositoryAdapter.save(input);

        assertThat(result).isNull();
        verify(scheduledWindowMapper, times(1)).fromModelTpEntity(input);
        verify(scheduledWindowRepository, times(1)).save(mapped);
        verify(scheduledWindowMapper, never()).fromEntityToModel(any(ScheduledWindowEntity.class));
    }

    @Test
    @DisplayName("remove should return true when delete succeeds")
    void remove_shouldReturnTrueWhenDeleteSucceeds() {
        UUID id = UUID.randomUUID();

        boolean result = scheduledWindowRepositoryAdapter.remove(id);

        assertThat(result).isTrue();
        verify(scheduledWindowRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("remove should return false when repository throws ResourceNotRemoveException")
    void remove_shouldReturnFalseWhenRepositoryThrowsResourceNotRemoveException() {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotRemoveException("cannot remove")).when(scheduledWindowRepository).deleteById(id);

        boolean result = scheduledWindowRepositoryAdapter.remove(id);

        assertThat(result).isFalse();
        verify(scheduledWindowRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("findById should return mapped model when found")
    void findById_shouldReturnMappedModelWhenFound() {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ScheduledWindowEntity found = entity(id, changeId, "time-infra@nexus.com");
        ScheduledWindow expected = model(id, changeId, "time-infra@nexus.com");

        when(scheduledWindowRepository.findById(id)).thenReturn(Optional.of(found));
        when(scheduledWindowMapper.fromEntityToModel(found)).thenReturn(expected);

        ScheduledWindow result = scheduledWindowRepositoryAdapter.findById(id);

        assertThat(result).isEqualTo(expected);
        verify(scheduledWindowRepository, times(1)).findById(id);
        verify(scheduledWindowMapper, times(1)).fromEntityToModel(found);
    }

    @Test
    @DisplayName("findById should return null when id does not exist")
    void findById_shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(scheduledWindowRepository.findById(id)).thenReturn(Optional.empty());

        ScheduledWindow result = scheduledWindowRepositoryAdapter.findById(id);

        assertThat(result).isNull();
        verify(scheduledWindowRepository, times(1)).findById(id);
        verify(scheduledWindowMapper, never()).fromEntityToModel(any(ScheduledWindowEntity.class));
    }

    @Test
    @DisplayName("findAll should return mapped list")
    void findAll_shouldReturnMappedList() {
        List<ScheduledWindowEntity> entities = List.of(
                entity(UUID.randomUUID(), UUID.randomUUID(), "time-infra@nexus.com"),
                entity(UUID.randomUUID(), UUID.randomUUID(), "time-security@nexus.com")
        );
        List<ScheduledWindow> expected = List.of(
                model(entities.get(0).getId(), entities.get(0).getChangeEntity().getId(), "time-infra@nexus.com"),
                model(entities.get(1).getId(), entities.get(1).getChangeEntity().getId(), "time-security@nexus.com")
        );

        when(scheduledWindowRepository.findAll()).thenReturn(entities);
        when(scheduledWindowMapper.map(entities)).thenReturn(expected);

        List<ScheduledWindow> result = scheduledWindowRepositoryAdapter.findAll();

        assertThat(result).isEqualTo(expected);
        verify(scheduledWindowRepository, times(1)).findAll();
        verify(scheduledWindowMapper, times(1)).map(entities);
    }

    @Test
    @DisplayName("update should save existing entity and return mapped model when id exists")
    void update_shouldSaveExistingAndReturnMappedModelWhenIdExists() {
        UUID id = UUID.randomUUID();
        UUID changeId = UUID.randomUUID();
        ScheduledWindow patch = model(null, changeId, "time-new@nexus.com");
        ScheduledWindowEntity existing = entity(id, changeId, "time-infra@nexus.com");
        ScheduledWindowEntity saved = entity(id, changeId, "time-infra@nexus.com");
        ScheduledWindow expected = model(id, changeId, "time-infra@nexus.com");

        when(scheduledWindowRepository.findById(id)).thenReturn(Optional.of(existing));
        when(scheduledWindowRepository.save(existing)).thenReturn(saved);
        when(scheduledWindowMapper.fromEntityToModel(saved)).thenReturn(expected);

        ScheduledWindow result = scheduledWindowRepositoryAdapter.update(id, patch);

        assertThat(result).isEqualTo(expected);
        verify(scheduledWindowRepository, times(1)).findById(id);
        verify(scheduledWindowRepository, times(1)).save(existing);
        verify(scheduledWindowMapper, times(1)).fromEntityToModel(saved);
        verify(scheduledWindowMapper, never()).fromModelTpEntity(patch);
    }

    @Test
    @DisplayName("update should return null when id does not exist")
    void update_shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        ScheduledWindow patch = model(null, UUID.randomUUID(), "time-new@nexus.com");

        when(scheduledWindowRepository.findById(id)).thenReturn(Optional.empty());

        ScheduledWindow result = scheduledWindowRepositoryAdapter.update(id, patch);

        assertThat(result).isNull();
        verify(scheduledWindowRepository, times(1)).findById(id);
        verify(scheduledWindowRepository, never()).save(any(ScheduledWindowEntity.class));
        verify(scheduledWindowMapper, never()).fromEntityToModel(any(ScheduledWindowEntity.class));
    }
}

